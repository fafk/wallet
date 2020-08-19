package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.core.network.BalanceChange;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardViewTest {

    @Test
    public void testMonthlyDiff() {
        // from zero to hero (an infinite % increase)
        var changes = List.of(
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-60)).in(0.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-20)).in(10.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-10)).in(10.0).out(0.0).build());
        var accumulativeBalances = new LinkedList();
        accumulativeBalances.add(changes.stream().map(c -> c.getIn() - c.getOut()).reduce(0.0, (acc, i) -> acc + i));
        var res = new DashboardView(Mockito.mock(Selector.class)).computeMonthlyChange(changes, accumulativeBalances);
        assertEquals(20.0, res[0]);
        assertEquals(1.0 / 0.0, res[1]);

        // No change
        changes = List.of(
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-60)).in(10.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-10)).in(0.0).out(0.0).build());
        accumulativeBalances.clear();
        accumulativeBalances.add(changes.stream().map(c -> c.getIn() - c.getOut()).reduce(0.0, (acc, i) -> acc + i));
        res = new DashboardView(Mockito.mock(Selector.class)).computeMonthlyChange(changes, accumulativeBalances);
        assertEquals(0.0, res[0]);
        assertEquals(0.0, res[1]);

        // from 10 to 30 - 200% inc
        changes = List.of(
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-60)).in(10.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-20)).in(10.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-10)).in(10.0).out(0.0).build());
        accumulativeBalances.clear();
        accumulativeBalances.add(changes.stream().map(c -> c.getIn() - c.getOut()).reduce(0.0, (acc, i) -> acc + i));
        res = new DashboardView(Mockito.mock(Selector.class)).computeMonthlyChange(changes, accumulativeBalances);
        assertEquals(20.0, res[0]);
        assertEquals(200.0, res[1]);

        // from 10 to 7.5 = 25% decrease
        changes = List.of(
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-60)).in(10.0).out(0.0).build(),
                BalanceChange.builder().timestamp(ZonedDateTime.now().plusDays(-10)).in(0.0).out(2.5).build());
        accumulativeBalances.clear();
        accumulativeBalances.add(changes.stream().map(c -> c.getIn() - c.getOut()).reduce(0.0, (acc, i) -> acc + i));
        res = new DashboardView(Mockito.mock(Selector.class)).computeMonthlyChange(changes, accumulativeBalances);
        assertEquals(-2.5, res[0]);
        assertEquals(-25.0, res[1]);
    }

}
