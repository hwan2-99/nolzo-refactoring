# ❓ 문제: 빈자리 알림 구독 처리의 대량 트래픽 부담과 이벤트 유실 가능성

- 빈자리 알림 기능은 사용자가 특정 공연/회차를 구독해두면, 좌석 취소나 미결제 예약 취소 등으로 빈자리가 발생했을 때 이를 빠르게 알려주는 기능이다.
- 하지만 인기 공연에서는 특정 회차에 구독자가 대량으로 몰릴 수 있고, 좌석 상태가 바뀌는 시점마다 알림 처리가 연달아 발생할 수 있다.
- 만약 이 작업을 예약 취소 흐름 안에서 끝까지 동기적으로 처리하면 다음과 같은 문제가 생길 수 있다.


## 1. 어떤 문제가 발생할까?

### 1.1. 하나의 빈자리 이벤트가 너무 큰 작업이 되는 문제

- 빈자리가 한 번 발생할 때마다 해당 공연/회차를 구독한 사용자를 조회하고 발송까지 수행하면, 이벤트 1건이 지나치게 무거운 단일 작업이 될 수 있다.
- 구독자가 많은 경우 consumer 처리 시간이 길어지고, 특정 이벤트가 Kafka consumer를 오래 점유해 전체 처리량이 감소할 수 있다.

<br>

### 1.2. 좌석 서비스와 알림 처리 로직의 결합

- 좌석 상태 변경 직후 바로 알림 처리까지 같이 수행하면, 알림 작업의 지연이나 실패가 좌석 취소 흐름에 직접 영향을 줄 수 있다.
- 즉, 사용자에게 즉시 끝나야 하는 좌석 취소 처리와, 별도 후속 작업으로 분리 가능한 알림 처리의 경계가 흐려진다.

<br>

# ❗ 해결: Kafka를 통한 비동기 메시지 발행

- 이 문제를 해결하기 위해, 빈자리 알림 처리는 좌석 취소 흐름 안에서 직접 끝내지 않고 Kafka로 이벤트를 발행한 뒤 별도 consumer가 처리하도록 분리했다.
- 즉 좌석 서비스는 "빈자리가 생겼다"는 사실만 이벤트로 남기고, 실제 알림 처리는 이후 비동기 흐름에서 수행한다.

<br>

## 1. 구현

- 좌석 취소 후 바로 구독자 조회와 알림 발송을 수행하는 대신, 빈자리 알림 이벤트를 발행하는 구조로 바꾸었다.
- 이 덕분에 좌석 취소 요청은 알림 처리 완료까지 기다리지 않고, 이후 알림 로직은 독립적으로 처리할 수 있게 되었다.
- 즉 좌석 상태 변경 로직은 "빈자리가 생겼다"는 사실을 이벤트로 발행하고, 실제 알림 처리 책임은 이후 consumer 쪽으로 넘기게 된다.
```java
@Override
public void publish(SeatAvailableEvent event) {
    String key = String.valueOf(event.eventScheduleId());
    SeatAvailableEventMessage message = SeatAvailableEventMessage.from(event);

    kafkaTemplate.send(seatAvailableTopic, key, message);
}
```

<br>

## 2. 기술 선택의 이유 및 장점

### 2.1. 요청 흐름과 후속 작업의 분리

- 좌석 취소 요청은 빠르게 끝내고, 알림은 별도 비동기 흐름에서 처리할 수 있다.
- 즉 알림 작업이 좌석 서비스 응답 시간에 직접적인 영향을 주지 않게 된다.

<br>

### 2.2. 서비스 간 결합도 감소

- 좌석 상태 변경과 알림 발송을 직접 엮지 않고, Kafka 이벤트를 통해 간접적으로 연결할 수 있다.
- 이로 인해 알림 처리의 지연이나 장애가 좌석 취소 처리 전체를 막는 문제를 줄일 수 있다.

<br>

### 2.3. 이후 확장에 유리

- Kafka의 높은 처리량과 확장성을 통해 메시지 볼륨 증가 시에도 안정적인 처리가 가능
- 이벤트 기반으로 분리해두면 이후 fan-out, retry, DLT, Outbox 같은 안정화 구조를 단계적으로 붙이기 쉬워진다.

<br>

## 3. 단점 및 트레이드오프

### 3.1. 복잡성 증가

