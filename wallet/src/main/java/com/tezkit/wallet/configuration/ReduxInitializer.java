package com.tezkit.wallet.configuration;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.netopyr.reduxfx.fxml.ReduxFxml;
import com.netopyr.reduxfx.fxml.Selector;
import com.netopyr.reduxfx.middleware.LoggingMiddleware;
import com.netopyr.reduxfx.store.ReduxFXStore;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.updater.Updater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ReduxInitializer {

    private ReduxFxml<AppState> reduxFxml;

    @Autowired
    public ReduxInitializer(Updater updater) {
        reduxFxml = ReduxFxml.create();
        var store = new ReduxFXStore<>(
                AppState.create().withTezkitScreen(null),
                updater::update,
                new LoggingMiddleware<>());
        reduxFxml.connect(store.getStatePublisher(), store.createActionSubscriber());
    }

    @Bean
    public Selector selector() {
        return reduxFxml;
    }

    @Bean
    public Dispatcher dispatcher() {
        return reduxFxml;
    }

}
