CREATE TABLE cent_codes (
    id              BIGSERIAL PRIMARY KEY,
    table_type      SMALLINT NOT NULL,
    date_key        INTEGER NOT NULL,
    tp_premium_1    INTEGER,
    tp_premium_2    INTEGER,
    tp_premium_3    INTEGER,
    tp_premium_4    INTEGER,
    tp_premium_5    INTEGER,
    tp_premium_6    INTEGER,
    tp_premium_7    INTEGER,
    tp_premium_8    INTEGER,
    tp_premium_9    INTEGER,
    tp_premium_10   INTEGER,
    cover_code_super VARCHAR(16) NOT NULL,
    premium         INTEGER NOT NULL,
    cov_grp_key     VARCHAR(16),
    inv_type        VARCHAR(8),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_cent_codes_cover_super ON cent_codes (cover_code_super);
CREATE INDEX idx_cent_codes_table_type_date ON cent_codes (table_type, date_key);
