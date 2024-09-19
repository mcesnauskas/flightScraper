package lt.mindaugas.scraper.testing;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Flight {
    private String airportDeparture;
    private String airportArrival;
    private String timeDeparture;
    private String timeArrival;
    private String flightNumber;

    public Flight(String airportDeparture,
                  String airportArrival,
                  String timeDeparture,
                  String timeArrival,
                  String flightNumber) {
        this.airportDeparture = airportDeparture;
        this.airportArrival = airportArrival;
        this.timeDeparture = timeDeparture;
        this.timeArrival = timeArrival;
        this.flightNumber = flightNumber;
    }
}
