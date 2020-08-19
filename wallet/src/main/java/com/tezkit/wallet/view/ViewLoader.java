// TODO write up what this is, what setControllerFactory does and why

package com.tezkit.wallet.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ViewLoader implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Parent loadView(String fxmlPath) {
        try {
            var loader = fxmlLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot load FXML file for stage: " + fxmlPath + e.toString()); // TODO own ex class
        }
    }

    public Parent loadView(String fxmlPath, Class controller) {
        try {
            var loader = fxmlLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            loader.setController(loader.getControllerFactory().call(controller));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot load FXML file for stage: " + fxmlPath + e.toString()); // TODO own ex class
        }
    }

    public Parent loadInMainLayout(String fxmlPath, Class controller) {
        var loader = fxmlLoader();
        var mainController = applicationContext.getBean(MainView.class);
        mainController.setContentController(controller);
        mainController.setContentFxml(fxmlPath);

        loader.setLocation(getClass().getResource("/views/MainLayout.fxml"));
        loader.setController(mainController);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot load FXML file for stage: " + fxmlPath + e.toString()); // TODO own ex class
        }
    }

    private FXMLLoader fxmlLoader() {
        var loader = new FXMLLoader();

        // Tell the loader to use spring IoC container to inject dependencies intro controllers
        loader.setControllerFactory((c) -> applicationContext.getBean(c));

        return loader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
