package subway.dto.station;

import javax.validation.constraints.NotBlank;

public class StationCreateRequest {
    @NotBlank(message = "역 이름은 비어있을 수 없습니다.")
    private String stationName;

    public StationCreateRequest(String name) {
        this.stationName = name;
    }

    public String getStationName() {
        return stationName;
    }
}