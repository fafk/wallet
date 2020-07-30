package com.tezkit.wallet.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configs {

    @Value("${css.refresh}")
    private Boolean refreshCss;

    public Boolean getRefreshCss() {
        return refreshCss;
    }

}
