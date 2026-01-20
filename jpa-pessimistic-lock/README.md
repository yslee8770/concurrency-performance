# Pessimistic Lock Deadlock & Timeout

## 실험 목적

비관적 락(`SELECT ... FOR UPDATE`)은  
동시성 문제를 해결해 주지만, **운영 관점의 실패 처리 전략까지 포함하지 않으면 오히려 장애의 원인이 된다.**

이 실험의 목적은 다음을 **실제 DB + 테스트 코드로 증명**하는 것이다.

- Deadlock과 Lock Wait Timeout은 **서로 다른 실패 유형**이다
- 두 실패는 **같은 재시도 정책을 적용하면 안 된다**
- MySQL과 PostgreSQL은 **에러 식별 방식(SQLSTATE)이 다르다**
- 따라서 **DB에 독립적인 운영 정책 분기**가 필요하다

---

## 실험 환경

- Java / Spring Boot / JPA (Hibernate)
- Pessimistic Lock (`SELECT ... FOR UPDATE`)
---

## Deadlock 재현 (MySQL / PostgreSQL)

### 시나리오
- 동일한 두 row(Seat)
- 트랜잭션 A: 1 → 2 순서로 락 획득
- 트랜잭션 B: 2 → 1 순서로 락 획득
- 첫 번째 락 획득 후 일정 시간 유지

→ **락 순서가 교차되며 순환 대기 발생**

### 결과

#### MySQL
- Error Code: **1213**
- SQLSTATE: **40001**
- 의미: Deadlock 발생, 트랜잭션 롤백

#### PostgreSQL
- SQLSTATE: **40P01** (`deadlock_detected`)
- 의미: Deadlock 발생, 트랜잭션 롤백

**결론**  
두 DB 모두 Deadlock을 즉시 감지하고 트랜잭션을 실패시킨다.

---

## Deadlock은 Retry로 수렴 가능한가? (MySQL)

### 정책 가정
- Deadlock은 **일시적인 충돌(transient failure)** 로 간주
- 제한된 횟수 내에서 **Retry 허용**

### 실험 조건
- 동시 요청: 2
- 최대 retry 횟수: 7

### 계측 결과

| 항목 | 값 |
|---|---|
| 완료 트랜잭션 수 | 2 |
| totalAttempts | 3 |
| maxAttempt | 2 |

### 해석
- Deadlock은 실제로 발생했으나
- **1회 재시도로 정상 수렴**

→ **Deadlock은 Retryable 하다는 것을 확인**

---

## Lock Wait Timeout 재현 (MySQL / PostgreSQL)

### 시나리오
- 트랜잭션 A가 row 락을 장시간 점유
- 트랜잭션 B가 동일 row에 접근 시도

### 결과

#### MySQL
- Error Code: **1205**
- SQLSTATE: **40001**
- 메시지: `Lock wait timeout exceeded`

#### PostgreSQL
- SQLSTATE: **55P03**
- `SET LOCAL lock_timeout` 적용
- 락 대기 시간이 초과되면 statement 취소

**특징**
- Deadlock이 아닌 **대기 초과(timeout)** 로 실패
- 구조적 문제에 가까움

---

## 6. Lock Wait Timeout은 왜 Fail-Fast 인가

### Deadlock vs Lock Wait Timeout 비교

| 구분 | Deadlock | Lock Wait Timeout |
|---|---|---|
| 원인 | 순환 락 의존 | 장시간 락 점유 |
| 성격 | 일시적 충돌 | 구조적 병목 |
| Retry 효과 | 있음 | 거의 없음 |
| Retry 위험 | 낮음 | 부하 증폭 |

### 결론
- Lock Wait Timeout은 재시도해도
  - 동일 락을 다시 기다리게 됨
  - TPS 저하, 스레드 고갈 위험 증가
- 따라서 **기본 정책은 Fail-Fast가 합리적**

---

## 정책 요약

```text
- Deadlock
  - MySQL: 1213
  - PostgreSQL: 40P01
  → Retry (제한된 횟수)

- Lock Wait Timeout
  - MySQL: 1205
  - PostgreSQL: 55P03
  → Fail-Fast
