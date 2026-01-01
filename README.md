# 상태 다이어그램

## 예매의 상태변화

```mermaid
stateDiagram-v2
        state "예매 전반" as Reservation{
            [*] --> Pending : 사용자가 성공적으로 예매
            Pending --> Canceled : 제한 시간 내 결제 실패
            Pending --> Confirm : 결제가 성공적으로 완료됨
            Confirm --> Canceled : 사용자 변심으로 결제 취소
            Confirm --> [*] : 영화상영시간이 지남
            Canceled --> [*] : 취소된 영화표는 다시 조작할 수 없음
        }
```

## Payment의 상태

```mermaid
stateDiagram-v2
    state "결제 엔티티의 상태" as Payment{
        [*] --> Ready : 결제 고유 id 발급
        Ready --> Timeout: 결제제한 시간이 지남.
        Ready-->Verifying: 사용자가 금액을 지불했고 결제가 확정되지는 않았음
        Verifying --> Paid : 결제 완료(검증여부와 무관)
        Verifying --> Aborted : 검증 실패로 인한 결제 거부
        Timeout --> [*]
        Aborted --> [*]
        Paid --> [*] : 사용자 환불이 있어도 변화없음
    }
```

Paid는 사용자가 환불을 요청해도 그 상태를 변경하지 않는다.
결제 취소에 대한 상태는 PaymentCancel에 의해서 관리하도록 할 것

## PaymentCancel의 상태

```mermaid
stateDiagram-v2
        state "결제 취소 엔티티의 상태" as PaymentCancel{
        [*] --> REQUIRED : 결제 취소 요청 발생
        REQUIRED --> CANCELED : [성공] PG사 승인 완료
        REQUIRED --> FAILED : [실패] 비즈니스 로직 위반(금액 초과 등)
        REQUIRED --> SKIPPED : [불필요] 이미 취소됨 또는 미결제 건

        CANCELED --> [*]
        FAILED --> [*]
        SKIPPED --> [*]
}
```

- 재시도 해야하는 경우 상태를 변경하지 않는다.

## 결제 성공 시나리오

```mermaid
stateDiagram-v2
        state "예매 확정단계(Reservation의 상태)" as Reservation{
            [*] --> Pending : 사용자가 성공적으로 예매
            Pending --> Payment
            state "결제 검증(Payment의 상태)" as Payment{
                [*] --> Ready : 결제 고유 id 발급
                Ready --> Verifying : 결제 성공/ 확정 X
                Verifying --> Paid : 결제 승인
            }
            Pending --> Canceled : 제한 시간 내 결제 실패
            Payment --> Confirm
            Confirm --> [*] : 영화상영시간이 지남
        }
```

## 결제 거부 시나리오

- 결제 거부란 결제 검증 실패 이후 이미 결제된 금액을 환불하는 보상 의미한다

### 상태다이어그램

```mermaid
stateDiagram-v2
    state "예매 확정단계(Reservation의 상태)" as Reservation{
        [*] --> Pending : 사용자가 성공적으로 예매
        Pending --> Payment
        state "결제 거부(Payment의 상태)" as Payment{
            [*] --> Ready : 결제 고유 id 발급
            Ready --> Verifying : 결제 성공/ 확정 X
            Verifying --> Aborted : 결제 거부(돈이 지불되었지만 결제 실패)
        }
        Payment --> PaymentCancel
        state "결제 취소" as PaymentCancel {
            [*] --> PaymentCancelRequired : 결제 취소 필요
            PaymentCancelRequired --> PaymentCanceled : 결제 취소 성공
            PaymentCanceled --> [*]
        }
        Pending --> Canceled : 제한 시간 내 결제 실패
        Canceled--> [*]
    }
```

### 결제 거부에 대한 보상 트랜잭션

#### 성공시나리오

```mermaid
sequenceDiagram
    autonumber
    participant PaymentValidateFacade as 결제 검증 서비스
    participant Facade as PaymentAbortFacade
    participant Service as PaymentCancelCrudService
    participant PortOne as PortOne 외부 API

    PaymentValidateFacade->>Facade: abortPayment(paymentId)


    rect rgba(240, 248, 255, 0.05)
        Note right of Service : 트랜잭션 1
        Facade->>Service: getOrCreate(paymentId, reason)
        activate Service
        Service-->>Facade: PaymentCancelDto (REQUIRED)
        deactivate Service
    end

    rect rgba(240, 248, 255, 0.05)
        Facade->>PortOne: cancelPayment(paymentId)
        PortOne-->>Facade: 결제 취소 성공
        Note right of PortOne : 외부 API 호출
    end

    rect rgba(240, 248, 255, 0.05)
    Note right of Service : 트랜잭션 2
    Facade->>Service: 상태변경 (REQUIRED -> CANCEL)
    end

    Facade-->>PaymentValidateFacade: 처리 종료
```

