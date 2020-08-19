package com.tezkit.wallet.updater;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.core.KeysAPI;
import com.tezkit.core.TezosAPI;
import com.tezkit.wallet.actions.ImportMnemonicAction;
import com.tezkit.wallet.persistence.Wallet;
import com.tezkit.wallet.persistence.WalletDAO;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.CurrentWalletState;
import com.tezkit.wallet.state.TezkitScreen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecretsUpdater {

    private final String DEFAULT_PASSWORD = "";
    private WalletDAO walletDAO;
    private Dispatcher dispatcher;
    private TezosAPI tezosAPI;

    @Autowired
    public SecretsUpdater(WalletDAO walletDAO, @Lazy Dispatcher dispatcher, TezosAPI tezosAPI) {
        this.walletDAO = walletDAO;
        this.dispatcher = dispatcher;
        this.tezosAPI = tezosAPI;
    }


    public AppState importMnemonic(AppState state, ImportMnemonicAction stateAction) {
        var mnemonic = stateAction.getMnemonic();
        var password = stateAction.getPassword();
        var keys = KeysAPI.keysFromMnemonic(List.of(mnemonic.split(" ")), password);

        walletDAO.insertWallet(Wallet.builder().mnemonic(mnemonic).password(password).build());

        return state
                .withTezkitScreen(TezkitScreen.DASHBOARD)
                .withCurrentWalletState(
                        CurrentWalletState
                                .builder()
                                .keys(keys)
                                .build());
    }

    public AppState newWallet(AppState state) {
        var mnemonic = KeysAPI.generateMnemonic();
        var keys = KeysAPI.keysFromMnemonic(mnemonic, DEFAULT_PASSWORD);

        walletDAO.insertWallet(
                Wallet.builder()
                        .mnemonic(String.join(" ", mnemonic))
                        .password(DEFAULT_PASSWORD)
                        .build());

        return state
                .withTezkitScreen(TezkitScreen.DASHBOARD)
                .withCurrentWalletState(CurrentWalletState
                        .builder()
                        .keys(keys)
                        .build());
    }

}
