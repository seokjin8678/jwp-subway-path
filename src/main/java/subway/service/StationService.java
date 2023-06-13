package subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.station.Station;
import subway.dto.station.StationRequest;
import subway.dto.station.StationResponse;
import subway.exception.station.DuplicateStationException;
import subway.exception.station.StationNotFoundException;
import subway.repository.StationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    @Transactional
    public StationResponse saveStation(StationRequest request) {
        if (stationRepository.existsByName(request.getName())) {
            throw new DuplicateStationException("중복된 역 이름 입니다.");
        }
        Station station = new Station(request.getName());
        stationRepository.save(station);
        return StationResponse.of(station);
    }

    public StationResponse findStationResponseById(Long id) {
        return StationResponse.of(findStationById(id));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException("해당 역을 찾을 수 없습니다."));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(toList());
    }

    @Transactional
    public void updateStation(Long id, StationRequest request) {
        Station station = findStationById(id);
        station.changeName(request.getName());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
