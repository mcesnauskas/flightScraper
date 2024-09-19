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

        // Get response:
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

        String responseBody = httpResponse.body();
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject actualBody = jsonObject.getJSONObject("body");
        JSONObject data = actualBody.getJSONObject("data");

        // Extract prices
        Map<Integer, Double> prices = new HashMap<>();
        JSONArray totalAvailabilities = data.getJSONArray("totalAvailabilities");
        for (int i = 0; i < totalAvailabilities.length(); i++) {
            JSONObject availability = totalAvailabilities.getJSONObject(i);
            int recommendationId = availability.getInt("recommendationId");
            double total = availability.getDouble("total");
            prices.put(recommendationId, total);
        }

        // Extract journeys
        JSONArray journeys = data.getJSONArray("journeys");

        // Extract flight data
        Map<Integer, FlightRecommendation> flightRecommendations = new HashMap<>();
        for (int i = 0; i < journeys.length(); i++) {
            JSONObject journey = journeys.getJSONObject(i);
            JSONArray flights = journey.getJSONArray("flights");
            int recommendationId = journey.getInt("recommendationId");
            double price = prices.get(recommendationId);

            // Create a new FlightRecommendation object if it doesn't exist
            if (!flightRecommendations.containsKey(recommendationId)) {
                flightRecommendations.put(recommendationId, new FlightRecommendation(price, new ArrayList<>(), new ArrayList<>()));
            }

            // Extract data for each flight
            for (int j = 0; j < flights.length(); j++) {
                JSONObject flight = flights.getJSONObject(j);
                String airportDeparture = flight.getJSONObject("airportDeparture").getString("code");
                String airportArrival = flight.getJSONObject("airportArrival").getString("code");
                String timeDeparture = flight.getString("dateDeparture");
                String timeArrival = flight.getString("dateArrival");
                String flightNumber = flight.getString("number");

                // Create a new Flight object
                Flight newFlight = new Flight(airportDeparture, airportArrival, timeDeparture, timeArrival, flightNumber);

                // Determine whether to add the flight to the outboundFlights list or the inboundFlights list
                String direction = journey.getString("direction");
                if (direction.equals("I")) {
                    flightRecommendations.get(recommendationId).getOutboundFlights().add(newFlight);
                } else {
                    flightRecommendations.get(recommendationId).getInboundFlights().add(newFlight);
                }
            }
        }

        StringBuilder csvContent = new StringBuilder();
        csvContent.append(
                "Price," +
                        "outbound 1 airport departure," +
                        "outbound 1 airport arrival," +
                        "outbound 1 time departure," +
                        "outbound 1 time arrival," +
                        "outbound 1 flight number," +
                        "outbound 2 airport departure," +
                        "outbound 2 airport arrival," +
                        "outbound 2 time departure," +
                        "outbound 2 time arrival," +
                        "outbound 2 flight number," +
                        "inbound 1 airport departure," +
                        "inbound 1 airport arrival," +
                        "inbound 1 time departure," +
                        "inbound 1 time arrival," +
                        "inbound 1 flight number," +
                        "inbound 2 airport departure," +
                        "inbound 2 airport arrival," +
                        "inbound 2 time departure," +
                        "inbound 2 time arrival," +
                        "inbound 2 flight number\n"
        );

        for (FlightRecommendation flightRecommendation : flightRecommendations.values()) {
            double price = flightRecommendation.getPrice();
            List<Flight> outboundFlights = flightRecommendation.getOutboundFlights();
            List<Flight> inboundFlights = flightRecommendation.getInboundFlights();

            StringBuilder csvLine = new StringBuilder();
            csvLine.append(price).append(",");

            for (int i = 0; i < 2; i++) {
                if (i < outboundFlights.size()) {
                    Flight outboundFlight = outboundFlights.get(i);
                    csvLine.append(outboundFlight.getAirportDeparture()).append(",")
                            .append(outboundFlight.getAirportArrival()).append(",")
                            .append(outboundFlight.getTimeDeparture()).append(",")
                            .append(outboundFlight.getTimeArrival()).append(",")
                            .append(outboundFlight.getFlightNumber()).append(",");
                } else {
                    csvLine.append(",,,,,");
                }
            }

            for (int i = 0; i < 2; i++) {
                if (i < inboundFlights.size()) {
                    Flight inboundFlight = inboundFlights.get(i);
                    csvLine.append(inboundFlight.getAirportDeparture()).append(",")
                            .append(inboundFlight.getAirportArrival()).append(",")
                            .append(inboundFlight.getTimeDeparture()).append(",")
                            .append(inboundFlight.getTimeArrival()).append(",")
                            .append(inboundFlight.getFlightNumber()).append(",");
                } else {
                    csvLine.append(",,,,,");
                }
            }

            csvContent.append(csvLine.toString()).append("\n");
        }

        PrintWriter writer = new PrintWriter("flight_recommendations.csv", "UTF-8");
        writer.print(csvContent.toString());
        writer.close();

        for (FlightRecommendation flightRecommendation : flightRecommendations.values()) {
            System.out.println(flightRecommendation);
        }
    }
}