- 메시지 브로커(Kafka)를 도입하면 단순히 메서드를 바로 호출하던 구조보다 시스템 아키텍처가 복잡해진다.
- 메시지 발행/구독, 토픽 관리, 메시지 포맷 정의, consumer 장애 처리 등 추가적인 고려사항이 생긴다.

<br>

### 3.2. 결과적 일관성

- 비동기 통신의 본질적인 특성상 결과적 일관성을 가지게 된다.
- 좌석이 비워진 시점과 실제 알림이 처리되는 시점 사이에는 지연이 발생할 수 있으며, 시스템 상태의 즉각적인 일관성을 보장하기 어렵다.
- 즉, 사용자에게는 좌석이 이미 비워졌더라도 알림은 약간 뒤에 도착할 수 있다는 점을 감수해야 한다.

<br>

### 3.3. 메시지 처리 보장의 중요성 증가

- 동기 호출에서는 비교적 단순하게 다루던 실패 처리가, Kafka 기반 비동기 처리에서는 메시지 유실과 중복 처리 문제로 더 중요해진다.
- At-Least-Once, Exactly-Once 같은 전달 보장 수준을 어떻게 가져갈지에 따라 설계 복잡도도 달라진다.
- 즉, Kafka를 도입했다고 해서 자동으로 안전해지는 것이 아니라, 개발자가 설정과 처리 방식을 올바르게 설계해야 한다.

<br>

# ❓ 문제: 전송 오류와 대량 구독자 처리에서 발생하는 후속 작업 부담

- Kafka로 비동기 분리를 했다고 해도, 빈자리 이벤트 1건을 consumer 하나가 끝까지 처리하는 구조라면 또 다른 문제가 남는다.
- 특히 인기 공연처럼 구독자가 많을 경우, 이벤트 1건이 다시 매우 큰 작업이 되어버릴 수 있다.
- 또한 장애나 전송 오류가 발생하면 같은 메시지가 재처리되거나, 실패 범위가 너무 커질 수 있다.

<br>

## 1. 어떤 문제가 발생할까?

### 1.1. 이벤트 1건이 다시 무거운 단일 작업이 되는 문제

- 예를 들어 같은 공연/회차 구독자가 1,000명인데 consumer가 이들을 한 번에 다 처리하면, 결국 이벤트 1건이 또 하나의 대형 작업이 된다.
- 이런 구조에서는 일부 실패가 전체 처리에 영향을 주고, 장애 범위도 커지게 된다.

<br>

### 1.2. DB 반영 후 Kafka 발행 실패 시 이벤트 유실 가능성

- 좌석 상태 변경 직후 Kafka에 바로 발행하는 구조에서는 다음과 같은 상황이 생길 수 있다.

```text
좌석 상태 변경 DB 반영 성공
-> Kafka 발행 실패
```

- 이 경우 실제로는 좌석이 비워졌지만, 빈자리 알림 이벤트가 Kafka에 기록되지 못해 구독자에게 알림이 가지 않을 수 있다.

<br>

# ❗ 해결: fan-out + Outbox 패턴

- 이 문제를 해결하기 위해, 이벤트 1건을 여러 개의 작은 알림 처리 작업으로 나누는 fan-out 구조를 적용했다.
- 동시에 DB 반영 이후 Kafka 발행 실패로 이벤트가 사라지는 문제를 줄이기 위해 Outbox 패턴도 함께 적용했다.

<br>

## 1. 구현

### 1.1. fan-out 구조로 이벤트를 여러 batch 작업으로 분리

- 빈자리 이벤트를 수신한 뒤, 해당 공연/회차를 구독한 사용자를 한 번에 전부 처리하지 않고 batch 단위로 나누어 **알림 처리 요청 메시지**를 발행한다.
- 예를 들어 같은 공연/회차를 구독한 사용자가 1,000명이고 batch size가 100이라면,
  - 1~100번 구독자
  - 101~200번 구독자
  - 201~300번 구독자
  - ...

  처럼 여러 묶음으로 나누어 각각 별도의 알림 처리 요청 메시지를 발행하게 된다.
- 즉 원본 이벤트는 1건이지만, 실제 알림 처리 단계에서는 여러 개의 batch 작업으로 다시 나뉘어 Kafka에 쌓이게 된다.

