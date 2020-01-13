package fan.zheyuan.demo.stockclient;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

@Log4j2
public class RSocketStockClient implements StockClient{

    private RSocketRequester rsocketRequester;

    public RSocketStockClient(RSocketRequester rsocketRequester) {
        this.rsocketRequester = rsocketRequester;
    }

    @Override
    public Flux<StockPrice> pricesFor(String symbol) {
        return rsocketRequester.route("stockPrices")
                .data(symbol)
                .retrieveFlux(StockPrice.class)
                .retryBackoff(5, Duration.ofSeconds(1), Duration.ofSeconds(20))
                .doOnError(IOException.class, e -> log.error(e.getMessage()));
    }
}
