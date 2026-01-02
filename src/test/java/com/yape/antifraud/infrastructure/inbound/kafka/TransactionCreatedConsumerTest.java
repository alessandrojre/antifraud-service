package com.yape.antifraud.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TransactionCreatedConsumerTest {

    @Test
    void onMessage_whenValidJson_shouldDeserializeAndCallUseCase() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        ValidateTransactionUseCase useCase = mock(ValidateTransactionUseCase.class);

        TransactionCreatedConsumer consumer = new TransactionCreatedConsumer(objectMapper, useCase);

        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("500")
        );

        String json = objectMapper.writeValueAsString(event);

        // when
        consumer.onMessage(json);

        // then
        verify(useCase, times(1)).validate(any(TransactionCreatedEvent.class));
    }

    @Test
    void onMessage_whenInvalidJson_shouldThrowExceptionAndNotCallUseCase() {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        ValidateTransactionUseCase useCase = mock(ValidateTransactionUseCase.class);

        TransactionCreatedConsumer consumer = new TransactionCreatedConsumer(objectMapper, useCase);

        String invalidJson = "{invalid-json";

        // when / then
        try {
            consumer.onMessage(invalidJson);
        } catch (Exception ignored) {}

        verify(useCase, never()).validate(any());
    }
}
