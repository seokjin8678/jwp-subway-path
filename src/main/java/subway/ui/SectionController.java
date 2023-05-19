package subway.ui;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.section.SectionService;
import subway.dto.response.Response;
import subway.dto.section.SectionCreateRequest;
import subway.dto.section.SectionDeleteRequest;
import subway.dto.section.SectionResponse;
import subway.exception.section.IllegalSectionException;

@RestController
@RequestMapping("/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/{lindId}")
    public ResponseEntity<Response> findAllSectionsByLineId(@PathVariable Long lindId) {
        List<SectionResponse> sections = sectionService.findSectionsByLineId(lindId);
        return Response.ok()
                .message(sections.size() + "개의 구간이 조회되었습니다.")
                .result(sections)
                .build();
    }

    @PostMapping("/{lineId}")
    public ResponseEntity<Response> createSection(@PathVariable Long lineId,
                                                  @RequestBody @Valid SectionCreateRequest sectionCreateRequest) {
        validateSameStation(sectionCreateRequest);
        sectionService.saveSection(lineId, sectionCreateRequest);
        return Response.ok()
                .message("구간이 생성되었습니다.")
                .build();
    }

    private void validateSameStation(SectionCreateRequest request) {
        if (Objects.equals(request.getUpBoundStationName(), request.getDownBoundStationName())) {
            throw new IllegalSectionException("추가하려는 상행역과 하행역의 이름이 같습니다.");
        }
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Response> deleteSection(@PathVariable Long lineId,
                                                  @RequestBody @Valid SectionDeleteRequest sectionDeleteRequest) {
        sectionService.deleteSection(lineId, sectionDeleteRequest);
        return Response.ok()
                .message("구간이 삭제되었습니다.")
                .build();
    }
}
