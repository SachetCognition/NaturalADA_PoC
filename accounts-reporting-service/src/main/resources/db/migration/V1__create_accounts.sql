CREATE TABLE accounts (
    id              BIGSERIAL PRIMARY KEY,
    acc_key         VARCHAR(18) NOT NULL,
    branch          VARCHAR(3) NOT NULL,
    agent           VARCHAR(6) NOT NULL,
    policy          VARCHAR(9) NOT NULL,
    entry_type      CHAR(1),
    entry_date      INTEGER,
    deb_cred_amt    BIGINT,
    comm_amount     BIGINT,
    cash_amt        BIGINT,
    cash_date       INTEGER,
    method_coll     VARCHAR(2),
    process_mkrs    VARCHAR(2)
);

CREATE INDEX idx_accounts_acc_key ON accounts (acc_key);
