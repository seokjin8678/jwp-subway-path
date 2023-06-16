package subway.fixture;

import java.util.ArrayList;
import subway.domain.line.Line;
import subway.domain.section.Sections;

public class LineFixture {
    public static Line 일호선() {
        return new Line(1L, new Sections(new ArrayList<>()), "1호선", "blue");
    }
}
