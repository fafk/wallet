package com.tezkit.wallet;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.wallet.actions.Actions;
import com.tezkit.wallet.configuration.Beans;
import com.tezkit.wallet.configuration.Configs;
import com.tezkit.wallet.platforms.mac.MacOS;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {

    public static void main(String[] args) {
        if (isMac()) MacOS.init();

        launch(args);
    }

    @Getter
    private AnnotationConfigApplicationContext context;

    @Override
    public void start(Stage primaryStage) {
        var context = prepareDependencyInjection(primaryStage);

        // Perform database migrations
        context.getBean(Flyway.class).migrate();

        Dispatcher dispatcher = (Dispatcher) context.getBean("dispatcher");
        dispatcher.dispatch(Actions.appReadyToStart());

        if (context.getBean(Configs.class).getRefreshCss()) CSSFX.start();
    }

    private AnnotationConfigApplicationContext prepareDependencyInjection(Stage primaryStage) {
        // Register the primary stage as bean, so it can be injected globally
        context = new AnnotationConfigApplicationContext();
        context.getBeanFactory().registerSingleton("stage", primaryStage);

        // Load bean definition and bean scan configuration from annotated class Beans.class
        context.register(Beans.class);

        // Use new configuration
        context.refresh();

        return context;
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
    }

}
