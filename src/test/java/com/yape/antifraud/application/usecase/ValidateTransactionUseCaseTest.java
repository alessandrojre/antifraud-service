package com.yape.antifraud.application.usecase;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ValidateTransactionUseCaseTest {

    @Test
    void validate_whenValueIsLessOrEqualThanLimit_shouldPublishApproved() {
        // given
        TransactionStatusPublisherPort publisher = mock(TransactionStatusPublisherPort.class);
        ValidateTransactionUseCase useCase = new ValidateTransactionUseCase(publisher);

        UUID txId = UUID.randomUUID();
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                txId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("1000")
        );

        // when
        useCase.validate(event);

        // then
        ArgumentCaptor<TransactionStatusUpdatedEvent> captor =
                ArgumentCaptor.forClass(TransactionStatusUpdatedEvent.class);

        verify(publisher, times(1)).publish(captor.capture());
        TransactionStatusUpdatedEvent published = captor.getValue();

        assertThat(published.transactionExternalId()).isEqualTo(txId);
        assertThat(published.status()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    void validate_whenValueIsGreaterThanLimit_shouldPublishRejected() {
        // given
        TransactionStatusPublisherPort publisher = mock(TransactionStatusPublisherPort.class);
        ValidateTransactionUseCase useCase = new ValidateTransactionUseCase(publisher);

        UUID txId = UUID.randomUUID();
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                txId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("1000.01")
        );

        // when
        useCase.validate(event);

        // then
        ArgumentCaptor<TransactionStatusUpdatedEvent> captor =
                ArgumentCaptor.forClass(TransactionStatusUpdatedEvent.class);

        verify(publisher, times(1)).publish(captor.capture());
        TransactionStatusUpdatedEvent published = captor.getValue();

        assertThat(published.transactionExternalId()).isEqualTo(txId);
        assertThat(published.status()).isEqualTo(TransactionStatus.REJECTED);
    }
}
