package com.tezkit.wallet.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import milfont.com.tezosj.model.TezosWallet;

@Data
@Builder
@AllArgsConstructor
public class CurrentWalletState {
//    @With private final String mnemonic;
    @With private final Double balance;
    @With private final TezosWallet tezosWallet;
}

