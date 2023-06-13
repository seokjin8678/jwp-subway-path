# jwp-subway-path

### 기능 요구 사항

- [ ] 노선에 역 등록
    - [ ] 노선에 역을 등록할 때는 상행, 하행 상관 없이 등록 할 수 있다.
        - [ ] 없는 노선에 역을 추가할 경우, 예외가 발생한다.
        - [ ] 없는 역을 입력할 경우, 예외가 발생한다.
    - [ ] upBoundStation이 상행역이고, downBoundStation이 하행역이다.
        - [ ] 노선에 구간이 없으면 바로 추가된다.
        - [ ] 상행역과 하행역 둘 다 노선에 있거나, 없으면 예외가 발생해야 한다.
        - [ ] 노선에 역을 추가할 때, 두 역 중 반드시 하나만 해당 노선에 있어야 한다.
        - 상행역이 이미 노선에 있는 역이라면, 하행역이 상행역 기준 하행역으로 추가된다.
        - 하행역이 이미 노선에 있는 역이라면, 상행역이 하행역 기준 상행역으로 추가된다.
    - [ ] 추가되는 구간의 길이가 기존 구간의 길이보다 작아야 한다.
    - [ ] 구간의 길이는 0보다 커야 한다.

- [ ] 노선에 역 삭제
    - [ ] 노선에서 역을 삭제할 때는, 노선이 재배치 되어야 한다.
        - [ ] A-B-C-D 역이 있는 노선에서 C를 삭제하면 A-B-D 순으로 재배치 된다.
        - [ ] A-2km-B, B-3km-C, C-4km-D 일 경우, 구간의 길이가 B-7km-D가 되어야 한다.
    - [ ] 노선에 등록된 역이 2개일때, 하나의 역을 삭제하면 두 역이 삭제된다.
