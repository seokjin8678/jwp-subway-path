package subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.LineService;
import subway.application.SectionService;
import subway.dto.LineCreateRequest;
import subway.dto.LineDetailResponse;
import subway.dto.LineResponse;
import subway.dto.LineUpdateRequest;
import subway.dto.SectionResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createLine(@RequestBody LineCreateRequest lineRequest) {
        long lineId = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findLineResponses());
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineDetailResponse> findLineDetailById(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findLineResponseById(lineId);
        List<SectionResponse> sectionResponses = sectionService.findSectionsByLineId(lineId);
        return ResponseEntity.ok(new LineDetailResponse(lineResponse, sectionResponses));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId,
                                           @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }
}
