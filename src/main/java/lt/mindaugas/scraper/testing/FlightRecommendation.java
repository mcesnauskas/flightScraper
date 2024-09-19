package lt.mindaugas.scraper.testing;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FlightRecommendation {
    private Double price;
    private Double importTaxAdl;
    private List<Flight> outboundFlights;
    private List<Flight> inboundFlights;
    
    public FlightRecommendation(Double price, Double importTaxAdl, List<Flight> outboundFlights, List<Flight> inboundFlights) {
        this.price = price;
        this.importTaxAdl = importTaxAdl;
        this.outboundFlights = outboundFlights;
        this.inboundFlights = inboundFlights;
    }
}