#### 외부 API 연동 실패 시나리오

```mermaid
sequenceDiagram
    autonumber
    participant PaymentValidateFacade as 결제 검증 서비스
    participant Facade as PaymentAbortFacade
    participant Service as PaymentCancelCrudService
    participant PortOne as PortOne 외부 API

    PaymentValidateFacade->>Facade: abortPayment(paymentId)


    rect rgba(240, 248, 255, 0.05)
        Note right of Service : 트랜잭션 1
        Facade->>Service: getOrCreate(paymentId, reason)
        activate Service
        Service-->>Facade: PaymentCancelDto (REQUIRED)
        deactivate Service
    end

    alt 외부 API 성공
        rect rgba(240, 248, 255, 0.05)
            Facade->>PortOne: cancelPayment(paymentId)
            PortOne-->>Facade: 결제 취소 성공
            Note right of PortOne : 외부 API 호출(TRY)
        end
        rect rgba(240, 248, 255, 0.05)
            Note right of Service : 트랜잭션 2(CONFIRM)
            Facade->>Service: 상태변경 (REQUIRED -> CANCEL)
        end
    else 외부 API 실패
        Note right of Facade: 상태변경을 하지 않고 required로 방치(외부에서 처리)
    end

    Facade-->>PaymentValidateFacade: 처리 종료
```

#### 트랜잭션 2 Confirm / Cancel 실패 시나리오

```mermaid
sequenceDiagram
    autonumber
    participant PaymentValidateFacade as 결제 검증 서비스
    participant Facade as PaymentAbortFacade
    participant Service as PaymentCancelCrudService
    participant PortOne as PortOne 외부 API

    PaymentValidateFacade->>Facade: abortPayment(paymentId)


    rect rgba(240, 248, 255, 0.05)
        Note right of Service : 트랜잭션 1
        Facade->>Service: getOrCreate(paymentId, reason)
        activate Service
        Service-->>Facade: PaymentCancelDto (REQUIRED)
        deactivate Service
    end

    rect rgba(240, 248, 255, 0.05)
        Facade->>PortOne: cancelPayment(paymentId)
        PortOne-->>Facade: 결제 취소 성공
        Note right of PortOne : 외부 API 호출(TRY)
    end

    alt 트랜잭션 성공
        rect rgba(240, 248, 255, 0.05)
            Note right of Service : 트랜잭션 2(CONFIRM)
            Facade->>Service: 상태변경 (REQUIRED -> CANCEL)
        end
    else 트랜잭션 실패
        Note right of Facade: 상태변경을 하지 않고 required로 방치(외부에서 처리)
    end
    Facade-->>PaymentValidateFacade: 처리 종료
```

#### 결론

```mermaid
sequenceDiagram
    autonumber
    participant PaymentValidateFacade as 결제 검증 서비스
    participant Facade as PaymentAbortFacade
    participant Service as PaymentCancelCrudService
    participant PortOne as PortOne 외부 API

    PaymentValidateFacade->>Facade: abortPayment(paymentId)


    rect rgba(240, 248, 255, 0.05)
        Note right of Service : 트랜잭션 1
        Facade->>Service: getOrCreate(paymentId, reason)
        activate Service
        Service-->>Facade: PaymentCancelDto (REQUIRED)
        deactivate Service
    end

    alt 외부 API 성공
        rect rgba(240, 248, 255, 0.05)
            Facade->>PortOne: cancelPayment(paymentId)
            PortOne-->>Facade: 결제 취소 성공
            Note right of PortOne : 외부 API 호출(TRY)
        end
    alt 트랜잭션 성공
        rect rgba(240, 248, 255, 0.05)
            Note right of Service : 트랜잭션 2(CONFIRM)
            Facade->>Service: 상태변경 (REQUIRED -> CANCEL)
        end
    else 트랜잭션 실패
        Note right of Facade: 상태변경을 하지 않고 required로 유지(외부에서 처리)
    end
    else 외부 API 실패
        Note right of Facade: 상태변경을 하지 않고 required로 유지(외부에서 처리)
    end

    Facade-->>PaymentValidateFacade: 처리 종료
```

