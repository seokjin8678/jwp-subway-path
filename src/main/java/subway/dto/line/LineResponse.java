package subway.dto.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import subway.domain.line.Line;

@AllArgsConstructor
@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }
}
