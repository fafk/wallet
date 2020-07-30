package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.wallet.actions.Actions;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewWallet {

    private final Dispatcher dispatcher;

    @FXML
    TextField txtMnemonicImport;

    @FXML
    TextField txtMnemonicImportPwd;

    @Autowired
    public NewWallet(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void onMnemonicToImportConfirmed() {
        dispatcher.dispatch(Actions.importMnemonic(txtMnemonicImport.getText(), txtMnemonicImportPwd.getText()));
    }

    public void onGenerateNewWalletSelected() {
        dispatcher.dispatch(Actions.generateNewWallet());
    }

    public void onGenerateFromMnemonicSelected() {
        dispatcher.dispatch(Actions.chosenMnemonicImport());
    }

}
