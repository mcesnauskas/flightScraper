package lt.mindaugas.scraper.testing;

import java.util.List;

public class FlightRecommendation {
    private Double price;
    private List<Flight> outboundFlights;
    private List<Flight> inboundFlights;

    // constructor

    public List<Flight> getOutboundFlights() {
        return outboundFlights;
    }

    public List<Flight> getInboundFlights() {
        return inboundFlights;
    }

    public Double getPrice() {
        return price;
    }

    public FlightRecommendation(Double price, List<Flight> outboundFlights, List<Flight> inboundFlights) {
        this.price = price;
        this.outboundFlights = outboundFlights;
        this.inboundFlights = inboundFlights;
    }

    @Override
    public String toString() {
        return "FlightRecommendation{" +
                "price=" + price +
                ", outboundFlights=" + outboundFlights +
                ", inboundFlights=" + inboundFlights +
                '}';
    }
}