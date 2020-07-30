package com.tezkit.wallet.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportMnemonicAction {
    final private String mnemonic;
    final private String password;
}
