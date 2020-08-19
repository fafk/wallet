CREATE TABLE wallet
(
    id       INTEGER IDENTITY NOT NULL,
    mnemonic VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
);

