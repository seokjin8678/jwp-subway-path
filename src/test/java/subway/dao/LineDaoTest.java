package subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.entity.LineEntity;
import subway.domain.Line;
import subway.domain.LineColor;
import subway.domain.LineName;

@JdbcTest
class LineDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Line을 저장할 수 있어야 한다.")
    void insert_success() {
        // given
        Line line = new Line(new LineName("1호선"), new LineColor("bg-blue-300"));

        // when
        long lineId = lineDao.insert(line);

        // then
        assertThat(lineDao.existsById(lineId))
                .isTrue();
    }

    @Test
    @DisplayName("Line을 수정할 수 있어야 한다.")
    void update_success() {
        // given
        long lineId = insertLine("1호선", "bg-blue-300");

        // when
        lineDao.update(lineId, new Line(new LineName("2호선"), new LineColor("bg-green-300")));

        // then
        LineEntity line = lineDao.findById(lineId).get();
        assertThat(line.getName())
                .isEqualTo("2호선");
        assertThat(line.getColor())
                .isEqualTo("bg-green-300");
    }

    @Test
    @DisplayName("모든 Line을 조회할 수 있어야 한다.")
    void findAll_success() {
        // given
        insertLine("1호선", "bg-blue-300");
        insertLine("2호선", "bg-green-300");
        insertLine("3호선", "bg-yellow-300");

        // when
        List<LineEntity> lines = lineDao.findAll();

        // then
        assertThat(lines)
                .hasSize(3);
    }

    @ParameterizedTest
    @CsvSource(value = {"1호선:true", "2호선:false"}, delimiter = ':')
    @DisplayName("Line의 이름으로 Line이 있는지 확인할 수 있어야 한다.")
    void existsByName_true(String stationName, boolean expect) {

        // given
        insertLine("1호선", "bg-blue-300");

        // expect
        assertThat(lineDao.existsByName(stationName))
                .isEqualTo(expect);
    }

    @Test
    @DisplayName("존재하지 않는 Line의 Id로 조회하면 빈 Optional이 반환되어야 한다.")
    void findById_emptyOptional() {
        // when
        Optional<LineEntity> line = lineDao.findById(2L);

        // then
        assertThat(line)
                .isEmpty();
    }

    @Test
    @DisplayName("Id로 Line을 조회할 수 있어야 한다.")
    void findById_success() {
        // given
        long lineId = insertLine("1호선", "bg-blue-300");

        // when
        Optional<LineEntity> line = lineDao.findById(lineId);

        // then
        assertThat(line)
                .isPresent();
        assertThat(line.get().getName())
                .isEqualTo("1호선");
        assertThat(line.get().getColor())
                .isEqualTo("bg-blue-300");
    }

    @Test
    @DisplayName("Line의 Id로 Line을 삭제할 수 있어야 한다.")
    void deleteById_success() {
        // given
        long lineId = insertLine("1호선", "bg-blue-300");

        // when
        lineDao.deleteById(lineId);

        // then
        assertThat(lineDao.doesNotExistById(lineId))
                .isTrue();
    }

    private long insertLine(String lineName, String lineColor) {
        Line line = new Line(new LineName(lineName), new LineColor(lineColor));
        return lineDao.insert(line);
    }
}
