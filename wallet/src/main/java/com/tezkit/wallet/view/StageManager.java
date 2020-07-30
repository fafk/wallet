package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.TezkitScreen;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tezkit.wallet.utils.Strings.capsToCamelCase;

@Component
public class StageManager {

    private final ViewLoader viewLoader;
    private final Stage primaryStage;
    private final Selector<AppState> selector;

    @Autowired
    public StageManager(ViewLoader viewLoader, Stage stage, Selector selector) {
        this.viewLoader = viewLoader;
        this.primaryStage = stage;
        this.selector = selector;

        init();
    }

    private void init() {
        primaryStage.setTitle("Tezos Wallet - TezKit");

        ObservableValue<TezkitScreen> observableScreen = selector.select(AppState::getTezkitScreen);
        observableScreen.addListener((o, oldValue, newValue) -> {
            var screenFxmlFile = getFxmlFilename(newValue);
            var root = viewLoader.loadView(screenFxmlFile);
            var scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    // The convention is that FXML filename si derived from TezkitScreen enum name
    private String getFxmlFilename(TezkitScreen screen) {
        return new StringBuilder()
                .append("/views/")
                .append(capsToCamelCase(screen.toString()))
                .append(".fxml")
                .toString();
    }

}