## 예매 취소 시나리오

```mermaid
stateDiagram-v2
        state "영화 예매 생명주기" as Reservation{
            [*] --> Pending : 사용자가 성공적으로 예매
            Pending --> Payment
            state "결제 검증(Payment의 상태)" as Payment{
                [*] --> Ready : 결제 고유 id 발급
                Ready --> Verifying : 결제 성공/ 확정 X
                Verifying --> Paid : 결제 승인
            }
            Payment --> Confirm : 결제 성공으로 예매 확정
            Confirm --> [*] : 영화상영시간이 지남
            Confirm --> Canceled: 예매 취소
            Canceled --> PaymentCancel

            state "결제 취소" as PaymentCancel {
            [*] --> PaymentCancelRequired : 결제 취소 필요
                PaymentCancelRequired --> PaymentCanceled : 결제 취소 성공
                PaymentCancelRequired -->  PaymentCancelFailed : 결제 취소할 필요가 없는 상황
                PaymentCancelFailed --> [*]
                PaymentCanceled --> [*]
            }
            Pending --> Canceled : 제한 시간 내 결제 실패
            Canceled--> [*] : 취소된 예매는 되돌릴 수 없음
    }
```

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    autonumber
    actor User as 사용자
    participant Service as 예매 취소 서비스
    participant Facade as 결제 취소 서비스

    activate Service
    Note over Service: Confirm 상태면 결제도 같이 취소
    Service->>Service: 예매 취소 가능한 지 검증

    alt 예매 취소 가능
        Service->>Service: Reservation의 상태를 Canceled로 변경
    else 예매 취소 불가능
        Service->>User : 사용자에게 예매 취소 불가능 응답
    end


    alt 결제 취소가 필요한 경우
        %% 2. 결제 정보 조회
        Service->>Service: 결제 로직 조회
        deactivate Service
        Note over Service, Facade: 사전 정의된 결제 취소 로직 호출(결제 성공실패와 예매 취소는 무관=비동기도 고려)
        Service->>Facade: cancelPayment(paymentId, CUSTOMER_REQUEST)
        activate Service
    end
    Service->>User: 사용자에게 예매 취소 정보 주고 종료
    deactivate Service
```

## 시퀀스 다이어그램

### 결제 전반

#### 결제 전체 시스템

```mermaid
sequenceDiagram
    autonumber
    actor user as 사용자
    participant frontend as 프런트엔드
    participant pg as 외부결제서버
    participant backend as 서버

    user ->> frontend: 결제 시도
    frontend ->> backend : 결제관련 사전 정보 요청
    backend ->> frontend : paymentId, 지불할 금액 제공
    frontend ->> pg : 결제 요청
    pg ->> frontend : 결제 성공
    frontend ->> backend : 결제 검증요청
    pg ->> backend : 웹훅으로 결제 검증 성공응답
    alt  검증 성공
        backend ->> backend : 결제검증 및 예매 확정
        backend ->> frontend : 결제 성공응답
    else 검증실패
        backend ->> pg : 검증실패
        backend ->> frontend : 검증실패
    end
    frontend ->> user : 결과 출력
```

#### 결제 검증 전반

```mermaid
sequenceDiagram
    autonumber
    participant facade as ReservationValidateFacade
    participant service as ReservationValidateService
    participant client as portOneAdaptor
    participant canceler as PaymentCancelFacade

    alt 결제 검증이 가능한 상황인지 try
        facade ->> service :
        service ->> service : Payment의 상태를 pending -> verifying으로 저장
        service ->> facade : 성공 응답
    else 실패
        facade ->> canceler : 결제취소(비동기)
    end

    alt 외부 API로부터 결제정보를 받을 수 있는지 try
        facade ->> client :
        client ->> client : 외부 결제 서버로부터 결제 정보 취득
        facade ->> facade : 외부 결제 정부 취득 후 결제 메타데이터 검증 (KRW인지 등)
    else 실패
        facade ->> canceler : 결제취소(비동기)
    end

    alt 결제 검증 및 예매 확정
        facade ->> service :
        service ->> service : 결제 금액 검증, 예매상태 확인 등
        service ->> service : 결제 승인 및 예매 승인(PAID, Confirmed)
    else
        service ->> service: 결제 상태를 승인 거부된 결제로 변경(Aborted)
        facade ->> canceler : 결제취소(비동기)
    end

```
