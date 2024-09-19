package lt.mindaugas.scraper;

import lt.mindaugas.scraper.testing.Flight;
import lt.mindaugas.scraper.testing.FlightRecommendation;
import org.json.JSONArray;
import org.json.JSONObject;

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

        // Generate endpoint and URL
        endpoint = generateEndpoint("MAD", "AUH", "2024-10-02", "2024-11-09");
        url = baseUrl + endpoint;

        // Send HTTP request
        URI uri = new URI(url);
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).GET().build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // Parse the response
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

        // Map to hold flight recommendations
        Map<Integer, FlightRecommendation> flightRecommendations = new HashMap<>();

        // Extract flight data for each journey
        for (int i = 0; i < journeys.length(); i++) {
            JSONObject journey = journeys.getJSONObject(i);
            JSONArray flights = journey.getJSONArray("flights");
            int recommendationId = journey.getInt("recommendationId");
            double price = prices.get(recommendationId);

            // Extract importTaxAdl
            double importTaxAdl = journey.getDouble("importTaxAdl");

            // Create or update the FlightRecommendation object
            FlightRecommendation flightRecommendation = flightRecommendations.getOrDefault(
                    recommendationId,
                    new FlightRecommendation(price, 0.0, new ArrayList<>(), new ArrayList<>())
            );

            // If the FlightRecommendation object already exists, add the importTaxAdl to the existing value
            double existingImportTaxAdl = flightRecommendation.getImportTaxAdl();
            flightRecommendation.setImportTaxAdl(existingImportTaxAdl + importTaxAdl);

            // Process each flight in the journey
            for (int j = 0; j < flights.length(); j++) {
                JSONObject flight = flights.getJSONObject(j);
                String airportDeparture = flight.getJSONObject("airportDeparture").getString("code");
                String airportArrival = flight.getJSONObject("airportArrival").getString("code");
                String timeDeparture = flight.getString("dateDeparture");
                String timeArrival = flight.getString("dateArrival");
                String flightNumber = flight.getString("number");

                // Create a new Flight object
                Flight newFlight = new Flight(airportDeparture, airportArrival, timeDeparture, timeArrival, flightNumber);

                // Determine if it's an outbound or inbound flight based on direction ("I" = Outbound, "V" = Inbound)
                String direction = journey.getString("direction");
                if (direction.equals("I")) {
                    flightRecommendation.getOutboundFlights().add(newFlight);  // Outbound flight
                } else if (direction.equals("V")) {
                    flightRecommendation.getInboundFlights().add(newFlight);  // Inbound flight
                }
            }

            // Add the updated FlightRecommendation back to the map
            flightRecommendations.put(recommendationId, flightRecommendation);
        }

        // Prepare CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(
                "Price,ImportTaxAdl," +
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

        // Populate CSV rows with flight data
        for (FlightRecommendation flightRecommendation : flightRecommendations.values()) {
            double price = flightRecommendation.getPrice();
            double importTaxAdl = flightRecommendation.getImportTaxAdl();
            List<Flight> outboundFlights = flightRecommendation.getOutboundFlights();
            List<Flight> inboundFlights = flightRecommendation.getInboundFlights();

            StringBuilder csvLine = new StringBuilder();
            csvLine.append(price).append(",").append(importTaxAdl).append(",");

            // Outbound flights (up to 2)
            for (int i = 0; i < 2; i++) {
                if (i < outboundFlights.size()) {
                    Flight outboundFlight = outboundFlights.get(i);
                    csvLine.append(outboundFlight.getAirportDeparture()).append(",")
                            .append(outboundFlight.getAirportArrival()).append(",")
                            .append(outboundFlight.getTimeDeparture()).append(",")
                            .append(outboundFlight.getTimeArrival()).append(",")
                            .append(outboundFlight.getFlightNumber()).append(",");
                } else {
                    csvLine.append(",,,,,");  // Empty values if less than 2 flights
                }
            }

            // Inbound flights (up to 2)
            for (int i = 0; i < 2; i++) {
                if (i < inboundFlights.size()) {
                    Flight inboundFlight = inboundFlights.get(i);
                    csvLine.append(inboundFlight.getAirportDeparture()).append(",")
                            .append(inboundFlight.getAirportArrival()).append(",")
                            .append(inboundFlight.getTimeDeparture()).append(",")
                            .append(inboundFlight.getTimeArrival()).append(",")
                            .append(inboundFlight.getFlightNumber()).append(",");
                } else {
                    csvLine.append(",,,,,");  // Empty values if less than 2 flights
                }
            }

            csvContent.append(csvLine.toString()).append("\n");
        }

        // Write CSV to file
        PrintWriter writer = new PrintWriter("flight_recommendations.csv", "UTF-8");
        writer.print(csvContent.toString());
        writer.close();

        // Print flight recommendations
        for (FlightRecommendation flightRecommendation : flightRecommendations.values()) {
            System.out.println(flightRecommendation);
        }
    }
}
