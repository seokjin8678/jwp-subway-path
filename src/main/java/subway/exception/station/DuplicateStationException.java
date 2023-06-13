package subway.exception.station;

public class DuplicateStationException extends RuntimeException {
    public DuplicateStationException(String message) {
        super(message);
    }
}
