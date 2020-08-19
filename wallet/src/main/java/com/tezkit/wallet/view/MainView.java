package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.wallet.actions.Actions;
import com.tezkit.wallet.state.AppState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import lombok.Setter;
import org.springframework.stereotype.Controller;

@Controller
public class MainView extends Parent {

    protected Dispatcher dispatcher;
    protected Selector<AppState> selector;
    protected ViewLoader viewLoader;

    @Setter
    private String contentFxml;
    @Setter
    private Class contentController;

    @FXML
    private MenuItem menuAccount, menuExit;

    @FXML
    protected ScrollPane contentArea;

    public MainView(Dispatcher dispatcher, Selector<AppState> selector, ViewLoader viewLoader) {
        this.dispatcher = dispatcher;
        this.selector = selector;
        this.viewLoader = viewLoader;
    }

    /**
     * The main view is a layout template.
     * `contentFxml` FXML is loaded into a scroll pane, controlled by `contentController`
     * It's required that these properties are set before loading the screen!
     */
    public void initialize() {
        contentArea.setContent(viewLoader.loadView(contentFxml, contentController));
        contentArea.setPannable(true);

        dispatcher.dispatch(Actions.mainViewInitialized());
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        if (event.getSource() == menuExit) {
            System.exit(0);
        }
    }

}
