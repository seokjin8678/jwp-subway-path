package subway.dto.section;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SectionCreateRequest {
    private String upBoundStationName;
    private String downBoundStationName;
    private int distance;
}
