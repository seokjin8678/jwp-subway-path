package subway.dto.station;

import lombok.AllArgsConstructor;
import lombok.Getter;
import subway.domain.station.Station;

@AllArgsConstructor
@Getter
public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
