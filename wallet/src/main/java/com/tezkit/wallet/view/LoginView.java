package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.wallet.actions.Actions;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginView {

    @FXML
    PasswordField txtEnterPassword;

    private Dispatcher dispatcher;

    @Autowired
    public LoginView(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @FXML
    private void onBtnLoginClick() {
        dispatcher.dispatch(Actions.passwordEntered(txtEnterPassword.getText()));
    }

}
