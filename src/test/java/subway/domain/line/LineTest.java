package subway.domain.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static subway.fixture.LineFixture.일호선;
import static subway.fixture.StationFixture.삼각지역;
import static subway.fixture.StationFixture.서울역;
import static subway.fixture.StationFixture.용산역;
import static subway.fixture.StationFixture.잠실나루역;
import static subway.fixture.StationFixture.잠실역;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.domain.section.Section;
import subway.exception.line.IllegalSectionException;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LineTest {

    @Test
    void 구간을_추가할_때_노선에_구간이_없으면_추가_되어야_한다() {
        // given
        Line line = 일호선();
        Section section = new Section(1L, 잠실역(), 잠실나루역(), line, 10);

        // when
        line.addSection(section);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(1);
        Section findSection = sections.get(0);
        assertThat(findSection.isUpBoundStation(잠실역()))
                .isTrue();
        assertThat(findSection.isDownBoundStation(잠실나루역()))
                .isTrue();
    }

    @Test
    void 구간을_추가할_때_노선에_해당_구간에_대한_역이_모두_있으면_예외가_발생해야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        Section newSection = new Section(2L, 잠실역(), 잠실나루역(), line, 10);

        // expect
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("노선에 이미 해당 역이 존재합니다.");
    }

    @Test
    void 구간을_추가할_때_노선에_기준이_되는_역이_없으면_예외가_발생해야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        Section newSection = new Section(2L, 서울역(), 용산역(), line, 10);

        // expect
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("노선에 기준이 되는 역을 찾을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12})
    void 구간을_추가할_때_추가할_구가할_구간의_길이가_기존의_구간의_길이보다_길거나_같으면_예외가_발생해야_한다(int distance) {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        Section newSection = new Section(2L, 잠실역(), 서울역(), line, distance);

        // expect
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("새로운 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
    }

    @Test
    void 구간을_추가할_때_상행역_기준_상행_종점에_추가될_수_있어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        line.addSection(new Section(2L, 서울역(), 잠실역(), line, 10));

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(2);
        Section findSection = sections.get(0);
        assertThat(findSection.isUpBoundStation(서울역()))
                .isTrue();
        assertThat(findSection.isDownBoundStation(잠실역()))
                .isTrue();
    }

    @Test
    void 구간을_추가할_때_하행역_기준_하행_종점에_추가될_수_있어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        line.addSection(new Section(2L, 잠실나루역(), 서울역(), line, 10));

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(2);
        Section findSection = sections.get(1);
        assertThat(findSection.isUpBoundStation(잠실나루역()))
                .isTrue();
        assertThat(findSection.isDownBoundStation(서울역()))
                .isTrue();
    }

    @Test
    void 구간을_추가할_때_구간_사이에_구간이_추가될_수_있어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        line.addSection(new Section(2L, 잠실역(), 서울역(), line, 3));

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(2);
        Section findSection = sections.get(0);
        assertThat(findSection.isUpBoundStation(잠실역()))
                .isTrue();
        assertThat(findSection.isDownBoundStation(서울역()))
                .isTrue();
        assertThat(findSection.getDistance())
                .isEqualTo(3);
        assertThat(sections.get(1).getDistance())
                .isEqualTo(7);
    }

    @Test
    void 구간을_삭제할_때_종점의_역을_지울수_있어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));
        line.addSection(new Section(2L, 잠실나루역(), 서울역(), line, 10));

        // when
        line.deleteSection(서울역());

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(1);
        Section findSection = sections.get(0);
        assertThat(findSection.isUpBoundStation(잠실역()))
                .isTrue();
        assertThat(findSection.isDownBoundStation(잠실나루역()))
                .isTrue();
    }

    @Test
    void 구간을_삭제할_때_사이에_있는_역을_지우면_구간이_합쳐져야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));
        line.addSection(new Section(2L, 잠실역(), 서울역(), line, 3));

        // when
        line.deleteSection(서울역());

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .hasSize(1);
        Section findSection = sections.get(0);
        assertThat(findSection.isUpBoundStation(잠실역())).isTrue();
        assertThat(findSection.isDownBoundStation(잠실나루역())).isTrue();
    }

    @Test
    void 구간을_삭제할_때_기존의_구간이_하나이면_모든_구간이_삭제되어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));

        // when
        line.deleteSection(잠실역());

        // then
        List<Section> sections = line.getSections();
        assertThat(sections)
                .isEmpty();
    }

    @Test
    void 구간을_조회하면_정렬된_구간이_조회_되어야_한다() {
        // given
        Line line = 일호선();
        line.addSection(new Section(1L, 잠실역(), 잠실나루역(), line, 10));
        line.addSection(new Section(2L, 잠실나루역(), 서울역(), line, 10));
        line.addSection(new Section(3L, 용산역(), 잠실역(), line, 5));
        line.addSection(new Section(4L, 잠실나루역(), 삼각지역(), line, 5));

        // when
        List<Section> sections = line.getSections();

        // then
        assertAll(
                () -> assertThat(sections).hasSize(4),
                () -> assertThat(sections.get(0).getUpBoundStation()).isEqualTo(용산역()),
                () -> assertThat(sections.get(0).getDownBoundStation()).isEqualTo(잠실역()),

                () -> assertThat(sections.get(1).getUpBoundStation()).isEqualTo(잠실역()),
                () -> assertThat(sections.get(1).getDownBoundStation()).isEqualTo(잠실나루역()),

                () -> assertThat(sections.get(2).getUpBoundStation()).isEqualTo(잠실나루역()),
                () -> assertThat(sections.get(2).getDownBoundStation()).isEqualTo(삼각지역()),

                () -> assertThat(sections.get(3).getUpBoundStation()).isEqualTo(삼각지역()),
                () -> assertThat(sections.get(3).getDownBoundStation()).isEqualTo(서울역())
        );
    }
}
