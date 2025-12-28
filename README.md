# 상태 다이어그램으로 보는 예매 시스템의 상태변화

## 예매의 상태변화

```mermaid
stateDiagram-v2
        state "예매 전반" as Reservation{
            [*] --> Pending : 사용자가 성공적으로 예매
            Pending --> Canceled : 제한 시간 내 결제 실패 / 사용자 변심
            Pending --> Confirm : 결제가 성공적으로 완료됨
            Confirm --> Canceling : 결제완료된 예매를 취소하는 중
            Canceling --> Canceled : 예매 취소 완료
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
