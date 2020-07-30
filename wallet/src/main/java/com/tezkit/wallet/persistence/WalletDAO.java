package com.tezkit.wallet.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletDAO {

    private final String SQL_GET_WALLETS = "SELECT * FROM wallet";
    private final String INSERT_WALLET = "INSERT INTO wallet (mnemonic, password) VALUES (?,?)";

    JdbcTemplate jdbcTemplate;

    @Autowired
    public WalletDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Wallet> getWallets() {
        return jdbcTemplate.query(SQL_GET_WALLETS, Wallet.create());
    }

    public Boolean insertWallet(Wallet wallet) {
        return jdbcTemplate.update(INSERT_WALLET, wallet.getMnemonic(), wallet.getPassword()) > 0;
    }
}
