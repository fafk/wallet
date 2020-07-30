package com.tezkit.wallet.updater;

import com.tezkit.wallet.state.AppState;
import milfont.com.tezosj.model.TezosWallet;

import java.util.Optional;

public class MainViewUpdater {

    static public AppState updateBalance(AppState state) {
        if (state.getCurrentWalletState() == null) return state;
        var wallet = state.getCurrentWalletState().getTezosWallet();
        if (wallet == null) return state;
        Optional<Double> balance = getBalance(wallet);
        if (balance.isEmpty()) return state;

        return state.withCurrentWalletState(
                state.getCurrentWalletState().withBalance(balance.get()));
    }

    static private Optional<Double> getBalance(TezosWallet wallet) {
        try {
            String balanceString = wallet.getBalance();
            return Optional.of(Double.parseDouble(balanceString.split(" ")[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
