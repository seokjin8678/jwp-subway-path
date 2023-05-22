package subway.application.price;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.domain.path.Path;
import subway.domain.price.Price;
import subway.domain.station.Station;

class DifferentialDistancePricePolicyTest {

    DifferentialDistancePricePolicy differentialDistancePricePolicy = new DifferentialDistancePricePolicy();

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    @DisplayName("거리가 10km 이하이면 1250원이 계산되어야 한다.")
    void calculate_lessThen10(int distance) {
        // given
        Path path = new Path(List.of(
                new Station("서울역"),
                new Station("용산역"),
                new Station("잠실역")
        ), distance, Set.of(1L));

        // when
        Price price = differentialDistancePricePolicy.calculate(path);

        // then
        assertThat(price.getAmount())
                .isEqualTo(1250);
    }

    /*
     * 10 1350
     * 15 1350
     * 20 1450
     * 25 1550
     * 30 1650
     * 35 1750
     * 40 1850
     * 45 1950
     * 50 2050
     */
    @ParameterizedTest
    @CsvSource(value = {"11:1350", "15:1350", "16:1450", "40:1850", "50:2050"}, delimiter = ':')
    @DisplayName("거리가 10~50km 사이면 1250 + 거리 5km 마다 100원이 추가되어야 한다.")
    void calculate_between10And50(int distance, int expect) {
        // given
        Path path = new Path(List.of(
                new Station("서울역"),
                new Station("용산역"),
                new Station("잠실역")
        ), distance, Set.of(1L));

        // when
        Price price = differentialDistancePricePolicy.calculate(path);

        // then
        assertThat(price.getAmount())
                .isEqualTo(expect);
    }

    /*
     * 51 2150
     * 58 2150
     * 66 2250
     * 74 2350
     * 82 2450
     * 90 2550
     * 98 2650
     * ...
     */
    @ParameterizedTest
    @CsvSource(value = {"51:2150", "58:2150", "59:2250", "98:2650"}, delimiter = ':')
    @DisplayName("거리가 50km를 초과하면 8km 마다 100원이 추가되어야 한다.")
    void calculate_overThan50(int distance, int expect) {
        // given
        Path path = new Path(List.of(
                new Station("서울역"),
                new Station("용산역"),
                new Station("잠실역")
        ), distance, Set.of(1L));

        // when
        Price price = differentialDistancePricePolicy.calculate(path);

        // then
        assertThat(price.getAmount())
                .isEqualTo(expect);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("거리가 1 미만이면 예외가 발생해야 한다.")
    void calculate_lessThan1(int distance) {
        // given
        Path path = new Path(List.of(
                new Station("서울역"),
                new Station("용산역"),
                new Station("잠실역")
        ), distance, Set.of(1L));

        // expect
        assertThatThrownBy(() -> differentialDistancePricePolicy.calculate(path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 1 이상의 값이어야 합니다.");
    }
}
