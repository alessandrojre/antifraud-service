package com.yape.antifraud.application.service;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class ValidateTransactionService implements ValidateTransactionUseCase {

    private final BigDecimal validationLimit;
    private final TransactionStatusPublisherPort transactionStatusPublisherPort;

    public ValidateTransactionService(
            BigDecimal validationLimit,
            TransactionStatusPublisherPort transactionStatusPublisherPort
    ) {
        this.validationLimit = validationLimit;
        this.transactionStatusPublisherPort = transactionStatusPublisherPort;
    }

    @Override
    public Mono<Void> validate(TransactionCreatedEvent transactionCreatedEvent) {
        return Mono.fromCallable(() -> {
            TransactionStatus transactionStatus = transactionCreatedEvent.value().compareTo(validationLimit) > 0
                    ? TransactionStatus.REJECTED
                    : TransactionStatus.APPROVED;

            return new TransactionStatusUpdatedEvent(
                    transactionCreatedEvent.transactionExternalId(),
                    transactionStatus
            );
        }).flatMap(transactionStatusPublisherPort::publish);
    }
}