```java
@Transactional
public void handle(SeatAvailableEvent event) {
    processBatches(event);
}

private void processBatches(SeatAvailableEvent event) {
    int page = 0;

    while (true) {
        List<SeatAvailabilitySubscription> subscriptions = loadSubscriptions(event, page);

        if (subscriptions.isEmpty()) {
            return;
        }

        publishBatchRequest(event, subscriptions);

        if (subscriptions.size() < batchSize) {
            return;
        }

        page++;
    }
}
```

- 위 코드에서 `page`를 0부터 증가시키며 구독자를 batch size만큼 조회하고, 조회된 구독자 목록마다 `publishBatchRequest(...)`를 호출한다.
- 따라서 이벤트 1건이 끝까지 한 번에 처리되는 것이 아니라, **여러 번의 Kafka 발행으로 쪼개져 분산 처리**된다.

<br>

### 1.2. batch consumer가 실제 알림 처리 수행

- **알림 처리 요청 메시지**를 소비하는 서비스는 실제 구독자 재조회, 발송 시도, 이력 저장을 담당한다.
- 즉 fan-out 이후에는 이벤트를 나누는 책임과, 실제 발송을 수행하는 책임이 분리된다.

```java
@Transactional
public void handle(NotificationBatchRequest request) {
    Event event = eventPersistencePort.getOrThrow(request.eventId());
    SeatAvailableEvent seatAvailableEvent = new SeatAvailableEvent(...);

    List<SeatAvailabilitySubscription> subscriptions =
            loadSeatAvailabilitySubscriptionPort.findSubscriptionsByIds(request.subscriptionIds());

    for (SeatAvailabilitySubscription subscription : subscriptions) {
        sendEmail(seatAvailableEvent, event, subscription);
    }
}
```

<br>

### 1.3. Outbox 패턴으로 DB-Kafka 사이 유실 완화

- DB 반영 성공 후 Kafka 발행 실패로 이벤트가 유실되는 문제를 줄이기 위해 Outbox 패턴을 적용했다.
- 예약 취소 시 Kafka에 직접 보내지 않고, 먼저 **이벤트 보관 테이블**에 저장한다.
- 즉, “바로 Kafka에 보내는 구조”에서 “먼저 안전하게 저장한 뒤 나중에 발행하는 구조”로 바꾸었다.

```java
private void saveOutboxEvent(SeatAvailableEvent event) {
    saveOutboxEventPort.save(new OutboxEvent(...));
}
```

- 이후 별도 publisher가 아직 발행되지 않은 저장 이벤트를 읽어 Kafka로 발행한다.

```java
@Transactional
public void publishPendingEvents() {
    List<OutboxEvent> events = loadOutboxEventPort.findPublishableEvents(batchSize);

    for (OutboxEvent outboxEvent : events) {
        publish(outboxEvent);
    }
}

private void publish(OutboxEvent outboxEvent) {
    try {
        SeatAvailableEvent event = objectMapper.readValue(
                outboxEvent.getPayload(),
                SeatAvailableEvent.class
        );
        publishSeatAvailableEventPort.publish(event);
        outboxEvent.markPublished();
        saveOutboxEventPort.save(outboxEvent);
    } catch (Exception e) {
        outboxEvent.markFailed(e.getMessage());
        saveOutboxEventPort.save(outboxEvent);
    }
}
```

<br>

## 2. 기술 선택의 이유 및 장점

### 2.1. 큰 작업을 작은 작업으로 분산

- fan-out 구조를 통해 이벤트 1건을 여러 개의 작은 batch 작업으로 나눌 수 있다.
- 이로 인해 한 consumer가 전체 구독자를 한 번에 떠안지 않게 되고, 실패 범위도 작아진다.

<br>

### 2.2. 후속 처리 책임 분리

- fan-out 이전 consumer는 "누구에게 보낼지 나누는 역할"만 담당하고,
- fan-out 이후 consumer는 "실제 발송을 수행하는 역할"만 담당한다.
- 이렇게 역할을 나누면 각 단계의 책임이 명확해지고, 병렬 처리 확장에도 유리하다.

<br>

### 2.3. 이벤트 유실 완화

- Outbox를 사용하면 좌석 상태 변경과 이벤트 저장을 같은 트랜잭션 안에서 처리할 수 있다.
- 따라서 Kafka 발행이 일시적으로 실패하더라도 "보내야 할 이벤트" 자체는 DB에 남아 있게 된다.

