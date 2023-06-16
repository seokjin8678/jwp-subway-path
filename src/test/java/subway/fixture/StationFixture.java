package subway.fixture;

import subway.domain.station.Station;

public class StationFixture {
    public static Station 잠실역() {
        return new Station(1L, "잠실역");
    }

    public static Station 잠실나루역() {
        return new Station(2L, "잠실나루역");
    }

    public static Station 서울역() {
        return new Station(3L, "서울역");
    }

    public static Station 용산역() {
        return new Station(4L, "용산역");
    }

    public static Station 삼각지역() {
        return new Station(5L, "삼각지역");
    }
}
