package subway.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.line.Line;
import subway.dto.line.LineRequest;
import subway.dto.line.LineResponse;
import subway.exception.line.DuplicateLineException;
import subway.exception.line.LineNotFoundException;
import subway.repository.LineRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateLineException("중복된 노선 이름 입니다.");
        }
        Line line = new Line(request.getName(), request.getColor());
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(toList());
    }

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        return LineResponse.of(persistLine);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException("해당 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line findLine = findLineById(id);
        findLine.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
