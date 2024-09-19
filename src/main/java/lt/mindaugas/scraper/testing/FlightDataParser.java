package lt.mindaugas.scraper.testing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlightDataParser {

    public Map<Integer, FlightRecommendation> parseFlightData(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject actualBody = jsonObject.getJSONObject("body");
        JSONObject data = actualBody.getJSONObject("data");

        Map<Integer, Double> prices = extractPrices(data);
        return extractFlightRecommendations(data, prices);
    }

    private Map<Integer, Double> extractPrices(JSONObject data) {
        Map<Integer, Double> prices = new HashMap<>();
        JSONArray totalAvailabilities = data.getJSONArray("totalAvailabilities");
        for (int i = 0; i < totalAvailabilities.length(); i++) {
            JSONObject availability = totalAvailabilities.getJSONObject(i);
            int recommendationId = availability.getInt("recommendationId");
            double total = availability.getDouble("total");
            prices.put(recommendationId, total);
        }
        return prices;
    }

    private Map<Integer, FlightRecommendation> extractFlightRecommendations(JSONObject data, Map<Integer, Double> prices) {
        Map<Integer, FlightRecommendation> flightRecommendations = new HashMap<>();
        JSONArray journeys = data.getJSONArray("journeys");

        for (int i = 0; i < journeys.length(); i++) {
            JSONObject journey = journeys.getJSONObject(i);
            int recommendationId = journey.getInt("recommendationId");

            FlightRecommendation flightRecommendation = flightRecommendations.getOrDefault(
                    recommendationId,
                    new FlightRecommendation(prices.get(recommendationId), 0.0, new ArrayList<>(), new ArrayList<>())
            );

            double importTaxAdl = journey.getDouble("importTaxAdl");
            flightRecommendation.setImportTaxAdl(flightRecommendation.getImportTaxAdl() + importTaxAdl);

            extractFlights(journey, flightRecommendation);

            flightRecommendations.put(recommendationId, flightRecommendation);
        }
        return flightRecommendations;
    }

    private void extractFlights(JSONObject journey, FlightRecommendation flightRecommendation) {
        JSONArray flights = journey.getJSONArray("flights");
        String direction = journey.getString("direction");

        for (int j = 0; j < flights.length(); j++) {
            JSONObject flight = flights.getJSONObject(j);
            Flight newFlight = new Flight(
                    flight.getJSONObject("airportDeparture").getString("code"),
                    flight.getJSONObject("airportArrival").getString("code"),
                    flight.getString("dateDeparture"),
                    flight.getString("dateArrival"),
                    flight.getString("number")
            );

            if (direction.equals("I")) {
                flightRecommendation.getOutboundFlights().add(newFlight);
            } else if (direction.equals("V")) {
                flightRecommendation.getInboundFlights().add(newFlight);
            }
        }
    }
}
