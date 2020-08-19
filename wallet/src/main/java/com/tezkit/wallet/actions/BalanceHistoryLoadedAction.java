package com.tezkit.wallet.actions;

import com.tezkit.core.network.BalanceChange;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BalanceHistoryLoadedAction {
    private final String address;
    private final List<BalanceChange> balanceChanges;
}
