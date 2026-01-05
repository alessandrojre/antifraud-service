package com.yape.antifraud.infrastructure.outbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionStatusKafkaPublisherAdapterTest {

    @Mock
    KafkaSender<String, String> kafkaSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returnCompleteWhenEventIsPublished() {
        // given
        String topic = "transaction.status.updated";
        TransactionStatusKafkaPublisherAdapter adapter =
                new TransactionStatusKafkaPublisherAdapter(topic, kafkaSender, objectMapper);

        UUID txId = UUID.randomUUID();
        TransactionStatusUpdatedEvent event =
                new TransactionStatusUpdatedEvent(txId, TransactionStatus.APPROVED);

        when(kafkaSender.send(any()))
                .thenReturn(Flux.just(mock(SenderResult.class)));

        // act
        Mono<Void> result = adapter.publish(event);

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(kafkaSender).send(any());
    }

    @Test
    void throwErrorWhenSerializationFails() throws Exception {
        // given
        ObjectMapper failingMapper = mock(ObjectMapper.class);
        when(failingMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("json error"));

        TransactionStatusKafkaPublisherAdapter adapter =
                new TransactionStatusKafkaPublisherAdapter(
                        "transaction.status.updated",
                        kafkaSender,
                        failingMapper
                );

        TransactionStatusUpdatedEvent event =
                new TransactionStatusUpdatedEvent(UUID.randomUUID(), TransactionStatus.REJECTED);

        // act
        Mono<Void> result = adapter.publish(event);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verifyNoInteractions(kafkaSender);
    }
}
