package com.tezkit.wallet.updater;

import com.tezkit.wallet.persistence.WalletDAO;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.CurrentWalletState;
import com.tezkit.wallet.state.TezkitScreen;
import milfont.com.tezosj.model.TezosWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BootstrapUpdater {

    private WalletDAO walletDAO;

    @Autowired
    public BootstrapUpdater(WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

    public AppState bootstrap() {
        var appState = AppState.create();
        var wallets = walletDAO.getWallets();

        if (wallets.isEmpty()) return appState.withTezkitScreen(TezkitScreen.NEW_WALLET_FROM);

        var rawWallet = wallets.get(0);
        TezosWallet tezosWallet = null;
        try {
            tezosWallet = new TezosWallet(rawWallet.getMnemonic(), rawWallet.getPassword());
            tezosWallet.setProvider("https://mainnet-tezos.giganode.io");
        } catch (Exception e) {
            // TODO handle properly
            e.printStackTrace();
        }
        var wallet = CurrentWalletState
                .builder()
                .tezosWallet(tezosWallet)
                .build();
        return appState
                .withTezkitScreen(TezkitScreen.MAIN_VIEW)
                .withCurrentWalletState(wallet);
    }
}
