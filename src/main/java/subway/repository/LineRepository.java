package subway.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import subway.domain.line.Line;

public interface LineRepository extends Repository<Line, Long> {
    Optional<Line> findById(Long id);

    void save(Line line);

    List<Line> findAll();

    void deleteById(Long id);

    boolean existsByName(String name);
}
