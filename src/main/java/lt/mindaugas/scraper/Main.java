package lt.mindaugas.scraper;

import lt.mindaugas.scraper.testing.Flight;
import lt.mindaugas.scraper.testing.FlightRecommendation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static String baseUrl = "http://homeworktask.infare.lt";
    private static String endpoint = "";
    private static String url;

    public static String generateEndpoint(String from, String to, String depart, String returnDate) {
        return String.format("/search.php?from=%s&to=%s&depart=%s&return=%s", from, to, depart, returnDate);
    }

    public static void main(String[] args) throws Exception {

        endpoint = generateEndpoint("MAD", "FUE", "2024-10-02", "2024-11-09");
        url = baseUrl + endpoint;

        URI uri = new URI(url);
        HttpRequest httpRequest = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response code: " + httpResponse.statusCode());
        System.out.println("Response headers: " + httpResponse.headers());
        System.out.println("Response body: " + httpResponse.body());
    }
}