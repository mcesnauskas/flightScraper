package lt.mindaugas.scraper.testing;

import java.util.List;

public class FlightRecommendation {
    private Double price;
    private Double importTaxAdl;
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

    public Double getImportTaxAdl() {
        return importTaxAdl;
    }

    public void setImportTaxAdl(Double importTaxAdl) {
        this.importTaxAdl = importTaxAdl;
    }

    public FlightRecommendation(Double price, Double importTaxAdl, List<Flight> outboundFlights, List<Flight> inboundFlights) {
        this.price = price;
        this.importTaxAdl = importTaxAdl;
        this.outboundFlights = outboundFlights;
        this.inboundFlights = inboundFlights;
    }

    @Override
    public String toString() {
        return "FlightRecommendation{" +
                "price=" + price +
                ", importTaxAdl=" + importTaxAdl +
                ", outboundFlights=" + outboundFlights +
                ", inboundFlights=" + inboundFlights +
                '}';
    }
}
