package subway.domain.section;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import subway.domain.station.Station;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isExistsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Optional<Section> findSectionBySameUpBoundStation(Station station) {
        return sections.stream()
                .filter(section -> section.isUpBoundStation(station))
                .findAny();
    }

    public Optional<Section> findSectionBySameDownBoundStation(Station station) {
        return sections.stream()
                .filter(section -> section.isDownBoundStation(station))
                .findAny();
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public List<Section> getSections() {
        return sortSections();
    }

    private List<Section> sortSections() {
        return findFirstStation()
                .map(this::getSortedSections)
                .orElse(Collections.emptyList());
    }

    private Optional<Station> findFirstStation() {
        Map<Station, Station> upBoundToDownBound = sections.stream()
                .collect(toMap(Section::getUpBoundStation, Section::getDownBoundStation));
        Set<Station> startStations = new HashSet<>(upBoundToDownBound.keySet());
        startStations.removeAll(upBoundToDownBound.values());
        return startStations.stream()
                .findFirst();
    }

    private List<Section> getSortedSections(Station firstStation) {
        Map<Station, Section> stationToSection = sections.stream()
                .collect(toMap(Section::getUpBoundStation, Function.identity()));
        List<Section> sortedSection = new ArrayList<>();
        Section section = stationToSection.get(firstStation);
        sortedSection.add(section);
        while (stationToSection.size() != sortedSection.size()) {
            section = stationToSection.get(section.getDownBoundStation());
            sortedSection.add(section);
        }
        return sortedSection;
    }
}
