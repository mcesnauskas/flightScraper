package lt.mindaugas.scraper;

import lt.mindaugas.scraper.service.CSVExportService;
import lt.mindaugas.scraper.service.FlightDataFetcher;
import lt.mindaugas.scraper.service.FlightDataParser;
import lt.mindaugas.scraper.model.FlightRecommendation;

import java.util.Map;

public class Main {

    private static final String BASE_URL = "http://homeworktask.infare.lt";

    public static void main(String[] args) throws Exception {
        // Generate endpoint
        String endpoint = generateEndpoint("MAD", "AUH", "2024-10-02", "2024-11-09");

        // Fetch flight data
        FlightDataFetcher fetcher = new FlightDataFetcher();
        String responseBody = fetcher.fetchFlightData(BASE_URL + endpoint);

        // Parse flight data
        FlightDataParser parser = new FlightDataParser();
        Map<Integer, FlightRecommendation> flightRecommendations = parser.parseFlightData(responseBody);

        // Export data to CSV
        CSVExportService csvExportService = new CSVExportService();
        csvExportService.exportToCSV(flightRecommendations, "flight_recommendations.csv");

        // Print flight recommendations for debugging
        flightRecommendations.values().forEach(System.out::println);
    }

    // Method to generate endpoint
    public static String generateEndpoint(String from, String to, String departDate, String returnDate) {
        return String.format("/search.php?from=%s&to=%s&depart=%s&return=%s", from, to, departDate, returnDate);
    }
}
