package subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static subway.RestDocsHelper.constraint;
import static subway.RestDocsHelper.prettyDocument;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import subway.application.LineService;
import subway.application.SectionService;
import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.section.SectionResponse;

@WebMvcTest(LineController.class)
@AutoConfigureRestDocs
class LineControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LineService lineService;

    @MockBean
    SectionService sectionService;

    @Test
    @DisplayName("/lines로 POST 요청과 함께 line의 정보를 보내면, HTTP 201 코드와 응답이 반환되어야 한다.")
    void createLine_success() throws Exception {
        // given
        LineCreateRequest request = new LineCreateRequest("2호선", "bg-red-600");
        willReturn(1L)
                .given(lineService)
                .saveLine(any(LineCreateRequest.class));

        // expect
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/lines/1"))
                .andExpect(jsonPath("$.message").value("노선이 생성되었습니다."))
                .andDo(prettyDocument("lines/create",
                        requestFields(
                                fieldWithPath("lineName").description("노선 이름"),
                                fieldWithPath("color").description("노선 색깔")
                                        .attributes(constraint(" bg-(소문자로 된 색 단어)-(1~9로 시작하는 100 단위의 수)"))
                        )));
    }

    @Test
    @DisplayName("Line을 생성할 때, Line의 색이 형식에 맞지 않으면 HTTP 400 코드와 응답이 반환되어야 한다.")
    void createLine_invalidColorFormat() throws Exception {
        // given
        LineCreateRequest request = new LineCreateRequest("2호선", "red");

        // expect
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.color")
                        .value("노선의 색은 bg-(소문자로 된 색 단어)-(1~9로 시작하는 100 단위의 수) 여야 합니다."));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Line을 생성할 때, Line의 이름과 색은 비어있으면 HTTP 400 코드와 응답이 반환되어야 한다.")
    void createLine_notBlank(String input) throws Exception {
        // given
        LineCreateRequest request = new LineCreateRequest(input, input);

        // expect
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.lineName").exists())
                .andExpect(jsonPath("$.validation.color").exists());
    }

    @Test
    @DisplayName("Line을 생성할 때, LineName이 15글자를 넘으면 HTTP 400 코드와 응답이 반환되어야 한다.")
    void createLine_lineNameOverThan15Characters() throws Exception {
        // given
        LineCreateRequest request = new LineCreateRequest("1234567890123456", "bg-green-300");

        // expect
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.lineName").value("노선의 이름은 15글자를 초과할 수 없습니다."));
    }

    @Test
    @DisplayName("Line을 생성할 때, Color가 15글자를 넘으면 HTTP 400 코드와 응답이 반환되어야 한다.")
    void createLine_ColorOverThan15Characters() throws Exception {
        // given
        LineCreateRequest request = new LineCreateRequest("1호선", "bg-redgreenblue-300");

        // expect
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.color").value("노선의 색은 15글자를 초과할 수 없습니다."));
    }

    @Test
    @DisplayName("/lines로 GET 요청을 보내면, HTTP 200 코드와 응답이 반환되어야 한다.")
    void findAllLines_success() throws Exception {
        // given
        List<LineResponse> response = List.of(
                new LineResponse(1L, "1호선", "bg-blue-300"),
                new LineResponse(2L, "2호선", "bg-green-300"),
                new LineResponse(3L, "3호선", "bg-yellow-300")
        );
        willReturn(response).given(lineService).findLineResponses();

        // expect
        mockMvc.perform(get("/lines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(prettyDocument("lines/inquiry"));
    }

    @Test
    @DisplayName("/lines/detail로 GET 요청을 보내면, HTTP 200 코드와 응답이 반환되어야 한다.")
    void findAllDetailLines() throws Exception {
        // given
        List<LineResponse> response = List.of(
                new LineResponse(1L, "1호선", "bg-blue-300")
        );
        willReturn(response).given(lineService).findLineResponses();
        given(sectionService.findSectionsByLineId(anyLong()))
                .willReturn(SectionResponsesFixture());

        // expect
        mockMvc.perform(get("/lines/detail")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(prettyDocument("lines/detail-inquiry"));
    }

    @Test
    @DisplayName("/lines/:lineId로 GET 요청을 보내면, HTTP 200 코드와 응답이 반환되어야 한다.")
    void findLineDetailById_success() throws Exception {
        // given
        willReturn(new LineResponse(1L, "1호선", "bg-blue-300"))
                .given(lineService)
                .findLineResponseById(anyLong());
        given(sectionService.findSectionsByLineId(anyLong()))
                .willReturn(SectionResponsesFixture());

        // expect
        mockMvc.perform(get("/lines/{lineId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(prettyDocument("lines/detail-inquiry-single",
                        pathParameters(
                                parameterWithName("lineId").description("노선 ID")
                        )));
    }

    @Test
    @DisplayName("/lines/:lineId로 PUT 요청과 line의 정보를 보내면, HTTP 200 코드와 응답이 반환되어야 한다.")
    void updateLine_success() throws Exception {
        // given
        LineUpdateRequest request = new LineUpdateRequest("2호선", "bg-green-300");
        doNothing()
                .when(lineService)
                .updateLine(anyLong(), any(LineUpdateRequest.class));

        // expect
        mockMvc.perform(put("/lines/{lineId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(prettyDocument("lines/update",
                        pathParameters(
                                parameterWithName("lineId").description("노선 ID")
                        ),
                        requestFields(
                                fieldWithPath("lineName").description("노선 이름"),
                                fieldWithPath("color").description("노선 색깔")
                                        .attributes(constraint(" bg-(소문자로 된 색 단어)-(1~9로 시작하는 100 단위의 수)"))
                        )));
    }

    @Test
    @DisplayName("/lines/:lineId로 DELETE 요청을 보내면, HTTP 200 코드와 응답이 반환되어야 한다.")
    void deleteLine_success() throws Exception {
        // given
        doNothing()
                .when(lineService)
                .deleteLineById(anyLong());

        // expect
        mockMvc.perform(delete("/lines/{lineId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(prettyDocument("lines/delete",
                        pathParameters(
                                parameterWithName("lineId").description("노선 ID")
                        )));
    }

    private List<SectionResponse> SectionResponsesFixture() {
        return List.of(
                new SectionResponse("서울역", "용산역", 10),
                new SectionResponse("용산역", "노량진역", 10),
                new SectionResponse("노량진역", "대방역", 10)
        );
    }
}
