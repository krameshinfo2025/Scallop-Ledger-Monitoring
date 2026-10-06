-- V3: beneficiaries - the external parties a holder can pay out to.
--
-- Normalised into four tables. Foreign keys point from parent to child
-- (beneficiary -> bank / account holder -> address) so the JPA side owns each
-- one-to-one and can load it lazily. Deleting is soft: status = 'DELETED'.

DROP TABLE IF EXISTS beneficiary_address CASCADE;
DROP TABLE IF EXISTS beneficiary_bank CASCADE;
DROP TABLE IF EXISTS beneficiary_account_holder CASCADE;
DROP TABLE IF EXISTS beneficiary CASCADE;


create table beneficiary_address (
    id                        uuid         not null default gen_random_uuid(),
    street_line1              varchar(140),
    street_line2              varchar(140),
    city                      varchar(80),
    state_or_province         varchar(80),
    state_region_or_province  varchar(80),
    postal_code               varchar(20),
    country_code              varchar(2),

    constraint beneficiary_address_pk           primary key (id),
    constraint beneficiary_address_country_fmt  check (country_code is null or country_code ~ '^[A-Z]{2}$')
);

create table beneficiary_bank (
    id              uuid          not null default gen_random_uuid(),
    name            varchar(140),
    address_id      uuid,
    type            varchar(32),
    account_number  varchar(40),
    routing_number  varchar(20),
    transit_number  varchar(20),
    ifsc_code       varchar(11),
    sort_code       varchar(10),
    bsb_number      varchar(10),
    ncc_number      varchar(20),
    clabe_number    varchar(18),
    bank_code       varchar(20),
    branch_code     varchar(20),
    cnaps_code      varchar(20),
    nuban_code      varchar(10),
    clearing_code   varchar(20),
    pix_code        varchar(77),
    iban            varchar(34),
    bic_swift       varchar(11),

    constraint beneficiary_bank_pk          primary key (id),
    constraint beneficiary_bank_address_fk  foreign key (address_id) references beneficiary_address (id),
    constraint beneficiary_bank_address_uq  unique (address_id),
    -- Every payout rail needs at least one of these to address the account.
    constraint beneficiary_bank_account_id  check (account_number is not null or iban is not null)
);

create table beneficiary_account_holder (
    id                         uuid          not null default gen_random_uuid(),
    type                       varchar(16)   not null,
    first_name                 varchar(80),
    last_name                  varchar(80),
    phone                      varchar(20),
    email                      varchar(254),
    date_of_birth              date,
    address_id                 uuid,
    citizenship                varchar(2),
    business_name              varchar(140),
    tax_identification_number  varchar(40),

    constraint beneficiary_holder_pk          primary key (id),
    constraint beneficiary_holder_address_fk  foreign key (address_id) references beneficiary_address (id),
    constraint beneficiary_holder_address_uq  unique (address_id),
    constraint beneficiary_holder_type_valid  check (type in ('INDIVIDUAL', 'BUSINESS')),
    -- A business is named by business_name; an individual by first and last name.
    constraint beneficiary_holder_name_shape  check (
        (type = 'BUSINESS'   and business_name is not null) or
        (type = 'INDIVIDUAL' and first_name is not null and last_name is not null))
);

create table beneficiary (
    id                 uuid          not null default gen_random_uuid(),
    beneficiary_type   varchar(32)   not null,
    is_third_party     boolean       not null default false,
    currency_code      varchar(10)   not null,
    bank_id            uuid          not null,
    account_holder_id  uuid          not null,
    reference_id       uuid,
    status             varchar(16)   not null default 'ACTIVE',
    created_at         timestamptz   not null default now(),
    updated_at         timestamptz   not null default now(),
    created_by         varchar(36)   not null default 'SCALLOP_SL',
    updated_by         varchar(36)   not null default 'SCALLOP_SL',
    version            bigint        not null default 0,

    constraint beneficiary_pk             primary key (id),
    constraint beneficiary_bank_fk        foreign key (bank_id)           references beneficiary_bank (id),
    constraint beneficiary_holder_fk      foreign key (account_holder_id) references beneficiary_account_holder (id),
    constraint beneficiary_currency_fk    foreign key (currency_code)     references ledger_assets (currency),
    constraint beneficiary_bank_uq        unique (bank_id),
    constraint beneficiary_holder_uq      unique (account_holder_id),
    constraint beneficiary_status_valid   check (status in ('ACTIVE', 'DELETED'))
);

comment on table beneficiary is
    'An external party a holder can pay out to. Soft-deleted via status = DELETED.';
comment on column beneficiary.reference_id is
    'Opaque client-supplied reference. Not a foreign key.';

create index beneficiary_reference_idx  on beneficiary (reference_id);
create index beneficiary_status_idx     on beneficiary (status, created_at desc);
create index beneficiary_bank_acct_idx  on beneficiary_bank (account_number);
create index beneficiary_bank_iban_idx  on beneficiary_bank (iban);
create index beneficiary_holder_email_idx on beneficiary_account_holder (lower(email));
