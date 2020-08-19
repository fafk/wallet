package com.tezkit.wallet.updater;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.core.KeysAPI;
import com.tezkit.core.TezosAPI;
import com.tezkit.wallet.persistence.WalletDAO;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.CurrentWalletState;
import com.tezkit.wallet.state.TezkitScreen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BootstrapUpdater {

    private WalletDAO walletDAO;

    @Autowired
    public BootstrapUpdater(WalletDAO walletDAO, @Lazy Dispatcher dispatcher, TezosAPI tezosAPI) {
        this.walletDAO = walletDAO;
    }

    public AppState bootstrap() {
        var appState = AppState.create();
        var wallets = walletDAO.getWallets();

        if (wallets.isEmpty()) return appState.withTezkitScreen(TezkitScreen.NEW_WALLET_FROM);

        var mnemonicWords = List.of(wallets.get(0).getMnemonic().split(" "));
        var password = wallets.get(0).getPassword();
        var keys = KeysAPI.keysFromMnemonic(mnemonicWords, password);
        var wallet = CurrentWalletState
                .builder()
                .keys(keys)
                .build();
        return appState
                .withTezkitScreen(TezkitScreen.DASHBOARD)
                .withCurrentWalletState(wallet);
    }

}
