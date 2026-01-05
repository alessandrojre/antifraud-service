package com.yape.antifraud.domain.port;

import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;
import reactor.core.publisher.Mono;

public interface TransactionStatusPublisherPort {
    Mono<Void> publish(TransactionStatusUpdatedEvent transactionStatusUpdatedEvent);
}