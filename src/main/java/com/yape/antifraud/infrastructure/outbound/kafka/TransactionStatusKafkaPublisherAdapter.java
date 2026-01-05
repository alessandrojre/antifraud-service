package com.yape.antifraud.infrastructure.outbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
public class TransactionStatusKafkaPublisherAdapter implements TransactionStatusPublisherPort {

    private final String statusUpdateTopic;
    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    public TransactionStatusKafkaPublisherAdapter(
            @Value("${antifraud.topics.status-updated}") String statusUpdateTopic,
            KafkaSender<String, String> kafkaSender,
            ObjectMapper objectMapper) {
        this.statusUpdateTopic = statusUpdateTopic;
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publish(TransactionStatusUpdatedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .map(payload -> {
                    ProducerRecord<String, String> record = new ProducerRecord<>(
                            statusUpdateTopic,
                            event.transactionExternalId().toString(),
                            payload
                    );
                    return SenderRecord.create(record, event.transactionExternalId());
                })
                .flatMap(senderRecord ->
                        kafkaSender.send(
                                Mono.just(senderRecord)).next()
                )
                .then();
    }
}