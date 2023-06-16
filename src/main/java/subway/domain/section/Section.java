package subway.domain.section;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import subway.domain.line.Line;
import subway.domain.station.Station;
import subway.exception.line.IllegalSectionException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Station upBoundStation;

    @OneToOne(fetch = FetchType.EAGER)
    private Station downBoundStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    private int distance;

    public Section(Long id, Station upBoundStation, Station downBoundStation, Line line,
                   int distance) {
        validateDistance(distance);
        this.id = id;
        this.upBoundStation = upBoundStation;
        this.downBoundStation = downBoundStation;
        this.line = line;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간의 거리는 0보다 커야합니다.");
        }
    }

    public boolean hasStation(Station station) {
        return Objects.equals(upBoundStation, station) || Objects.equals(downBoundStation, station);
    }

    public boolean isUpBoundStation(Station station) {
        return Objects.equals(upBoundStation, station);
    }

    public boolean isDownBoundStation(Station station) {
        return Objects.equals(downBoundStation, station);
    }

    public void changeUpBoundStation(Station station) {
        this.upBoundStation = station;
    }

    public void changeDownBoundStation(Station station) {
        this.downBoundStation = station;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }

        Section section = (Section) o;

        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public Station getUpBoundStation() {
        return upBoundStation;
    }

    public Station getDownBoundStation() {
        return downBoundStation;
    }

    public int getDistance() {
        return distance;
    }
}
