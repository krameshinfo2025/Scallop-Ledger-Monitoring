-- Table: public.app_user

DROP TABLE IF EXISTS app_user;

CREATE TABLE IF NOT EXISTS app_user
(
    id  uuid primary key,
    username character varying(64) not null,
    password_hash character varying(100) not null,
    role character varying(20) not null,
    enabled boolean not null default true,
    created_at   timestamptz  not null default now(), 
    updated_at   timestamptz  not null default now(),
	created_by   VARCHAR(36) not null default 'SCALLOP_SL',
	updated_by   VARCHAR(36) not null default 'SCALLOP_SL',
	
    CONSTRAINT app_user_username_key UNIQUE (username),
    CONSTRAINT app_user_role_check CHECK (role::text = ANY (ARRAY['ADMIN'::character varying, 'ANALYST'::character varying, 'CLIENT_SYSTEM'::character varying]::text[]))
);

INSERT INTO app_user(id, username, password_hash, role, enabled) 
VALUES ( '94e62b3e-2e4b-4e57-a8a4-65a7b6d67e11','admin', 
         '$2a$12$YMUyk5lsS8UroyXVv2baDuRumQa1EBdSHxbWbCasx/j/8CUHnFqjG',
         'ADMIN',true);


--DROP TRIGGER IF EXISTS reject_modification ON audit_log;

--DROP TRIGGER IF EXISTS audit_log_append_only ON audit_log;

DROP TABLE IF EXISTS audit_log;

create table audit_log (
    id           uuid           primary key,
    actor        varchar(64)    not null,
    action       varchar(64)    not null,
    entity_type  varchar(40)    not null,
    entity_id    varchar(64),
    detail       varchar(2000),
    occurred_at  timestamptz    not null
);
create index audit_log_entity on audit_log (entity_type, entity_id, occurred_at desc);

-- Decisions and the audit trail are evidence: once written they may never be
-- changed or removed, even by a bug in the application.
--create function reject_modification() returns trigger
--language plpgsql as $$
--begin
--    raise exception '% is append-only: % rejected', tg_table_name, tg_op;
--end;
--$$;

--create trigger audit_log_append_only
--    before update or delete on audit_log
--    for each row execute function reject_modification();
    
    
DROP TABLE IF EXISTS ledger_entities CASCADE;

CREATE TABLE IF NOT EXISTS ledger_entities
(
    entity_id text not null,
    name text      not null,
    CONSTRAINT ledger_entities_pkey PRIMARY KEY (entity_id)
);

INSERT INTO ledger_entities VALUES('SCALLOP_SL','Scallop PVT Limited'),('SCALLOP_CA','Scallop Payments Inc'),('SCALLOP_HK','Scallop Finance'),('SCALLOP_US','Scallop Fintech Inc');



-- CIF or Customer Information File or customer id ranges from 6 to 16 digits long, varying by the specific financial institution.
-- A bank account number typically ranges from 5 to 18

DROP TABLE IF EXISTS account_holder CASCADE;


