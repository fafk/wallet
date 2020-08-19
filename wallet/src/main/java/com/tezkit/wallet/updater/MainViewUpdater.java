package com.tezkit.wallet.updater;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.tezkit.core.TezosAPI;
import com.tezkit.core.TzStatsAPI;
import com.tezkit.wallet.actions.Actions;
import com.tezkit.wallet.actions.BalanceHistoryLoadedAction;
import com.tezkit.wallet.actions.BalanceLoadedAction;
import com.tezkit.wallet.actions.ExchangeRateLoadedAction;
import com.tezkit.wallet.state.AppState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class MainViewUpdater {

    private Dispatcher dispatcher;
    private TezosAPI tezosAPI;

    @Autowired
    public MainViewUpdater(@Lazy Dispatcher dispatcher, TezosAPI tezosAPI) {
        this.dispatcher = dispatcher;
        this.tezosAPI = tezosAPI;
    }

    static public AppState updateBalance(AppState state, BalanceLoadedAction action) {
        var walletState = state.getCurrentWalletState();
        var newWalletState = walletState.withBalance(action.getBalance());

        return state.withCurrentWalletState(newWalletState);
    }

    public static AppState updateChanges(AppState state, BalanceHistoryLoadedAction action) {
        var walletState = state.getCurrentWalletState();
        var newWalletState = walletState.withBalanceChanges(action.getBalanceChanges());

        return state.withCurrentWalletState(newWalletState);
    }

    public static AppState updateExchangeRate(AppState state, ExchangeRateLoadedAction stateAction) {
        return state.withExchangeRate(stateAction.getRate());
    }

    public AppState viewInitialized(AppState state) {
        var address = state.getCurrentWalletState().getKeys().getPublicKeyHash();
        tezosAPI.getBalanceAsync(address).thenAccept(
                balance -> dispatcher.dispatch(Actions.balanceLoaded(address, balance)));
        TzStatsAPI.getBalanceChangesAsync(address).thenAccept(
                changes -> dispatcher.dispatch(Actions.balanceHistoryLoaded(address, changes)));
        TzStatsAPI.getExchangeRate().thenAccept(
                rate -> dispatcher.dispatch(Actions.exchangeRateLoaded(rate)));

        return state;
    }

}
