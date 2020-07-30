package com.tezkit.wallet.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.tezkit")
@PropertySource({ // set `tezkit_env` to `dev` in your environment variables
        "classpath:app-${tezkit.env:prod}.properties"
})
public class Beans {

    @Bean("dataSource")
    DataSource dataSource() {
        return new DriverManagerDataSource("jdbc:hsqldb:mem:mymemdb:sa");
    }

    @Bean
    @Autowired
    @DependsOn({"dataSource"})
    public Flyway flyway(DataSource dataSource) {
        return Flyway
                .configure()
                .dataSource(dataSource)
                .load();
    }

    @Bean
    @DependsOn({"dataSource"})
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
