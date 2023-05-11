package subway.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Sql("/line_initialize.sql")
class LineControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 노선이 조회되어야 한다.")
    void findAllLines_success() throws Exception {
        // expect
        mockMvc.perform(get("/lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    @DisplayName("역 정보가 포함된 모든 노선이 조회되어야 한다.")
    void findAllDetailLines_success() throws Exception {
        // expect
        mockMvc.perform(get("/lines/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lineResponse.lineId").value(1))
                .andExpect(jsonPath("$[0].lineResponse.lineName").value("2호선"))
                .andExpect(jsonPath("$[0].lineResponse.color").value("green"))
                .andExpect(jsonPath("$[0].sectionResponses[0].startStationName").value("잠실역"))
                .andExpect(jsonPath("$[0].sectionResponses[0].endStationName").value("삼성역"))
                .andExpect(jsonPath("$[0].sectionResponses[0].distance").value(10))
                .andExpect(jsonPath("$[0].sectionResponses.size()").value(3))

                .andExpect(jsonPath("$[1].lineResponse.lineId").value(2))
                .andExpect(jsonPath("$[1].lineResponse.lineName").value("3호선"))
                .andExpect(jsonPath("$[1].lineResponse.color").value("orange"))
                .andExpect(jsonPath("$[1].sectionResponses[0].startStationName").value("잠실역"))
                .andExpect(jsonPath("$[1].sectionResponses[0].endStationName").value("양재역"))
                .andExpect(jsonPath("$[1].sectionResponses[0].distance").value(10))
                .andExpect(jsonPath("$[1].sectionResponses.size()").value(4))

                .andExpect(jsonPath("$[2].lineResponse.lineId").value(3))
                .andExpect(jsonPath("$[2].lineResponse.lineName").value("4호선"))
                .andExpect(jsonPath("$[2].lineResponse.color").value("blue"))
                .andExpect(jsonPath("$[2].sectionResponses[0].startStationName").value("장승배기역"))
                .andExpect(jsonPath("$[2].sectionResponses[0].endStationName").value("상도역"))
                .andExpect(jsonPath("$[2].sectionResponses[0].distance").value(10))
                .andExpect(jsonPath("$[2].sectionResponses.size()").value(5));
    }
}