package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.core.network.BalanceChange;
import com.tezkit.wallet.state.AppState;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardView {

    private final Selector<AppState> selector;
    private final ToggleGroup group = new ToggleGroup();
    @FXML
    private Label lblWalletBalance, lblTotalUSDBalance, lblPercPastMonth, lblUSDPastMonth;
    @FXML
    private AreaChart<?, ?> dataChart;
    @FXML
    private ToggleButton tbWeek, tbMonth, tbYear;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn colTxType, colTxAmount, colTxDate, colTxDest;
    @FXML
    private ImageView iconMonthlyPerc, iconMonthlyUSD;
    @FXML
    private ScrollPane spDashboard;
    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    public DashboardView(Selector<AppState> selector) {
        this.selector = selector;
    }

    public void initialize() {
        bindBalance();
        bindBalanceHistory();
        bindUSDBalance();
        bindLastMonthChange();

        initBalanceTableColumns();

        dataChart.getXAxis().setTickLabelsVisible(false);
        dataChart.getXAxis().setTickMarkVisible(false);

        tbMonth.setToggleGroup(group);
        tbWeek.setToggleGroup(group);
        tbWeek.setSelected(true);
        tbYear.setToggleGroup(group);
        group.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            if (new_toggle == null) {
                // skip
            } else {
                // render chart
            }
        });
    }

    private void bindUSDBalance() {
        ObservableValue<BigDecimal> USDBalanceObs = selector.select((state) -> {
            if (state.getCurrentWalletState() == null) return null;
            if (state.getCurrentWalletState().getBalance() == null) return null;
            if (state.getExchangeRate() == null) return null;

            var balance = new BigDecimal(state.getCurrentWalletState().getBalance());
            var exchangeRate = new BigDecimal(state.getExchangeRate().getHigh());
            return balance.divide(new BigDecimal(1_000_000)).multiply(exchangeRate);
        });

        lblTotalUSDBalance.textProperty().bind(
                Bindings.createStringBinding(
                        () -> new Formatter(2, " USD").balance(USDBalanceObs.getValue()),
                        USDBalanceObs));
    }

    private void bindBalance() {
        ObservableValue<BigInteger> balanceObs = selector.select((state) -> {
            if (state.getCurrentWalletState() == null) return null;
            return state.getCurrentWalletState().getBalance();
        });

        lblWalletBalance.textProperty().bind(
                Bindings.createStringBinding(
                        () -> new Formatter(2, " XTZ").balance(balanceObs.getValue()),
                        balanceObs));
    }

    private void bindBalanceHistory() {
        ObservableList<BalanceChange> balanceChanges = selector.selectList((state) -> {
            if (state.getCurrentWalletState() == null) return new LinkedList<>();
            if (state.getCurrentWalletState().getBalanceChanges() == null)
                return new LinkedList<>();
            return state.getCurrentWalletState().getBalanceChanges();
        });

        balanceChanges.addListener((ListChangeListener<? super BalanceChange>) (changes) -> {
            var accumulativeBalance = accumulativeBalance(changes.getList());
            renderBalanceChart(accumulativeBalance);
            renderRecentTxs(changes.getList());
        });

    }

    private void bindLastMonthChange() {
        ObservableValue<Double[]> lastMonthChangesObs = selector.select((state) -> {
            if (state.getCurrentWalletState() == null) return null;
            if (state.getCurrentWalletState().getBalanceChanges() == null) return null;
            if (state.getExchangeRate() == null) return null;
            var changes = state.getCurrentWalletState().getBalanceChanges();
            var rate = state.getExchangeRate();
            var computedChanges = computeMonthlyChange(changes, accumulativeBalance(changes));

            // return [change in dollars; change in % of XTZ]
            return new Double[]{rate.getHigh() * computedChanges[0], computedChanges[1]};
        });

        lastMonthChangesObs.addListener((c) -> {
            var absoluteChangeUSD = lastMonthChangesObs.getValue()[0];
            var relativeChange = lastMonthChangesObs.getValue()[1];
            renderMonthlyChange(absoluteChangeUSD, relativeChange);
        });
    }

    private void renderMonthlyChange(Double absoluteChangeUSD, Double relativeChange) {
        lblUSDPastMonth.setText(new Formatter(2, " USD")
                .balance(new BigDecimal(absoluteChangeUSD)));
        iconMonthlyUSD.setImage(getArrowImage(absoluteChangeUSD));

        String percentualChange = relativeChange.isInfinite() ?
                "100%" : new Formatter(2, "%").balance(new BigDecimal(relativeChange));
        lblPercPastMonth.setText(percentualChange);
        iconMonthlyPerc.setImage(getArrowImage(relativeChange));
    }

    private Image getArrowImage(Double relativeChange) {
        var path = relativeChange > 0 ? "/icons/up_green.png" : "/icons/down_red.png";
        return new Image(path, 16, 16, false, false);
    }

    private void initBalanceTableColumns() {
        colTxAmount.setCellValueFactory(new PropertyValueFactory<RecentTx, Double>("amount"));
        colTxDate.setCellValueFactory(new PropertyValueFactory<RecentTx, String>("timestamp"));
        colTxDest.setCellValueFactory(new PropertyValueFactory<RecentTx, String>("address"));
        colTxType.setCellValueFactory(new PropertyValueFactory<RecentTx, ImageView>("type"));
    }

    /**
     * Compute how much the account balance has changed in the last month in both relative and
     * absolute numbers.
     *
     * @param changes             all changes in an account
     * @param accumulativeBalance accumulative changes for this acc to get the current balance
     * @return a tuple/pair where the first item is the absolute and the second relative change
     */
    protected Double[] computeMonthlyChange(List<? extends BalanceChange> changes,
            LinkedList<Double> accumulativeBalance) {
        ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().plusDays(-30);
        var changeOverLastMonth = changes.stream()
                .sorted(Collections.reverseOrder())
                .takeWhile(bc -> bc.getTimestamp().isAfter(thirtyDaysAgo))
                .map(bc -> bc.getIn() - bc.getOut())
                .reduce(0.0, (acc, i) -> acc + i);
        var currentBalance = accumulativeBalance.size() > 0 ? accumulativeBalance.getLast() : 0;
        var balanceLastMonth = currentBalance - changeOverLastMonth;
        if (isZero(balanceLastMonth, 0.1)) {
            return new Double[]{changeOverLastMonth, 1.0 / 0.0};
        }
        if (isZero((currentBalance - changeOverLastMonth), 0.1)) {
            return new Double[]{balanceLastMonth, 0.0};
        } else {
            var percentualIncrease = 100 * (currentBalance - balanceLastMonth) / balanceLastMonth;
            return new Double[]{changeOverLastMonth, percentualIncrease};
        }
    }

    public boolean isZero(double value, double threshold) {
        return value >= -threshold && value <= threshold;
    }

    private void renderRecentTxs(List<? extends BalanceChange> changes) {
        var txs = changes.parallelStream()
                .sorted(Collections.reverseOrder())
                .filter(change -> !change.getIsFee())
                .map(change -> remapToRecentTx(change))
                .collect(Collectors.toList());
        tableView.getItems().setAll(txs);
    }

    private RecentTx remapToRecentTx(BalanceChange change) {
        DecimalFormat formatter = getDecimalFormatter();
        var icon = new ImageView(getArrowImage(change.getIn() - change.getOut()));
        return new RecentTx()
                .setAmount(formatter.format(change.getIn() - change.getOut()))
                .setAddress(change.getCounterparty())
                .setTimestamp(change.getTimestamp().toString())
                .setType(icon);
    }

    private DecimalFormat getDecimalFormatter() {
        var formatter = new DecimalFormat("#.#");
        formatter.setMaximumFractionDigits(6);
        return formatter;
    }

    private void renderBalanceChart(LinkedList<Double> out) {
        XYChart.Series series1 = new XYChart.Series();
        for (Integer i = 0; i < out.size(); i++) {
            series1.getData().add(new XYChart.Data<>(i.toString(), out.get(i)));
        }
        dataChart.getData().clear();
        dataChart.getData().add(series1);
    }

    /**
     * Take a list of balance changes and trasnform them into a series of balance states.
     * [+1, +1, +1] => [1, 2, 3] - account received one unit three times => balances were 1, 2 and 3
     *
     * @param changes balance change events
     * @return a series of balance history
     */
    private LinkedList<Double> accumulativeBalance(List<? extends BalanceChange> changes) {
        return changes.stream().reduce(new LinkedList<>(), (acc, change) -> {
            Double newBalance = (acc.size() > 0) ?
                    acc.getLast() + change.getIn() - change.getOut() :
                    change.getIn() - change.getOut();
            acc.add(newBalance);
            return acc;
        }, (a, b) -> /* never invoked */ new LinkedList<>());
    }

    @Data
    @Accessors(chain = true)
    public static class RecentTx {
        private String amount;
        private String address;
        private ImageView type;
        private String timestamp;
    }

}
