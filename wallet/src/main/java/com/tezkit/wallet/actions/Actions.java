package com.tezkit.wallet.actions;

import com.tezkit.core.network.BalanceChange;
import com.tezkit.core.network.ExchangeRate;
import com.tezkit.wallet.updater.Updater;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class Actions {

    private Actions() { }

    public static AppReadyToStartAction appReadyToStart() {
        return new AppReadyToStartAction();
    }

    public static PasswordEnteredAction passwordEntered(String pwd) {
        return new PasswordEnteredAction(pwd);
    }

    public static GenerateNewWalletAction generateNewWallet() {
        return new GenerateNewWalletAction();
    }

    public static ChosenMnemonicImportAction chosenMnemonicImport() {
        return new ChosenMnemonicImportAction();
    }

    public static ImportMnemonicAction importMnemonic(String mnemonicWords, String password) {
       return new ImportMnemonicAction(mnemonicWords, password);
    }

    public static MainViewIntializedAction mainViewInitialized() {
        return new MainViewIntializedAction();
    }

    public static BalanceLoadedAction balanceLoaded(String address, BigInteger balance) {
        return new BalanceLoadedAction(address, balance);
    }

    public static BalanceHistoryLoadedAction balanceHistoryLoaded(
            String address, List<BalanceChange> changes) {
       return new BalanceHistoryLoadedAction(address, changes);
    }

    public static ExchangeRateLoadedAction exchangeRateLoaded(ExchangeRate rate) {
       return new ExchangeRateLoadedAction(rate);
    }
}
