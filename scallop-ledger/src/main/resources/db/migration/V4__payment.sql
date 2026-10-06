-- V4: payments - a record of money moving from a payer to a payee.
--
-- Record only: nothing here posts journals or moves wallet balances.
-- Normalised the same way as beneficiary: the payment row owns the transaction
-- fields and points (parent -> child) at its payer, payee, amount breakdown and
-- optional additional information. An FX payment references the fx_quotes row
-- it was priced with. Deleting is soft: status = 'CANCELLED'.

create table payment_payer (
    id                uuid            not null default gen_random_uuid(),
    user_id           varchar(100)    not null,
    source_wallet_id  varchar(64),
    source_currency   varchar(10)     not null,
    source_amount     numeric(38,18)  not null,
    source_address    varchar(255),
    source_image      varchar(2048),

    constraint payment_payer_pk           primary key (id),
    constraint payment_payer_currency_fk  foreign key (source_currency) references ledger_assets (currency),
    constraint payment_payer_amount_pos   check (source_amount > 0)
);

create table payment_payee (
    id                        uuid            not null default gen_random_uuid(),
    destination_wallet_id     varchar(64),
    destination_currency      varchar(10)     not null,
    destination_amount        numeric(38,18),
    destination_address       varchar(255),
    destination_image         varchar(2048),
    is_beneficiary_in_system  boolean         not null default false,
    beneficiary_id            varchar(64),
    beneficiary_name          varchar(140),
    account_number_or_iban    varchar(40),
    code_swift_or_bic         varchar(11),
    code_routing_or_local     varchar(20),
    bank_address              varchar(500),

    constraint payment_payee_pk           primary key (id),
    constraint payment_payee_currency_fk  foreign key (destination_currency) references ledger_assets (currency),
    constraint payment_payee_amount_pos   check (destination_amount is null or destination_amount > 0),
    -- Money has to land somewhere: a wallet, a known beneficiary or a bank account.
    constraint payment_payee_destination  check (
        destination_wallet_id is not null or beneficiary_id is not null or account_number_or_iban is not null)
);

create table payment_amount (
    id                uuid            not null default gen_random_uuid(),
    principle_amount  numeric(38,18)  not null,
    fee_amount        numeric(38,18),
    network_fee       numeric(38,18),
    other_fee         numeric(38,18),
    total_fee         numeric(38,18),
    fee_in_usd        numeric(38,18),
    discount          numeric(38,18),
    net_gross_amount  numeric(38,18),
    total_amount      numeric(38,18)  not null,

    constraint payment_amount_pk            primary key (id),
    constraint payment_amount_principle_pos check (principle_amount > 0),
    constraint payment_amount_total_pos     check (total_amount > 0),
    constraint payment_amount_non_negative  check (
        coalesce(fee_amount, 0) >= 0 and coalesce(network_fee, 0) >= 0 and coalesce(other_fee, 0) >= 0 and
        coalesce(total_fee, 0) >= 0 and coalesce(fee_in_usd, 0) >= 0 and coalesce(discount, 0) >= 0 and
        coalesce(net_gross_amount, 0) >= 0)
);

create table payment_additional_info (
    id                    uuid            not null default gen_random_uuid(),
    memo                  varchar(500),
    network               varchar(32),
    provider              varchar(64),
    include_network_fee   boolean,
    fee_collected_from    varchar(32),
    fee_rule_type         varchar(32),
    fee_rule_percent      numeric(9,6),
    free_rule_amount      numeric(38,18),
    description           varchar(1000),
    reference             varchar(140),
    funding_instructions  varchar(2000),

    constraint payment_additional_info_pk  primary key (id),
    constraint payment_fee_rule_percent    check (fee_rule_percent is null or fee_rule_percent between 0 and 100)
);

create table payment (
    id                       uuid          not null default gen_random_uuid(),
    fx_quote_id              uuid,
    payer_id                 uuid          not null,
    payee_id                 uuid          not null,
    amount_id                uuid          not null,
    additional_info_id       uuid,
    external_id              varchar(120),
    transaction_id           varchar(120),
    original_transaction_id  varchar(120),
    transaction_hash         varchar(255),
    transaction_type         varchar(64)   not null,
    transaction_sub_type     varchar(64),
    transfer_type            varchar(64),
    status                   varchar(16)   not null default 'PENDING',
    is_settled               boolean       not null default false,
    is_locked                boolean       not null default false,
    created_at               timestamptz   not null default now(),
    updated_at               timestamptz   not null default now(),
    created_by               varchar(36)   not null default 'SCALLOP_SL',
    updated_by               varchar(36)   not null default 'SCALLOP_SL',
    version                  bigint        not null default 0,

    constraint payment_pk               primary key (id),
    constraint payment_fx_quote_fk      foreign key (fx_quote_id)        references fx_quotes (id),
    constraint payment_payer_fk         foreign key (payer_id)           references payment_payer (id),
    constraint payment_payee_fk         foreign key (payee_id)           references payment_payee (id),
    constraint payment_amount_fk        foreign key (amount_id)          references payment_amount (id),
    constraint payment_additional_fk    foreign key (additional_info_id) references payment_additional_info (id),
    constraint payment_payer_uq         unique (payer_id),
    constraint payment_payee_uq         unique (payee_id),
    constraint payment_amount_uq        unique (amount_id),
    constraint payment_additional_uq    unique (additional_info_id),
    constraint payment_status_valid     check (status in ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REVERSED', 'CANCELLED'))
);

comment on table payment is
    'A payment record. Does not post to the ledger. Soft-deleted via status = CANCELLED.';
comment on column payment.external_id is
    'PaymentTransaction.id as supplied by the client. Not a key.';
comment on column payment.fx_quote_id is
    'The fx_quotes row the payment was priced with; required when source and destination currencies differ.';

-- One payment per client transaction id; payments without one are not constrained.
create unique index payment_transaction_id_uq on payment (transaction_id) where transaction_id is not null;
create index payment_status_idx          on payment (status, created_at desc);
create index payment_original_tx_idx     on payment (original_transaction_id);
create index payment_fx_quote_idx        on payment (fx_quote_id);
create index payment_payer_user_idx      on payment_payer (user_id);
create index payment_payer_wallet_idx    on payment_payer (source_wallet_id);
create index payment_payee_wallet_idx    on payment_payee (destination_wallet_id);
create index payment_payee_benef_idx     on payment_payee (beneficiary_id);
