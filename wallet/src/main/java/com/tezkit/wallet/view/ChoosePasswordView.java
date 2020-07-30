package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.wallet.actions.PasswordConfirmationAction;
import com.tezkit.wallet.actions.PasswordEnteredAction;
import com.tezkit.wallet.state.AppState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChoosePasswordView {
    private final Dispatcher dispatcher;
    private final Selector<AppState> selector;
    @FXML
    private PasswordField txtChoosePassword;
    @FXML
    private Label lblPasswordValidationMsg;

//    @FXML
//    private Button btnConfirmPassword;

    @Autowired
    public ChoosePasswordView(Dispatcher dispatcher, Selector<AppState> selector) {
        this.dispatcher = dispatcher;
        this.selector = selector;
    }

    public void initialize() {
//        lblPasswordValidationMsg.textProperty().bind(
////                Bindings.format(
////                        "ꜩ You clicked the button %d times \uD83D",
//                        selector.select(AppState::getPasswordValidationMsg)
////                )
//        );
    }

    public void onPasswordChosen() {
        dispatcher.dispatch(new PasswordEnteredAction(txtChoosePassword.getText()));
    }


    public void onPasswordConfirm() {
       dispatcher.dispatch(new PasswordConfirmationAction(txtChoosePassword.getText()));
    }
}
