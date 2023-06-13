package subway.ui;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.dto.line.LineRequest;
import subway.dto.line.LineResponse;
import subway.dto.response.Response;
import subway.service.LineService;

@RestController
@RequestMapping("/lines")
@RequiredArgsConstructor
public class LineController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<Response> createLine(@RequestBody LineRequest request) {
        LineResponse response = lineService.saveLine(request);
        return Response.created(URI.create("/lines/" + response.getId()))
                .message("노선이 생성되었습니다.")
                .result(response)
                .build();
    }

    @GetMapping
    public ResponseEntity<Response> findAllLines() {
        List<LineResponse> responses = lineService.findLineResponses();
        return Response.ok()
                .message(responses.size() + "개의 노선이 조회되었습니다.")
                .result(responses)
                .build();
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<Response> findLineById(@PathVariable Long lineId) {
        LineResponse response = lineService.findLineResponseById(lineId);
        return Response.ok()
                .message("노선이 조회되었습니다.")
                .result(response)
                .build();
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Response> updateLine(@PathVariable Long lineId,
                                               @RequestBody LineRequest request) {
        lineService.updateLine(lineId, request);
        return Response.ok()
                .message("노선이 수정되었습니다.")
                .build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Response> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return Response.ok()
                .message("노선이 삭제되었습니다.")
                .build();
    }
}
