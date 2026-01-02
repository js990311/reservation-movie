# 영화 예매 및 결제 시스템

목적: 단순히 기능을 구현하는 것을 넘어, 다수의 사용자가 동시에 좌석을 선점할 때 발생하는 Race Condition을 해결하고, 외부 결제 API 연동 과정에서 발생할 수 있는 분산 트랜잭션의 데이터 불일치를 엔지니어링적으로 풀어나가는 데 중점을 두었습니다.

## 바로가기

- 배포 URL : https://reservation-movie.vercel.app/
- API 서버 : https://movie.rejs.link/api

## Stack

Frontend: Next.js 16, Tailwind CSS  
Backend: Spring Boot 3, JPA, QueryDsl  
Infra: AWS EC2, Vercel, MySQL, Caddy (HTTPS/SSL), Docker
Database: MariaDB

## 주요 기능

- 영화에 대해 예매
- 예매 상태에서 결제를 통한 상태 예매 상태 확정

## 기술 도전 및 성과

1. 동시성 제어: 가상 자원의 실체화를 통한 가용성 최적화
   문제 상황: 동일 상영 시간 내 좌석 예약 시 Race Condition 발생. 초기 설계 시 '상영-좌석' 간 물리적 관계 부재로 Row-Level Lock 적용 불가.

해결 방안:

Screening 레벨 락은 모든 좌석 예약을 직렬화시켜 가용성을 저해함.

Named Lock/Redis 분산 락 대신 가상 자원의 실체화(ScreeningSeat 엔티티 도입) 선택.

락 경합 단위를 '상영'에서 **'개별 좌석'**으로 최소화하여 인프라 복잡도 없이 성능 개선.

성과: k6 부하 테스트(200 RPS) 결과, Latency(http_req_waiting) 약 30% 개선 (71.7ms → 43.4ms).
