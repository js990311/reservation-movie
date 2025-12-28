# 상태 다이어그램으로 보는 예매 시스템의 상태변화

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
        Ready --> Failed : 결제 실패(돈이 안나감)
        Ready-->Verifying: 결제는 성공했고 확정되지는 않았음
        Verifying --> Paid : 결제 완료(검증여부와 무관)
        Verifying --> Aborted : 검증 실패로 인한 결제 거부
        Aborted --> [*]
        Failed --> [*]
        Paid --> [*] : 사용자 환불이 있어도 변화없음(PaymentCancel 참조)
    }
```

## PaymentCancel의 상태

```mermaid
stateDiagram-v2
        state "결제 취소 엔티티의 상태" as PaymentCancel{
            [*] --> REQUIRED : 결제 취소 필요
            REQUIRED --> Canceled : 결제 취소 성공
            REQUIRED --> Failed : 결제 불가능
            Canceled --> [*]
            Failed --> [*]
        }
```

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
