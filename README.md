# jwp-subway-path

### [API 명세](http://localhost:8080/docs/index.html)

### 기능 요구 사항

- [x] 노선에 역 등록
  - [x] 노선에 역을 등록할 때는 상행, 하행 상관 없이 등록 할 수 있다.
    - [x] 없는 노선에 역을 추가할 경우, 예외가 발생한다.
    - [x] 없는 역을 입력할 경우, 예외가 발생한다.
  - [x] upBoundStation이 상행역이고, downBoundStation이 하행역이다.
    - [x] 노선에 구간이 없으면 바로 추가된다.
    - [x] 상행역과 하행역 둘 다 노선에 있거나, 없으면 예외가 발생해야 한다. 
    - [x] 노선에 역을 추가할 때, 두 역 중 반드시 하나만 해당 노선에 있어야 한다.
    - 상행역이 이미 노선에 있는 역이라면, 하행역이 상행역 기준 하행역으로 추가된다.
    - 하행역이 이미 노선에 있는 역이라면, 상행역이 하행역 기준 상행역으로 추가된다.
  - [x] 추가되는 구간의 길이가 기존 구간의 길이보다 작아야 한다.
  - [x] 구간의 길이는 0보다 커야 한다.

- [x] 노선에 역 삭제
  - [x] 노선에서 역을 삭제할 때는, 노선이 재배치 되어야 한다.
    - [x] A-B-C-D 역이 있는 노선에서 C를 삭제하면 A-B-D 순으로 재배치 된다.
    - [x] A-2km-B, B-3km-C, C-4km-D 일 경우, 구간의 길이가 B-7km-D가 되어야 한다.
  - [x] 노선에 등록된 역이 2개일때, 하나의 역을 삭제하면 두 역이 삭제된다.

- [x] 경로 조회 기능
  - [x] 출발역과 도착역 사이 최단 경로를 구할 수 있다.
  - [x] 최단 거리 경로와 함께 총 거리 정보를 응답해야 한다.
  - [x] 한 노선이 아니라, 여러 노선의 환승도 가능해야 한다.
  - [x] 경로 조회 시, 요금 정보도 포함해야 한다.
    - [x] 10km 이하: 1250원
    - [x] 10~50km: 5km 마다 100원 추가
    - [x] 50km 초과: 8km 마다 100원 추가

- [x] 노선별 추가 요금
  - [x] 추가 요금이 있는 노선을 이용할 경우 측정된 요금에 추가한다
  - [x] 경로 중 추가요금이 있는 노선을 환승할 경우, 가장 높은 금액의 추가 요금만 적용한다.

- [ ] 연령별 할인 정책
  - [ ] 연령에 따른 요금 할인 정책을 반영한다.
  - [ ] 청소년(13세 이상 ~ 19세 미만)
    - 운임에서 350원을 공제한 금액의 20% 할인
  - [ ] 어린이(6세 이상 ~ 13세 미만)
    - 운임에서 350원을 공제한 금액의 20% 할인