create table account_holder (
    
	id              uuid         not null default gen_random_uuid(),
	entity_id  varchar(40)    not null,
	customer_id  varchar(40)    not null,
	account_number  varchar(40)    not null,
    holder_type     varchar(16)  not null,
	account_type     varchar(16)  not null default 'MULTICURRENCY',
    display_name    text         not null,
    business_reg_no text,
    role character varying(16)  not null,
    status          varchar(16)  not null default 'ACTIVE',
    created_at      timestamptz  not null default now(),
	updated_at   timestamptz  not null default now(),
	created_by   VARCHAR(36) not null default 'SCALLOP_SL',
	updated_by   VARCHAR(36) not null default 'SCALLOP_SL',
	
	UNIQUE(customer_id,account_number),
    constraint account_holder_pk           primary key (id),
    constraint account_holder_type_valid   check (holder_type in ('INDIVIDUAL', 'BUSINESS')),
	constraint account_account_type_valid   check (account_type in ('SAVING', 'CHECKING','SALARY','FIXEDDEPOSIT','RECURINGDEPOSIT','HIGHYIELD','MULTICURRENCY','OFFSHORE','BUSINESSCURRENT')),
    constraint account_holder_status_valid check (status in ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    -- A business must carry a registration number; an individual must not.
    constraint account_holder_reg_no_shape check (
        (holder_type = 'BUSINESS'   and business_reg_no is not null) or (holder_type = 'INDIVIDUAL' and business_reg_no is null)),
    -- A business must carry a role OWNER,ACCOUNTANT,VIEWER and an individual must carry a role OWNER.
    constraint holder_membership_role_valid CHECK (role::text = ANY (ARRAY['OWNER'::character varying, 'ACCOUNTANT'::character varying, 'VIEWER'::character varying]::text[]))
  );

comment on table account_holder is
    'The party that owns wallets and that the bank owes money to.';

    
-- V3: wallets, and the chart of accounts they hang off.
--
-- A wallet is holder x currency. Its balance column is a cached projection of
-- the ledger, not the source of truth - reconciliation in step 8 proves the
-- two agree by recomputing from journal lines.
DROP TABLE IF EXISTS wallet;
    
create table wallet (

    id            uuid           not null default gen_random_uuid(),
    holder_id     uuid           not null,
    currency_code varchar(10)    not null,
	display_scale smallint not null,
	kind         varchar(10)  not null,
    status       varchar(16)    not null default 'ACTIVE',
    balance      numeric(38,18) not null default 0,    
    created_at    timestamptz    not null default now(),
	updated_at   timestamptz  not null default now(),
	created_by   VARCHAR(36) not null default 'SCALLOP_SL',
	updated_by   VARCHAR(36) not null default 'SCALLOP_SL',
	version       bigint         not null default 0,

    constraint wallet_pk                  primary key (id),
    constraint wallet_holder_fk           foreign key (holder_id)     references account_holder (id),
    constraint wallet_currency_fk         foreign key (currency_code) references ledger_assets (currency),
    constraint wallet_status_valid        check (status in ('ACTIVE', 'FROZEN', 'CLOSED')),
    constraint currency_kind_valid   check (kind in ('FIAT', 'CRYPTO','OTHER')),
    constraint currency_scale_range  check (display_scale between 0 and 18),
    constraint currency_code_format  check (currency_code ~ '^[A-Z]{3,10}$'),
    constraint wallet_balance_not_negative check (balance >= 0),
    -- One wallet per currency per holder.
    constraint wallet_holder_currency_unique unique (holder_id, currency_code),
    -- Not redundant: this is the target of the composite foreign key below,
    -- which is what stops a ledger account being attached to a wallet of a
    -- different currency.
    constraint wallet_id_currency_unique     unique (id, currency_code)
);

comment on column wallet.balance is
    'Cached projection of the ledger, maintained by the posting service. The journal is the source of truth.';
comment on column wallet.version is
    'Optimistic lock. Concurrent debits on the same wallet collide here rather than silently interleaving.';

create index wallet_holder_idx on wallet (holder_id);

DROP TABLE IF EXISTS financial_journals CASCADE;

CREATE TABLE financial_journals (

 journal_id VARCHAR(255) primary key,
 timestamp timestamptz    not null,
 user_id VARCHAR(255) not null,
 tier_profile TEXT not null,
 transaction_type VARCHAR(64) not null,
 base_currency VARCHAR(16) not null,
 network VARCHAR(32), 
 target_currency VARCHAR(16),
 gross_amount NUMERIC(18,6) not null, 
 fee_breakdown TEXT not null,
 net_processed_amount NUMERIC(18,6) not null,
 oracle_spot_rate NUMERIC(18,6) default 1.000000,
 source_rail_address VARCHAR(255), 
 counterparty_endpoint VARCHAR(255),
 created_at timestamptz default now(),
 updated_at   timestamptz  not null default now(),
 created_by   VARCHAR(36) not null default 'SCALLOP_SL',
 updated_by   VARCHAR(36) not null default 'SCALLOP_SL'
);

DROP TABLE IF EXISTS journal_double_entry_postings CASCADE;

CREATE TABLE journal_double_entry_postings (

 id BIGSERIAL primary key,
 journal_id VARCHAR(255) REFERENCES financial_journals(journal_id) ON DELETE CASCADE,
 account VARCHAR(255) not null, 
 currency VARCHAR(16) not null,
 posting_type VARCHAR(2) not null CHECK(posting_type IN ('DR','CR')),
 amount NUMERIC(18,6) not null, 
 description TEXT,
 posting_time timestamptz  not null
 
);
CREATE INDEX idx_postings_journal_id ON journal_double_entry_postings(journal_id);
CREATE INDEX idx_postings_account ON journal_double_entry_postings(account);
CREATE INDEX idx_postings_currency_type ON journal_double_entry_postings(currency,posting_type);
CREATE INDEX idx_postings_time ON journal_double_entry_postings(posting_time,id);
CREATE INDEX idx_journals_timestamp ON financial_journals(timestamp,journal_id);


DROP TABLE IF EXISTS ledger_accounts CASCADE;

CREATE TABLE ledger_accounts(

 entity_id TEXT not null REFERENCES ledger_entities,
 account VARCHAR(255) not null,
 currency VARCHAR(16) not null,
 owner_id TEXT, 
 rail_reference TEXT,
 PRIMARY KEY(entity_id,account,currency),
 CHECK(account ~ '^(LIABILITY|CLEARING|REVENUE|EXPENSE):[A-Z0-9_]+([ \[].*)?$')
 
);

DROP TABLE IF EXISTS journal_control CASCADE;

CREATE TABLE journal_control(

 journal_id VARCHAR(255) PRIMARY KEY REFERENCES financial_journals,
 entity_id TEXT NOT NULL REFERENCES ledger_entities,
 actor TEXT NOT NULL, 
 request_key TEXT NOT NULL,
 deterministic_id CHAR(32) NOT NULL,
 request_hash CHAR(64) NOT NULL,
 original_journal_id VARCHAR(255) REFERENCES financial_journals,
 event_kind TEXT NOT NULL CHECK(event_kind IN ('POST','HOLD','CAPTURE','RELEASE','REVERSAL')),
 UNIQUE(entity_id,request_key), 
 UNIQUE(entity_id,deterministic_id)
 
);

CREATE INDEX idx_control_entity ON journal_control(entity_id,journal_id);
CREATE UNIQUE INDEX one_hold_resolution ON journal_control(original_journal_id) WHERE event_kind IN ('CAPTURE','RELEASE');
CREATE UNIQUE INDEX one_full_reversal ON journal_control(original_journal_id) WHERE event_kind='REVERSAL';


DROP TABLE IF EXISTS journal_seals CASCADE;

CREATE TABLE journal_seals(

 journal_id VARCHAR(255) PRIMARY KEY REFERENCES financial_journals,
 canonical_payload TEXT NOT NULL, sha256 CHAR(64) NOT NULL,
 sealed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
 
);

DROP TABLE IF EXISTS fx_quotes CASCADE;

CREATE TABLE fx_quotes(
 id           uuid           primary key,
 quote_id     TEXT           NOT NULL,
 entity_id TEXT NOT NULL REFERENCES ledger_entities,
 base_currency VARCHAR(16) NOT NULL REFERENCES ledger_assets,
 target_currency VARCHAR(16) NOT NULL REFERENCES ledger_assets,
 rate TEXT NOT NULL,
 observed_at TIMESTAMPTZ NOT NULL,
 expires_at TIMESTAMPTZ NOT NULL,
 source_reference TEXT NOT NULL,
 created_by TEXT NOT NULL,
 CHECK(expires_at>observed_at),CHECK(base_currency<>target_currency)
);

DROP TABLE IF EXISTS reserve_observations CASCADE;

CREATE TABLE reserve_observations(
 observation_id UUID PRIMARY KEY,
 entity_id TEXT NOT NULL REFERENCES ledger_entities,
 account VARCHAR(255) NOT NULL,
 currency VARCHAR(16) NOT NULL,
 statement_reference TEXT NOT NULL,
 observed_at TIMESTAMPTZ NOT NULL,
 external_balance NUMERIC(18,6) NOT NULL,
 ledger_balance NUMERIC(18,6) NOT NULL,
 difference NUMERIC(18,6) NOT NULL,
 recorded_by TEXT NOT NULL,
 UNIQUE(entity_id,statement_reference,account,currency),
 FOREIGN KEY(entity_id,account,currency) REFERENCES ledger_accounts
);

