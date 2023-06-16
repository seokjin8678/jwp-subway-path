package subway.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.line.Line;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.dto.line.LineRequest;
import subway.dto.line.LineResponse;
import subway.dto.section.SectionCreateRequest;
import subway.dto.section.SectionDeleteRequest;
import subway.dto.section.SectionResponse;
import subway.exception.line.DuplicateLineException;
import subway.exception.line.LineNotFoundException;
import subway.exception.station.StationNotFoundException;
import subway.repository.LineRepository;
import subway.repository.StationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateLineException("중복된 노선 이름 입니다.");
        }
        Line line = createLine(request);
        Line savedLine = lineRepository.save(line);
        return LineResponse.from(savedLine);
    }

    private Line createLine(LineRequest request) {
        return new Line(null, new Sections(new ArrayList<>()), request.getName(),
                request.getColor());
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::from)
                .collect(toList());
    }

    public LineResponse findLineResponseById(Long lineId) {
        Line persistLine = findLineById(lineId);
        return LineResponse.from(persistLine);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest request) {
        Line line = findLineById(lineId);
        line.changeName(request.getName());
        line.changeColor(request.getColor());
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public List<SectionResponse> findSectionsById(Long lineId) {
        Line line = findLineById(lineId);
        return line.getSections().stream()
                .map(SectionResponse::from)
                .collect(toList());
    }

    @Transactional
    public void addSection(Long lineId, SectionCreateRequest request) {
        Line line = findLineById(lineId);
        Section section = createSection(request, line);
        line.addSection(section);
    }

    private Section createSection(SectionCreateRequest request, Line line) {
        Station upBoundStation = findStationByName(request.getUpBoundStationName());
        Station downBoundStation = findStationByName(request.getDownBoundStationName());
        return new Section(null, upBoundStation, downBoundStation, line,
                request.getDistance());
    }

    private Station findStationByName(String stationName) {
        return stationRepository.findByName(stationName)
                .orElseThrow(() -> new StationNotFoundException("해당 역이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteSection(Long lineId, SectionDeleteRequest request) {
        Line line = findLineById(lineId);
        Station station = findStationByName(request.getStationName());
        line.deleteSection(station);
    }
}
