package com.tezkit.wallet.view;

import com.netopyr.reduxfx.fxml.Dispatcher;
import com.netopyr.reduxfx.fxml.Selector;
import com.tezkit.wallet.actions.Actions;
import com.tezkit.wallet.state.AppState;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainView {
    private final Dispatcher dispatcher;
    private final Selector<AppState> selector;

    @FXML
    private Text lblWalletBalance;

    @FXML
    private StackedAreaChart<String, Number> chartBalanceHistory;

    @FXML
    private ComboBox comboReceiver;

    @FXML
    private TableView tableRecentTransactions;

    @Autowired
    public MainView(Dispatcher dispatcher, Selector<AppState> selector) {
        this.dispatcher = dispatcher;
        this.selector = selector;
    }

    /**
     * The method {@code initialize} is used to define the bindings that map the application state
     * to the view.
     */
    public void initialize() {
        comboReceiver.setItems(FXCollections.observableArrayList(
                "Adam Smith", "Olivia Stone", "Peter Pan"));

        lblWalletBalance.textProperty().bind(
                Bindings.format(
                        "%.2f XTZ",
                        selector.select((state) -> {
                            if (state.getCurrentWalletState() == null) return "...";
                            return state.getCurrentWalletState().getBalance();
                        })
                )
        );
//        ObservableValue<Integer> obs = selector.select(AppState::getCounter);
//        obs.addListener((o, oldVal, newVal) -> {
//            System.out.println("Electric bill has changed!");
//            value.setText("you click x times: " + (Integer) newVal);
//        });


        XYChart.Series series1 = new XYChart.Series();
//        series1.getNode().relo
        series1.setName("Balance Over Time");
        series1.getData().add(new XYChart.Data(0, 10));
        series1.getData().add(new XYChart.Data(10, 20));
        series1.getData().add(new XYChart.Data(20, 15));
        series1.getData().add(new XYChart.Data(30, 10));
        series1.getData().add(new XYChart.Data(40, 15));
        series1.getData().add(new XYChart.Data(50, 25));
        series1.getData().add(new XYChart.Data(60, 30));
        chartBalanceHistory.getData().addAll(series1);

        ((TableColumn) tableRecentTransactions.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory("type"));
        ((TableColumn) tableRecentTransactions.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory("date"));
        ((TableColumn) tableRecentTransactions.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory("to"));
        ((TableColumn) tableRecentTransactions.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory("amount"));
        var rec = TxRecord.builder()
                .amount("100")
                .date("2020/1/1")
                .to("tz1TGWQojBNZCuLkkdwXpFigdDL8PFiP8EsA")
                .type("IN")
                .build();
        var rec2 = TxRecord.builder()
                .amount("20")
                .date("2020/1/2")
                .to("tz1N9dysSpzApajX2bZH1Y1bCchP4GEwLV1c")
                .type("OUT")
                .build();
        tableRecentTransactions.setItems(FXCollections.observableArrayList(rec, rec2));


        dispatcher.dispatch(Actions.mainViewInitialized());
    }

    public void increase() {
        dispatcher.dispatch(Actions.incCounterAction());
    }

    @Data
    @Builder
    public static class TxRecord {
        private String to;
        private String date;
        private String amount;
        private String type;
    }
}
