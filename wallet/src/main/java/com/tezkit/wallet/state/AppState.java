package com.tezkit.wallet.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@AllArgsConstructor
// TODO document lombok @With immutability
public class AppState {

    @With final private TezkitScreen tezkitScreen;
    @With final private String passwordValidationMsg;
    @With final private String chosenPassword;
    @With final private CurrentWalletState currentWalletState;

    /**
     * Initialize the app state.
     *
     * @return the new {@code AppState}
     */
    public static AppState create() {
        return AppState.builder()
                .passwordValidationMsg("")
                .tezkitScreen(TezkitScreen.CHOOSE_PASSWORD)
                .chosenPassword("")
                .build();
    }

}
