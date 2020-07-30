package com.tezkit.wallet.updater;

import com.tezkit.wallet.actions.ImportMnemonicAction;
import com.tezkit.wallet.persistence.Wallet;
import com.tezkit.wallet.persistence.WalletDAO;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.CurrentWalletState;
import com.tezkit.wallet.state.TezkitScreen;
import milfont.com.tezosj.model.TezosWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecretsUpdater {

    private final String DEFAULT_PASSWORD = "empty";
    private WalletDAO walletDAO;

    @Autowired
    public SecretsUpdater(WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

//    static public AppState choosePassword(AppState state, PasswordEnteredAction action) {
//        return state
//                .withChosenPassword(action.getPassword())
//                .withTezkitScreen(TezkitScreen.CONFIRM_PASSWORD);
//    }
//
//    public static AppState confirmPassword(AppState state, PasswordConfirmationAction action) {
//        var newState = state.withPasswordValidationMsg("");
//
//        if (action.getPassword().equals(state.getChosenPassword())) {
//            return newState.withTezkitScreen(TezkitScreen.NEW_WALLET_FROM);
//        } else {
//            return newState.withPasswordValidationMsg("Passwords don't match.");
//        }
//    }

    public AppState importMnemonic(AppState state, ImportMnemonicAction stateAction) {
        var mnemonic = stateAction.getMnemonic();
        var password = stateAction.getPassword();
        TezosWallet wallet;
        try {
            wallet = new TezosWallet(mnemonic, password);
            wallet.setProvider("https://mainnet-tezos.giganode.io");
        } catch (Exception e) {
            e.printStackTrace();
            return state;
            // TODO handle bad input
        }

        walletDAO.insertWallet(Wallet.builder().mnemonic(mnemonic).password(password).build());

        return state
                .withTezkitScreen(TezkitScreen.MAIN_VIEW)
                .withCurrentWalletState(
                        CurrentWalletState
                                .builder()
//                                .mnemonic(stateAction.getMnemonic())
                                .tezosWallet(wallet)
                                .build());
    }

    public AppState newWallet(AppState state) {
        TezosWallet wallet;
        try {
            wallet = new TezosWallet(DEFAULT_PASSWORD);
            wallet.setProvider("https://mainnet-tezos.giganode.io");
        } catch (Exception e) {
            // TODO handle properly
            e.printStackTrace();
            throw new RuntimeException();
        }

        walletDAO.insertWallet(
                Wallet.builder()
                        .mnemonic(wallet.getMnemonicWords())
                        .password(DEFAULT_PASSWORD)
                        .build());

        return state
                .withTezkitScreen(TezkitScreen.MAIN_VIEW)
                .withCurrentWalletState(CurrentWalletState
                        .builder()
                        .tezosWallet(wallet)
                        .build());
    }
}
