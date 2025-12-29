package com.yape.antifraud.application.usecase;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;

import java.math.BigDecimal;

public class ValidateTransactionUseCase {

    //se puede realizar configurable por variable de entorno
    private static final BigDecimal LIMIT = new BigDecimal("1000");
    private final TransactionStatusPublisherPort publisher;

    public ValidateTransactionUseCase(TransactionStatusPublisherPort publisher) {
        this.publisher = publisher;
    }

    public void validate(TransactionCreatedEvent event) {
        TransactionStatus status = event.value().compareTo(LIMIT) > 0
                ? TransactionStatus.REJECTED
                : TransactionStatus.APPROVED;

        publisher.publish(new TransactionStatusUpdatedEvent(event.transactionExternalId(), status));
    }
}
