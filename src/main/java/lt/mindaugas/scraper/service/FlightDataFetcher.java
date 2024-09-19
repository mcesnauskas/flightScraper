package lt.mindaugas.scraper.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FlightDataFetcher {

    public String fetchFlightData(String url) throws Exception {
        URI uri = new URI(url);
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).GET().build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return httpResponse.body();
    }
}

