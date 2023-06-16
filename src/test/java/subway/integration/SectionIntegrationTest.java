package subway.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.line.Line;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.dto.section.SectionCreateRequest;
import subway.dto.section.SectionDeleteRequest;
import subway.repository.LineRepository;
import subway.repository.StationRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SectionIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Long lineId;

    @BeforeEach
    void setUp() {
        Line line = new Line(null, new Sections(new ArrayList<>()), "1호선", "blue");
        Line savedLine = lineRepository.save(line);
        lineId = savedLine.getId();
        stationRepository.save(new Station(null, "잠실역"));
        stationRepository.save(new Station(null, "삼성역"));
        stationRepository.save(new Station(null, "포항역"));
        stationRepository.save(new Station(null, "대구역"));
        stationRepository.save(new Station(null, "강남역"));
        stationRepository.save(new Station(null, "부산역"));
    }

    @Test
    void 빈_노선에_구간을_추가하면_추가에_성공한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);

        // expect
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("1개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[0].distance").value(50));
    }

    @Test
    void 노선의_두_역_사이에_역을_추가하면_추가에_성공한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("잠실역", "대구역", 20);

        // expect
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[0].distance").value(20))
                .andExpect(jsonPath("$.result[1].upBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[1].downBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[1].distance").value(30));
    }

    @Test
    void 노선의_상행_종점_역_앞에_새로운_역을_추가하면_추가에_성공한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("대구역", "잠실역", 20);

        // expect
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].distance").value(20))
                .andExpect(jsonPath("$.result[1].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[1].downBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[1].distance").value(50));
    }

    @Test
    void 노선의_하행_종점_역_뒤에_역을_추가하면_추가에_성공한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("삼성역", "대구역", 20);

        // expect
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[0].distance").value(50))
                .andExpect(jsonPath("$.result[1].upBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[1].downBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[1].distance").value(20));
    }

    @Test
    void 노선의_두_역_사이에_역을_추가할_때_기준이_되는_역이_없다면_추가에_실패한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);

        // expect
        SectionCreateRequest newSectionAddRequest = new SectionCreateRequest("포항역", "대구역", 20);
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newSectionAddRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("노선에 기준이 되는 역을 찾을 수 없습니다."));
    }

    @Test
    void 노선의_두_역_사이에_역을_추가할_때_모든_역이_노선에_존재한다면_추가에_실패한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);

        // expect
        SectionCreateRequest newSectionAddRequest = new SectionCreateRequest("삼성역", "잠실역", 20);
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newSectionAddRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("노선에 이미 해당 역이 존재합니다."));
    }

    @ParameterizedTest
    @ValueSource(ints = {50, 51})
    void 기존_구간에_역을_추가할_때_기존_구간보다_길이가_같거나_길다면_추가에_실패한다(int distance) throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);

        // expect
        SectionCreateRequest newSectionAddRequest = new SectionCreateRequest("잠실역", "대구역", distance);
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(newSectionAddRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("새로운 구간의 길이는 기존 구간의 길이보다 작아야 합니다."));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 노선에_구간을_추가할_때_구간의_길이가_0_이하면_추가에_실패한다(int distance) throws Exception {
        // given
        SectionCreateRequest request = new SectionCreateRequest("잠실역", "삼성역", distance);

        // expect
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("구간의 거리는 0보다 커야합니다."));
    }

    @Test
    void 노선에_구간을_추가할_때_존재하지_않는_역이면_추가에_실패한다() throws Exception {
        // given
        SectionCreateRequest request = new SectionCreateRequest("장승배기역", "상도역", 10);

        // expect
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 역이 존재하지 않습니다."));
    }

    @Test
    void 구간의_중간에_있는_역을_삭제할_때_두_구간이_합쳐진다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("삼성역", "대구역", 50);

        // when
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest("삼성역");
        mockMvc.perform(delete("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구간이 삭제되었습니다."));

        // then
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(jsonPath("$.message").value("1개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[0].distance").value(100));
    }

    @ParameterizedTest
    @ValueSource(strings = {"잠실역", "삼성역"})
    void 노선에_구간이_1개일_때_모든_구간이_삭제된다(String station) throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);

        // when
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest(station);
        mockMvc.perform(delete("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구간이 삭제되었습니다."));

        // then
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(jsonPath("$.result.size()").value(0))
                .andExpect(jsonPath("$.message").value("0개의 구간이 조회되었습니다."));
    }

    @Test
    void 노선에_구간이_여러_개_일_때_상행_종점_역을_지울_수_있다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("삼성역", "대구역", 50);

        // when
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest("잠실역");
        mockMvc.perform(delete("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구간이 삭제되었습니다."));

        // then
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(jsonPath("$.message").value("1개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("대구역"))
                .andExpect(jsonPath("$.result[0].distance").value(50));
    }

    @Test
    void 노선에_구간이_여러_개_일_때_하행_종점_역을_지울_수_있다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("삼성역", "대구역", 50);

        mockMvc.perform(get("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // when
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest("대구역");
        mockMvc.perform(delete("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구간이 삭제되었습니다."));

        // then
        mockMvc.perform(get("/lines/{lineId}/sections", lineId))
                .andExpect(jsonPath("$.message").value("1개의 구간이 조회되었습니다."))
                .andExpect(jsonPath("$.result[0].upBoundStationName").value("잠실역"))
                .andExpect(jsonPath("$.result[0].downBoundStationName").value("삼성역"))
                .andExpect(jsonPath("$.result[0].distance").value(50));
    }

    @Test
    void 노선에_없는_역을_지우면_삭제에_실패한다() throws Exception {
        // given
        addSection("잠실역", "삼성역", 50);
        addSection("삼성역", "대구역", 50);

        // expect
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest("포항역");
        mockMvc.perform(delete("/lines/{lineId}/sections", lineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("삭제하려는 역이 구간에 없습니다."));
    }

    void addSection(String upBoundStationName, String downBoundStationName, int distance) throws Exception {
        SectionCreateRequest createRequest = new SectionCreateRequest(upBoundStationName, downBoundStationName, distance);
        mockMvc.perform(post("/lines/{lineId}/sections", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(createRequest)));
    }
}
