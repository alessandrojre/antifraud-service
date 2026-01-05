package com.yape.antifraud.application.usecase;

import com.yape.antifraud.application.dto.TransactionCreatedEvent;
import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import com.yape.antifraud.domain.model.TransactionStatus;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ValidateTransactionUseCase {
    Mono<Void> validate(TransactionCreatedEvent transactionCreatedEvent);
}