package com.yape.antifraud.domain.model;

import java.util.UUID;

public record FraudDecision(UUID transactionExternalId, TransactionStatus status) { }