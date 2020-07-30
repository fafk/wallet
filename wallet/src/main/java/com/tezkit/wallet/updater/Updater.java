package com.tezkit.wallet.updater;

import com.netopyr.reduxfx.updater.Update;
import com.tezkit.wallet.actions.*;
import com.tezkit.wallet.state.AppState;
import com.tezkit.wallet.state.TezkitScreen;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

/**
 * The {@code Updater} is the heart of every ReduxFX-application. This is where the main application logic resides.
 * <p>
 * An {@code Updater} consists of a single function ({@link #update(AppState, Object)} in this class), which takes
 * the current state (an instance of {@link AppState}) and an action and calculates the new state from that.
 * <p>
 * Please note that {@code Updater} has no internal state. Everything that is needed for {@code update} is passed in
 * the parameters. This makes it very easy to understand the code and write tests for it.
 */
@Component
@NoArgsConstructor
public class Updater {

    private BootstrapUpdater bootstrapUpdater;
    private SecretsUpdater secretsUpdater;

    @Autowired
    public Updater(BootstrapUpdater bootstrapUpdater, SecretsUpdater secretsUpdater) {
        this.bootstrapUpdater = bootstrapUpdater;
        this.secretsUpdater = secretsUpdater;
    }

    /**
     * The method {@code update} is the central piece of the application. The whole application logic is implemented
     * here.
     * <p>
     * This method takes the current state (an instance of {@link AppState}) and an action and calculates the new state
     * from that.
     * <p>
     * Please note that {@code update} does not require any internal state. Everything that is needed, is passed in the
     * parameters. Also {@code update} has no side effects. It is a pure function. This makes it very easy to understand
     * the code and write tests for it.
     * <p>
     * Also please note, that {@code AppState} is an immutable data structure. This means that {@code update} does not
     * modify the old state, but instead creates a new instance of {@code AppState}, if anything changes.
     *
     * @param state  the current {@code AppState}
     * @param action the action that needs to be performed
     * @return the new {@code AppState}
     * @throws NullPointerException if state or action are {@code null}
     */
    public Update<AppState> update(AppState state, Object action) {
        Objects.requireNonNull(state, "The parameter 'state' must not be null");
        Objects.requireNonNull(action, "The parameter 'action' must not be null");

        return Update.of(

                // This is part of Vavr'Actions pattern-matching API. It works similar to the regular switch-case
                // in Java, except that it is much more flexible and returns a value.
                // We check which of the cases is true and in that branch we specify the newState.
                Match(action).of(
                        Case($(instanceOf(AppReadyToStartAction.class)),
                            stateAction -> bootstrapUpdater.bootstrap()),

//                        Case($(instanceOf(PasswordEnteredAction.class)),
//                                stateAction -> SecretsUpdater.choosePassword(state, stateAction)),
//                        Case($(instanceOf(PasswordConfirmationAction.class)),
//                                stateAction -> SecretsUpdater.confirmPassword(state, stateAction)),

                        Case($(instanceOf(MainViewIntializedAction.class)),
                                stateAction -> MainViewUpdater.updateBalance(state)),

                        Case($(instanceOf(ChosenMnemonicImportAction.class)),
                                stateAction -> state.withTezkitScreen(TezkitScreen.IMPORT_PHRASE_WALLET)),
                        Case($(instanceOf(GenerateNewWalletAction.class)),
                                stateAction -> secretsUpdater.newWallet(state)),
                        Case($(instanceOf(ImportMnemonicAction.class)),
                                stateAction -> secretsUpdater.importMnemonic(state, stateAction)),

                        // This is the default branch of this switch-case. If an unknown action was passed to the
                        // updater, we simply return the old state. This is a convention, that is not needed right
                        // now, but will help once you start to decompose your updater.
                        Case($(), state)
                )
        );
    }

}


