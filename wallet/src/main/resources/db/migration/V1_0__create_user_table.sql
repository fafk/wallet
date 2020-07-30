CREATE TABLE wallet
(
    id       INTEGER IDENTITY NOT NULL,
    mnemonic VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
);

-- INSERT INTO wallet (mnemonic, password) VALUES ('siren dice hawk negative convince dignity twenty salad suggest hurry flight train economy sudden lizard', 'hkdo24bVxp');
