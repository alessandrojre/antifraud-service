package com.yape.antifraud.application.usecase;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;

import java.math.BigDecimal;

public class ValidateTransactionUseCase {

    private final BigDecimal limit;
    private final TransactionStatusPublisherPort publisher;

    public ValidateTransactionUseCase(
            BigDecimal limit,
            TransactionStatusPublisherPort publisher
    ) {
        this.limit = limit;
        this.publisher = publisher;
    }

    public void validate(TransactionCreatedEvent event) {
        TransactionStatus status = event.value().compareTo(limit) > 0
                ? TransactionStatus.REJECTED
                : TransactionStatus.APPROVED;

        publisher.publish(
                new TransactionStatusUpdatedEvent(
                        event.transactionExternalId(),
                        status
                )
        );
    }
}
