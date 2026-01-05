package com.yape.antifraud.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

@Component
public class TransactionCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionCreatedConsumer.class);
    private final ObjectMapper objectMapper;
    private final ValidateTransactionUseCase validateTransactionUseCase;
    private final KafkaReceiver<String, String> kafkaReceiver;

    public TransactionCreatedConsumer(
            ObjectMapper objectMapper,
            ValidateTransactionUseCase validateTransactionUseCase,
            KafkaReceiver<String, String> kafkaReceiver) {
        this.objectMapper = objectMapper;
        this.validateTransactionUseCase = validateTransactionUseCase;
        this.kafkaReceiver = kafkaReceiver;
    }

    @PostConstruct
    public void init() {
        startConsuming();
    }

    public void startConsuming() {
        kafkaReceiver.receive()
                .flatMap(receiverRecord ->
                        processMessage(receiverRecord.value())
                                .doOnSuccess(unused -> receiverRecord.receiverOffset().acknowledge())
                )
                .subscribe(
                        null,
                        error -> logger.error("Error in Kafka consumption: ", error)
                );
    }

    private Mono<Void> processMessage(String messagePayload) {
        return Mono.fromCallable(() ->
                        objectMapper.readValue(messagePayload, TransactionCreatedEvent.class))
                .flatMap(validateTransactionUseCase::validate);
    }
}