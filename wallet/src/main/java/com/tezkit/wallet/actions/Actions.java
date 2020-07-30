package com.tezkit.wallet.actions;

import com.tezkit.wallet.updater.Updater;
import lombok.Data;

@Data
public class Actions {

    // IncCounterAction is stateless, therefore only a single instance can be reused
    private static final IncCounterAction INC_COUNTER_ACTION = new IncCounterAction();

    private Actions() { }

    /**
     * This method returns a {@link IncCounterAction}.
     * <p>
     * This action is passed to the {@link Updater} when the user clicks the button
     *
     * @return the {@code IncCounterAction}
     */
    public static IncCounterAction incCounterAction() {
        return INC_COUNTER_ACTION;
    }

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
}
