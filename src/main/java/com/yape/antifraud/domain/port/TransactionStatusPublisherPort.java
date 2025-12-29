package com.yape.antifraud.domain.port;

import com.yape.antifraud.application.dto.TransactionStatusUpdatedEvent;

public interface TransactionStatusPublisherPort {
    void publish(TransactionStatusUpdatedEvent event);
}