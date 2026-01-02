package com.yape.antifraud.infrastructure.outbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransactionStatusKafkaPublisherAdapterTest {

    @Test
    void publish_whenSerializationOk_shouldSendToKafka() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();

        TransactionStatusKafkaPublisherAdapter adapter =
                new TransactionStatusKafkaPublisherAdapter(kafkaTemplate, objectMapper);

        UUID txId = UUID.randomUUID();
        TransactionStatusUpdatedEvent event = new TransactionStatusUpdatedEvent(txId, TransactionStatus.APPROVED);

        // when
        adapter.publish(event);

        // then
        verify(kafkaTemplate, times(1))
                .send(eq("transaction.status.updated"), eq(txId.toString()), anyString());
    }

    @Test
    void publish_whenObjectMapperFails_shouldThrowIllegalStateException() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        TransactionStatusKafkaPublisherAdapter adapter =
                new TransactionStatusKafkaPublisherAdapter(kafkaTemplate, objectMapper);

        UUID txId = UUID.randomUUID();
        TransactionStatusUpdatedEvent event = new TransactionStatusUpdatedEvent(txId, TransactionStatus.REJECTED);

        when(objectMapper.writeValueAsString(event)).thenThrow(new RuntimeException("boom"));

        // when / then
        assertThatThrownBy(() -> adapter.publish(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to publish transaction.status.updated");

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}
