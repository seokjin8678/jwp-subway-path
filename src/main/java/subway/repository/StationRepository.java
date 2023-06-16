package subway.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import subway.domain.station.Station;

public interface StationRepository extends Repository<Station, Long> {
    Station save(Station station);

    Optional<Station> findById(Long id);

    Optional<Station> findByName(String name);

    List<Station> findAll();

    void deleteById(Long id);

    boolean existsByName(String name);
}
