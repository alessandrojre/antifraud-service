package com.yape.antifraud.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCreatedConsumerTest {

    @Mock
    ValidateTransactionUseCase validateTransactionUseCase;

    @Mock
    KafkaReceiver<String, String> kafkaReceiver;

    @Mock
    ReceiverRecord<String, String> receiverRecord;

    @Mock
    ReceiverOffset receiverOffset;

    @Test
    void returnAcknowledgeWhenMessageIsValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("10.00")
        );

        String payload = objectMapper.writeValueAsString(event);

        when(receiverRecord.value()).thenReturn(payload);
        when(receiverRecord.receiverOffset()).thenReturn(receiverOffset);

        when(kafkaReceiver.receive()).thenReturn(Flux.just(receiverRecord));

        when(validateTransactionUseCase.validate(any(TransactionCreatedEvent.class)))
                .thenReturn(Mono.empty());

        TransactionCreatedConsumer consumer =
                new TransactionCreatedConsumer(objectMapper, validateTransactionUseCase, kafkaReceiver);

        consumer.startConsuming();

        verify(validateTransactionUseCase, timeout(500)).validate(any(TransactionCreatedEvent.class));
        verify(receiverOffset, timeout(500)).acknowledge();
    }


}
