package com.yape.antifraud.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final ValidateTransactionUseCase useCase;

    public TransactionCreatedConsumer(ObjectMapper objectMapper,
                                      ValidateTransactionUseCase useCase) {
        this.objectMapper = objectMapper;
        this.useCase = useCase;
    }

    @KafkaListener(topics = "transaction.created", groupId = "antifraud-service")
    public void onMessage(String message) throws Exception {
        TransactionCreatedEvent event = objectMapper.readValue(message, TransactionCreatedEvent.class);
        useCase.validate(event);
    }
}
