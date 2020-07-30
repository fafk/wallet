package com.tezkit.core.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class Network {

    public static final Integer TIMEOUT = 15;

    static HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    static public HttpResponse<String> get(String url) throws TezosRPCException {
        var request = requestBuilder(url).build();

        try {
            return checkStatusCode(client.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new TezosRPCException(e.toString());
        }
    }

    static public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        var request = requestBuilder(url).build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    static public HttpResponse<String> post(String url, String data) throws TezosRPCException {
        var request = requestBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        try {
            return checkStatusCode(client.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new TezosRPCException(e.toString());
        }
    }

    private static HttpRequest.Builder requestBuilder(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT));
    }

    private static HttpResponse<String> checkStatusCode(HttpResponse<String> response)
    throws TezosRPCException {
        if (response.statusCode() > 299 || response.statusCode() < 200) {
            throw new TezosRPCException(
                    "Network error: " + response.statusCode() + response.body());
        }
        return response;
    }
}
