package com.yape.antifraud.application.service;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateTransactionServiceTest {

    @Mock
    TransactionStatusPublisherPort publisherPort;

    @Captor
    ArgumentCaptor<TransactionStatusUpdatedEvent> eventCaptor;

    @Test
    void returnApprovedWhenValueIsLowerOrEqualThanLimit() {
        // given
        BigDecimal limit = new BigDecimal("1000.00");
        ValidateTransactionService service = new ValidateTransactionService(limit, publisherPort);

        TransactionCreatedEvent createdEvent = new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("999.99")
        );

        when(publisherPort.publish(any())).thenReturn(Mono.empty());

        // act
        Mono<Void> result = service.validate(createdEvent);

        // then
        StepVerifier.create(result).verifyComplete();

        verify(publisherPort).publish(eventCaptor.capture());
        TransactionStatusUpdatedEvent published = eventCaptor.getValue();

        assertThat(published.transactionExternalId()).isEqualTo(createdEvent.transactionExternalId());
        assertThat(published.status()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    void returnRejectedWhenValueIsHigherThanLimit() {
        // given
        BigDecimal limit = new BigDecimal("1000.00");
        ValidateTransactionService service = new ValidateTransactionService(limit, publisherPort);

        TransactionCreatedEvent createdEvent = new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("1000.01")
        );

        when(publisherPort.publish(any())).thenReturn(Mono.empty());

        // act
        Mono<Void> result = service.validate(createdEvent);

        // then
        StepVerifier.create(result).verifyComplete();

        verify(publisherPort).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().status()).isEqualTo(TransactionStatus.REJECTED);
    }

    @Test
    void returnErrorWhenPublisherFails() {
        // given
        BigDecimal limit = new BigDecimal("1000.00");
        ValidateTransactionService service = new ValidateTransactionService(limit, publisherPort);

        TransactionCreatedEvent createdEvent = new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("10.00")
        );

        when(publisherPort.publish(any()))
                .thenReturn(Mono.error(new RuntimeException("kafka down")));

        // act
        Mono<Void> result = service.validate(createdEvent);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
