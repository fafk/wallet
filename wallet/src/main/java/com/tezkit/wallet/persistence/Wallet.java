package com.tezkit.wallet.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
public class Wallet implements RowMapper<Wallet> {

    private Integer id;
    private String mnemonic;
    private String password;

    public static Wallet create() { return Wallet.builder().build(); }

    @Override
    public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Wallet
                .builder()
                .id(rs.getInt("id"))
                .mnemonic(rs.getString("mnemonic"))
                .password(rs.getString("password"))
                .build();
    }

}
