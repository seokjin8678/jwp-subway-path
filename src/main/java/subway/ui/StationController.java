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
import subway.dto.response.Response;
import subway.dto.station.StationRequest;
import subway.dto.station.StationResponse;
import subway.service.StationService;

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<Response> createStation(@RequestBody StationRequest request) {
        StationResponse response = stationService.saveStation(request);
        return Response.created(URI.create("/stations/" + response.getId()))
                .message("역이 생성되었습니다.")
                .result(response)
                .build();
    }

    @GetMapping
    public ResponseEntity<Response> showStations() {
        List<StationResponse> responses = stationService.findAllStationResponses();
        return Response.ok()
                .message(responses.size() + "개의 역이 조회되었습니다.")
                .result(responses)
                .build();
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<Response> showStation(@PathVariable Long stationId) {
        StationResponse response = stationService.findStationResponseById(stationId);
        return Response.ok()
                .message("역이 조회되었습니다.")
                .result(response)
                .build();
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<Response> updateStation(@PathVariable Long stationId,
                                                  @RequestBody StationRequest request) {
        stationService.updateStation(stationId, request);
        return Response.ok()
                .message("역이 수정되었습니다.")
                .build();
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<Response> deleteStation(@PathVariable Long stationId) {
        stationService.deleteStationById(stationId);
        return Response.ok()
                .message("역이 삭제되었습니다.")
                .build();
    }
}
