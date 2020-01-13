package fan.zheyuan.demo.stockui;

import fan.zheyuan.demo.stockclient.StockPrice;
import fan.zheyuan.demo.stockclient.WebClientStockClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ChartController {

    @FXML
    public LineChart<String, Double> chart;
    private WebClientStockClient webClientStockClient;

    public ChartController(WebClientStockClient webClientStockClient) {
        this.webClientStockClient = webClientStockClient;
    }

    @FXML
    public void initialize() {
        String symbol1 = "SYMBOL1";
        final PriceSubscriber priceSubscriber1 = new PriceSubscriber(symbol1);
        webClientStockClient.pricesFor(symbol1)
                .subscribe(priceSubscriber1);

        String symbol2 = "SYMBOL2";
        final PriceSubscriber priceSubscriber2 = new PriceSubscriber(symbol2);
        webClientStockClient.pricesFor(symbol2)
                .subscribe(priceSubscriber2);

        ObservableList<Series<String, Double>> data = FXCollections.observableArrayList();
        data.add(priceSubscriber1.getSeries());
        data.add(priceSubscriber2.getSeries());
        chart.setData(data);
    }


    private static class PriceSubscriber implements Consumer<StockPrice> {
        private final ObservableList<XYChart.Data<String, Double>> seriesData = FXCollections.observableArrayList();
        private final Series<String, Double> series;

        public PriceSubscriber(String symbol) {
            series = new Series<>(symbol, seriesData);
        }

        @Override
        public void accept(StockPrice stockPrice) {
            Platform.runLater(() ->
                    seriesData.add(new XYChart.Data<>(String.valueOf(stockPrice.getTime().getSecond()),
                            stockPrice.getPrice()))
            );
        }

        public Series<String, Double> getSeries() {
            return series;
        }
    }
}
