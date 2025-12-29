package com.yape.antifraud.application.dto;

import com.yape.antifraud.domain.model.TransactionStatus;

import java.util.UUID;

public record TransactionStatusUpdatedEvent(
        UUID transactionExternalId,
        TransactionStatus status
) {}