package lt.mindaugas.scraper.service;

import lt.mindaugas.scraper.model.Flight;
import lt.mindaugas.scraper.model.FlightRecommendation;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CSVExportService {

    // Method to export flight recommendations to CSV
    public void exportToCSV(Map<Integer, FlightRecommendation> flightRecommendations, String filePath) throws Exception {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(getCSVHeader());

        for (FlightRecommendation flightRecommendation : flightRecommendations.values()) {
            csvContent.append(formatRecommendationForCSV(flightRecommendation));
        }

        // Write CSV to file
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            writer.print(csvContent.toString());
        }
    }

    // Helper method to get CSV header
    private String getCSVHeader() {
        return "Price,ImportTaxAdl," +
                "Outbound Flight 1 (Departure,Arrival,Departure Time,Arrival Time,Flight Number)," +
                "Outbound Flight 2 (Departure,Arrival,Departure Time,Arrival Time,Flight Number)," +
                "Inbound Flight 1 (Departure,Arrival,Departure Time,Arrival Time,Flight Number)," +
                "Inbound Flight 2 (Departure,Arrival,Departure Time,Arrival Time,Flight Number)\n";
    }

    // Helper method to format a FlightRecommendation for CSV
    private String formatRecommendationForCSV(FlightRecommendation flightRecommendation) {
        StringBuilder csvLine = new StringBuilder();
        csvLine.append(flightRecommendation.getPrice()).append(",")
                .append(flightRecommendation.getImportTaxAdl()).append(",");

        List<Flight> outboundFlights = flightRecommendation.getOutboundFlights();
        List<Flight> inboundFlights = flightRecommendation.getInboundFlights();

        appendFlightData(csvLine, outboundFlights, 2); // Append up to 2 outbound flights
        appendFlightData(csvLine, inboundFlights, 2);  // Append up to 2 inbound flights

        return csvLine.append("\n").toString();
    }

    // Helper method to append flight data for CSV
    private void appendFlightData(StringBuilder csvLine, List<Flight> flights, int maxFlights) {
        for (int i = 0; i < maxFlights; i++) {
            if (i < flights.size()) {
                Flight flight = flights.get(i);
                csvLine.append(flight.getAirportDeparture()).append(",")
                        .append(flight.getAirportArrival()).append(",")
                        .append(flight.getTimeDeparture()).append(",")
                        .append(flight.getTimeArrival()).append(",")
                        .append(flight.getFlightNumber()).append(",");
            } else {
                csvLine.append(",,,,,");
            }
        }
    }
}

