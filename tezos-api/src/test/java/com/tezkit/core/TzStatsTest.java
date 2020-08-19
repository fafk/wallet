package com.tezkit.core;

import com.tezkit.core.network.Network;
import com.tezkit.core.network.TzStatsRPC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class) // requires JUnit 4
@PrepareForTest({Network.class})
@SuppressStaticInitializationFor({"com.tezkit.core.network.Network"})
public class TzStatsTest {

    class MockResponse implements HttpResponse<String> {
        private final String body;
        MockResponse(String body) { this.body = body; }
        @Override public int statusCode() { return 200; }
        @Override public HttpRequest request() { return null; }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return null; }
        @Override
        public String body() { return body; } // *** the meat of the class **
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return null; }
        @Override public HttpClient.Version version() { return null; }
    }

    @Test
    public void getBalanceSeriesTest() throws ExecutionException, InterruptedException {
        var responseBody = "[[1595243215000,1.013837,0.000000,\"tz1MXjdb684ByEP5qUn5J7EMub7Sr8eBziDe\",0]\n" +
                ",[1595919185000,0.000000,0.002490,\"tz1TDSmoZXwVevLTEvKCTHWpomG76oC9S2fJ\",1]]";
        PowerMockito.mockStatic(Network.class);
        PowerMockito.when(Network.getAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture(new MockResponse(responseBody)));

        var res = TzStatsRPC.getAllBalanceChanges("tz1UPm1EoC7ZQ6EGHZ5LuHf37NnBrSixwgzq").get();

        assertEquals(res.size(), 2);
        assertEquals("2020-07-20T11:06:55Z", res.get(0).getTimestamp().toString());
        assertEquals(1.013837, res.get(0).getIn());
        assertEquals(0.0, res.get(0).getOut());
        assertEquals(0.0, res.get(1).getIn());
        assertEquals(0.002490, res.get(1).getOut());
    }

    @Test
    public void getExchangeRateTest() throws ExecutionException, InterruptedException {
        var responseBody = "[[1596708000000,3.13240,3.14230,3.13230,3.14100,3.13747,12,7,5,798.79830,2506.20227,492.22676,1545.85308,306.57154,960.34919]\n]";
        PowerMockito.mockStatic(Network.class);
        PowerMockito.when(Network.getAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture(new MockResponse(responseBody)));

        var res = TzStatsRPC.getExchangeRate().get();

        assertEquals("2020-08-06T10:00Z", res.getTime().toString());
        assertEquals(3.13240, res.getOpen());
        assertEquals(3.14230, res.getHigh());
        assertEquals(3.13230, res.getLow());
    }

}