<br>

## 3. 단점 및 트레이드오프

### 3.1. 구조 복잡도 증가

- fan-out과 Outbox까지 도입하면 단순히 이벤트를 보내고 받는 수준을 넘어, batch 분리, 별도 consumer, outbox 저장, outbox 발행 스케줄러까지 함께 관리해야 한다.
- 즉, 처리량과 안정성은 좋아지지만 그만큼 설계와 운영 복잡도는 증가한다.

<br>

### 3.2. 지연 발생 가능성

- Outbox publisher가 스케줄러 기반으로 동작하기 때문에, 좌석이 비워진 직후 Kafka에 즉시 발행되는 구조보다 몇 초 수준의 지연이 생길 수 있다.
- 이는 즉시성을 조금 희생하는 대신, DB 반영 후 Kafka 발행 실패로 인한 유실 가능성을 줄이기 위한 트레이드오프다.

<br>

### 3.3. 정리 배치 필요

- `PUBLISHED` 된 이벤트를 계속 쌓아두면 테이블 크기가 커지고 조회 성능에도 영향을 줄 수 있다.
- 따라서 Outbox를 도입한 뒤에는 오래된 이벤트를 정리하거나 보관 정책을 따로 가져가는 작업이 필요해진다.

<br>

# ❓ 문제: 메시지 유실 가능성

- Kafka를 사용하더라도, 전송 중 네트워크 장애나 브로커 응답 지연이 있으면 메시지가 안정적으로 저장되지 못할 수 있다.
- 또한 consumer가 메시지를 읽은 직후 장애가 발생하면, 아직 비즈니스 로직이 끝나지 않았는데도 이미 처리된 메시지로 간주될 위험이 있다.

<br>

## 1. 어떤 문제가 발생할까?

### 1.1. producer 전송 단계 유실 가능성

- 브로커 장애나 일시적인 네트워크 오류가 있을 때, producer가 메시지를 충분히 안전하게 전달하지 못하면 유실이 발생할 수 있다.

<br>

### 1.2. consumer 처리 중 커밋 시점 문제

- consumer가 메시지를 읽었다는 이유만으로 offset을 자동 커밋해버리면, 실제 비즈니스 로직이 실패했어도 Kafka 입장에서는 이미 처리된 메시지로 간주될 수 있다.

<br>

### 1.3. 반복 실패 메시지가 전체 흐름을 막는 문제

- 같은 메시지가 계속 실패하는데도 이를 계속 일반 토픽에서만 재시도하면, 전체 consumer 흐름이 지연되거나 멈출 수 있다.

<br>

# ❗ 해결: 프로듀서 acks, retry, delivery.timeout.ms 설정과 컨슈머 자동 커밋 비활성화

- 이 문제를 줄이기 위해 producer와 consumer 설정을 보완해 메시지를 더 안전하게 보내고, 성공적으로 처리된 레코드만 커밋되도록 제어했다.
- 또한 반복 실패 메시지는 DLT(실패 메시지 보관 토픽)로 분리해 전체 흐름이 막히지 않도록 했다.

<br>

## 1. 구현

### 1.1. producer 설정

```yaml
spring:
  kafka:
    producer:
      acks: all
      retries: 10
      properties:
        enable.idempotence: true
        request.timeout.ms: 30000
        delivery.timeout.ms: 120000
```

##### `acks=all`
- producer가 메시지를 보낸 뒤, 리더 브로커뿐 아니라 ISR(In-Sync Replicas)까지 저장 완료 응답을 받을 때까지 기다리게 한다.
- 이 설정이 없으면 브로커 일부 장애 상황에서 메시지가 충분히 복제되기 전에 성공으로 처리될 수 있다.
- 즉, **브로커 장애 상황에서도 메시지 유실 가능성을 줄이기 위한 설정**이다.

##### `enable.idempotence=true`
- 네트워크 지연이나 재시도 상황에서 같은 메시지가 다시 전송되더라도 브로커가 중복 저장하지 않도록 돕는다.
- 또한 전송 순서가 꼬일 가능성을 낮추는 역할도 함께 한다.

