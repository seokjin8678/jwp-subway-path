package subway.domain.line;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.exception.line.IllegalSectionException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Sections sections;

    private String name;
    private String color;

    public Line(Long id, Sections sections, String name, String color) {
        this.id = id;
        this.sections = sections;
        this.name = name;
        this.color = color;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        boolean hasUpBoundStation = isExistsStation(newSection.getUpBoundStation());
        boolean hasDownBoundStation = isExistsStation(newSection.getDownBoundStation());
        validateHasStation(hasUpBoundStation, hasDownBoundStation);
        if (hasUpBoundStation) {
            addDownBoundSection(newSection);
        }
        if (hasDownBoundStation) {
            addUpBoundSection(newSection);
        }
    }

    private boolean isExistsStation(Station station) {
        return sections.isExistsStation(station);
    }

    private void validateHasStation(boolean hasUpBoundStation, boolean hasDownBoundStation) {
        if (hasUpBoundStation && hasDownBoundStation) {
            throw new IllegalSectionException("노선에 이미 해당 역이 존재합니다.");
        }
        if (!hasUpBoundStation && !hasDownBoundStation) {
            throw new IllegalSectionException("노선에 기준이 되는 역을 찾을 수 없습니다.");
        }
    }

    private void addDownBoundSection(Section newSection) {
        findSectionBySameUpBoundStation(newSection.getUpBoundStation())
                .ifPresent(section -> {
                    int distance = section.getDistance();
                    int newDistance = newSection.getDistance();
                    validateDistance(distance, newDistance);
                    Station newStation = newSection.getDownBoundStation();
                    newSection.changeUpBoundStation(newStation);
                    newSection.changeDownBoundStation(section.getDownBoundStation());
                    newSection.changeDistance(distance - newDistance);
                    section.changeDownBoundStation(newStation);
                    section.changeDistance(newDistance);
                });
        sections.add(newSection);
    }

    private Optional<Section> findSectionBySameUpBoundStation(Station upBoundStation) {
        return sections.findSectionBySameUpBoundStation(upBoundStation);
    }

    private void validateDistance(int distance, int newDistance) {
        if (distance <= newDistance) {
            throw new IllegalSectionException("새로운 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
        }
    }

    private void addUpBoundSection(Section newSection) {
        findSectionBySameDownBoundStation(newSection.getDownBoundStation())
                .ifPresent(section -> {
                    int distance = section.getDistance();
                    int newDistance = newSection.getDistance();
                    validateDistance(distance, newDistance);
                    Station newStation = newSection.getUpBoundStation();
                    newSection.changeUpBoundStation(section.getUpBoundStation());
                    newSection.changeDownBoundStation(newStation);
                    newSection.changeDistance(distance - newDistance);
                    section.changeUpBoundStation(newStation);
                    section.changeDistance(newDistance);
                });
        sections.add(newSection);
    }

    private Optional<Section> findSectionBySameDownBoundStation(Station downBoundStation) {
        return sections.findSectionBySameDownBoundStation(downBoundStation);
    }

    public void deleteSection(Station station) {
        if (!isExistsStation(station)) {
            throw new IllegalSectionException("삭제하려는 역이 구간에 없습니다.");
        }
        Optional<Section> leftSection = findSectionBySameDownBoundStation(station);
        Optional<Section> rightSection = findSectionBySameUpBoundStation(station);
        if (leftSection.isPresent() && rightSection.isPresent()) {
            mergeSection(leftSection.get(), rightSection.get());
            return;
        }
        leftSection.ifPresent(sections::remove);
        rightSection.ifPresent(sections::remove);
    }

    private void mergeSection(Section leftSection, Section rightSection) {
        sections.remove(leftSection);
        rightSection.changeUpBoundStation(leftSection.getUpBoundStation());
        rightSection.changeDistance(leftSection.getDistance() + rightSection.getDistance());
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }

        Line line = (Line) o;

        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
