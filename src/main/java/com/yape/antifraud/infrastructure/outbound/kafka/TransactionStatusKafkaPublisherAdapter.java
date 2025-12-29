package com.yape.antifraud.infrastructure.outbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusKafkaPublisherAdapter implements TransactionStatusPublisherPort {

    private static final String TOPIC = "transaction.status.updated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionStatusKafkaPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate,
                                                  ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TransactionStatusUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.transactionExternalId().toString(), payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish transaction.status.updated", e);
        }
    }
}
