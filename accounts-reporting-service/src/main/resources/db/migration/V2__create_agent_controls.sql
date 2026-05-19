CREATE TABLE agent_controls (
    id              BIGSERIAL PRIMARY KEY,
    main_agt_key    VARCHAR(12) NOT NULL,
    status          CHAR(1) NOT NULL,
    inspect_no      SMALLINT,
    name            VARCHAR(25)
);

CREATE INDEX idx_agent_controls_key ON agent_controls (main_agt_key);