##### `retries`
- 일시적인 네트워크 오류나 브로커 응답 지연이 발생했을 때 producer가 메시지 전송을 다시 시도하도록 한다.
- 이 설정이 없으면 순간적인 오류에도 바로 실패로 끝나 메시지가 유실될 수 있다.

##### `request.timeout.ms`
- producer가 브로커에 메시지를 보낸 뒤, 한 번의 요청에 대해 응답을 얼마나 오래 기다릴지 정하는 설정이다.
- 즉, **한 번의 전송 시도에 대한 응답 대기 시간을 제어해 과도한 조기 실패나 과도한 대기를 막는 설정**이다.

##### `delivery.timeout.ms`
- producer가 메시지를 최종적으로 전송 완료하거나 실패로 판단할 때까지 허용하는 전체 시간이다.
- 여기에는 첫 전송뿐 아니라 재시도에 걸리는 시간도 포함된다.
- 즉, **메시지를 얼마나 끈기 있게 전달할지 정하는 전체 제한 시간**이다.

<br>

### 1.2. consumer 설정

```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
      max-poll-records: 100
```

##### 자동 커밋 비활성화
- consumer가 메시지를 읽었다는 이유만으로 offset을 자동 커밋하지 않도록 막는다.
- 현재 프로젝트는 `ack.acknowledge()`를 직접 호출하는 명시적 수동 커밋 방식은 아니고, listener 컨테이너에서 레코드 단위로 커밋을 제어하는 방식을 사용한다.
- 이 설정은 listener 메서드가 예외 없이 끝난 레코드만 커밋하고, 예외가 발생한 레코드는 커밋하지 않도록 동작한다.

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
        ConsumerFactory<Object, Object> consumerFactory,
        KafkaTemplate<Object, Object> kafkaTemplate
) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    ...
    return factory;
}
```

```java
@KafkaListener(
        topics = "${app.kafka.topics.seat-available}",
        groupId = "${app.kafka.consumer.group-id}"
)
public void consume(SeatAvailableEventMessage message) {
    handleSeatAvailableUseCase.handle(...);
}

@KafkaListener(
        topics = "${app.kafka.topics.notification-batch-request}",
        groupId = "${app.kafka.consumer.group-id}"
)
public void consume(NotificationBatchRequestMessage message) {
    handleNotificationBatchUseCase.handle(message.toRequest());
}
```

<br>

### 1.3. backoff retry와 DLT 적용

- 재시도 시 바로 연속으로 다시 시도하지 않고 일정 시간 간격을 두는 backoff retry를 적용했다.
- 반복적으로 처리에 실패하는 메시지가 전체 시스템을 막는 것을 방지하고, 동시에 해당 메시지를 유실하지 않기 위해 DLT(Dead Letter Topic) 패턴을 적용했다.
- 재시도에 실패한 메시지는 DLT로 전송되어 나중에 원인을 분석하고 수동으로 처리할 수 있다.

<br>

## 2. 다른 방법 및 비교

### 2.1. HTTP 동기 호출

- 좌석 취소 직후 알림 서비스나 메일 서비스를 HTTP로 바로 호출할 수도 있다.
- 구현은 단순하지만, 알림 처리 시간이 길어질수록 좌석 취소 요청도 느려지고, 외부 서비스 장애가 바로 전파될 수 있다.
- 따라서 대량 트래픽과 장애 격리를 고려하면 적합하지 않다고 판단했다.

<br>

### 2.2. 다른 메시지 큐
- RabbitMQ 같은 다른 메시지 브로커도 사용할 수 있다.
- 하지만 이번 프로젝트에서는 이벤트 기반 비동기 처리, fan-out 확장성, 대량 트래픽 대응 측면을 고려해 Kafka를 선택했다.

<br>

## 3. 정리

- 이번 프로젝트의 빈자리 알림 구독 처리는 단순히 “알림이 간다” 수준이 아니라 아래 내용을 함께 고려한 구조로 설계했다.
  - 비동기 분리
  - 대량 구독자 처리
  - 이벤트 유실 완화
  - 처리 실패 복구
  - 중복 발송 방지

- 즉, 좌석 상태 변경과 알림 처리를 `Kafka`로 분리하고, `fan-out`으로 여러 `batch` 작업으로 나누고, `Outbox`로 유실 가능성을 줄이고, `producer/consumer` 설정과 `DLT`로 실패 복구 가능성을 높이는 방향으로 구현했다.
