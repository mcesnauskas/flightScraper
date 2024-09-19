package lt.mindaugas.scraper.testing;

public class Flight {
    private String airportDeparture;
    private String airportArrival;
    private String timeDeparture;
    private String timeArrival;
    private String flightNumber;

    public Flight(String airportDeparture, String airportArrival, String timeDeparture, String timeArrival, String flightNumber) {
        this.airportDeparture = airportDeparture;
        this.airportArrival = airportArrival;
        this.timeDeparture = timeDeparture;
        this.timeArrival = timeArrival;
        this.flightNumber = flightNumber;
    }

    public String getAirportDeparture() {
        return airportDeparture;
    }

    public String getAirportArrival() {
        return airportArrival;
    }

    public String getTimeDeparture() {
        return timeDeparture;
    }

    public String getTimeArrival() {
        return timeArrival;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "airportDeparture='" + airportDeparture + '\'' +
                ", airportArrival='" + airportArrival + '\'' +
                ", timeDeparture='" + timeDeparture + '\'' +
                ", timeArrival='" + timeArrival + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                '}';
    }
}