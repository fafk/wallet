package com.tezkit.wallet.state;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.BalanceChange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import milfont.com.tezosj.model.TezosWallet;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CurrentWalletState {
    @With private final KeyHolder keys;
    @With private final BigInteger balance;
    @With private final List<BalanceChange> balanceChanges;
}

