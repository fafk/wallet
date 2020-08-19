package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.TezkitScreen;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tezkit.wallet.state.TezkitScreen.DASHBOARD;
import static com.tezkit.wallet.utils.Strings.capsToCamelCase;

/**
 * Listen to changes in AppState screens, load new screens from FXML and show them.
 *
 * When you want to add a new screen that should be displayed within a layout template, it needs
 * to be so specified in `#loadScreen`.
 */
@Component
public class StageManager {

    private final ViewLoader viewLoader;
    private final Stage primaryStage;
    private final Selector<AppState> selector;
    private double x, y;

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
        observableScreen.addListener((o, oldValue, newScreen) -> {
            var screenFxmlFile = getFxmlFilename(newScreen);
            var root = loadScreen(newScreen, screenFxmlFile);
            var scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/tezwallet_styling.css").toExternalForm());
            primaryStage.setScene(scene);

            //drag it here
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);
            });
            primaryStage.show();
        });
    }

    private Parent loadScreen(TezkitScreen newScreen, String screenFxmlFile) {
        Parent root;

        switch (newScreen) {
            case DASHBOARD: root = loadViewInMainLayout(DASHBOARD); break;
            default: root = viewLoader.loadView(screenFxmlFile);
        }

        return root;
    }

    private Parent loadViewInMainLayout(TezkitScreen screen) {
       return viewLoader.loadInMainLayout(getFxmlFilename(screen), getControllerClass(screen));
    }

    private Class getControllerClass(TezkitScreen screen) {
        try {
            return Class.forName("com.tezkit.wallet.view." + capsToCamelCase(screen.toString()) + "View");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Controller class not found! " + e.toString());
        }
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
