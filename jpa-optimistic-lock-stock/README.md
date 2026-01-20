# JPA Optimistic Lock — 재고 감소 동시성 실험

---

## 실험 환경

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA (Hibernate 6.5.x)
- H2 (MySQL Mode)
- Isolation: DB 기본 설정
- Lock 전략: **Optimistic Lock (`@Version`)**

---

## 핵심 시나리오

### 재고 감소 문제
- 하나의 재고(row)에 대해 **동시에 다수 요청**이 들어오는 상황
- 모든 요청은 `quantity = 1` 감소를 시도
- Optimistic Lock 충돌 발생 가능

---

## 결과 코드 분류 (고정)

| 코드 | 의미 |
|---|---|
| `SUCCESS` | 정상적으로 재고 감소 성공 |
| `OUT_OF_STOCK` | 비즈니스 실패 (재고 부족) |
| `OPTIMISTIC_CONFLICT_MAX_ATTEMPTS` | 기술/정책 실패 (재시도 한계 초과) |

---

## STEP 1 — Retry 없는 Baseline (Q=1, 요청=100)

### 목적
- Optimistic Lock 충돌이 **그대로 에러로 노출되는 상태**를 확인

### 결과
SUCCESS = 1
OUT_OF_STOCK = 90
OPTIMISTIC_FAILURE = 9
elapsedMs = 182


### 해석
- 재고는 1개뿐이므로 **성공은 1**
- 하지만 충돌 9건이 **비즈니스 실패로 수렴하지 못하고 기술 실패로 노출**
- 즉, **retry 없이는 운영에서 에러율이 발생**

---

## STEP 2 — Retry 적용 (Q=1, 요청=100)

### Retry 정책
- maxAttempts = 30
- exponential backoff (5ms ~ 50ms)

### 결과
SUCCESS = 1
OUT_OF_STOCK = 99
OPTIMISTIC_CONFLICT_MAX_ATTEMPTS = 0


### 해석
- 모든 충돌이 **재시도로 흡수**
- 최종 결과가 **비즈니스 관점(SUCCESS / OUT_OF_STOCK)으로만 수렴**
- Optimistic Lock은 “락”이 아니라 **충돌을 전제로 한 설계**임을 확인

---

## STEP 3 — 일반화 검증 (Q=10, 요청=100, with retry)

### 결과
SUCCESS = 10
OUT_OF_STOCK = 90
OPTIMISTIC_CONFLICT_MAX_ATTEMPTS = 0
elapsedMs = 250
totalAttempts = 141
totalConflicts = 41
avgAttempts = 1.41


### 해석
- 극단 케이스(Q=1)가 아닌 **일반 재고(Q=10)**에서도 정확히 수렴
- 평균적으로 **요청당 1.41회 시도 비용**으로 충돌을 흡수
- 단순 “장난 실험”이 아니라 **일반화 가능한 패턴**임을 증명

---

## STEP 4 — Retry 정책 튜닝 비교 (Q=10, 요청=100)

| Policy | elapsedMs | totalAttempts | totalConflicts | avgAttempts |
|---|---:|---:|---:|---:|
| P1: max10, no backoff | 322 | 146 | 46 | 1.46 |
| P2: max30, no backoff | 110 | 137 | 37 | 1.37 |
| P3: max30, exp(5~50) | 148 | 139 | 39 | 1.39 |

### 해석
- backoff는 **항상 성능을 높이지는 않음**
- 하지만 DB 부하 완화/폭주 방지 관점에서는 선택지
- 정책은 정답이 아니라 **트레이드오프**임을 확인

---

## STEP 5 — 의도적 정책 실패 유도 (최종)

### 목적
- 정책이 약할 경우 **어떤 실패가 발생하는지**를 명확히 증명

### 설정
- `maxAttempts = 1` (retry 사실상 없음)
- Q=10, 요청=100

### 결과
SUCCESS = 10
OUT_OF_STOCK = 47
OPTIMISTIC_CONFLICT_MAX_ATTEMPTS = 43
elapsedMs = 328
remainingQty = 0


### 해석 
- 재고는 모두 소진되었지만,
- **47건만 비즈니스 실패로 수렴**
- **43건은 정책 실패(OPTIMISTIC_CONFLICT_MAX_ATTEMPTS)** 로 종료

 **Retry 정책이 약하면, 실패는 비즈니스가 아니라 기술 문제로 노출된다.**

---

## 트랜잭션 증명 로그

모든 retry 시도는 **새 트랜잭션**에서 실행됨:

[attempt=1] isNewTx=true, txActive=true


- 매 시도마다 새로운 트랜잭션에서 flush/commit 수행
- “말로만 retry”가 아닌 **실제 트랜잭션 단위 재시도**임을 로그로 증명

---

## 결론

- Optimistic Lock은 충돌을 막는 기술이 아니다
- 충돌을  흡수하는 설계가 핵심
- Retry 정책이 충분하면:
  - 실패는 비즈니스 실패로 수렴
- Retry 정책이 부족하면:
  - 실패는 기술 실패로 노출

---
