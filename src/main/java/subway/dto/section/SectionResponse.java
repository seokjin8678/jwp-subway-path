package subway.dto.section;

import lombok.AllArgsConstructor;
import lombok.Getter;
import subway.domain.section.Section;

@AllArgsConstructor
@Getter
public class SectionResponse {
    private String upBoundStationName;
    private String downBoundStationName;
    private int distance;

    public static SectionResponse from(Section section) {
        return new SectionResponse(section.getUpBoundStation().getName(),
                section.getDownBoundStation().getName(), section.getDistance());
    }
}
