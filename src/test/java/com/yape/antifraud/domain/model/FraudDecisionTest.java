package com.yape.antifraud.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FraudDecisionTest {

    @Test
    void shouldCreateFraudDecisionRecordCorrectly() {
        UUID transactionId = UUID.randomUUID();
        TransactionStatus status = TransactionStatus.APPROVED;

        FraudDecision decision = new FraudDecision(transactionId, status);

        assertThat(decision.transactionExternalId()).isEqualTo(transactionId);
        assertThat(decision.status()).isEqualTo(status);
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        // given
        UUID transactionId = UUID.randomUUID();

        FraudDecision decision1 =
                new FraudDecision(transactionId, TransactionStatus.REJECTED);

        FraudDecision decision2 =
                new FraudDecision(transactionId, TransactionStatus.REJECTED);

        // then
        assertThat(decision1)
                .isEqualTo(decision2)
                .hasSameHashCodeAs(decision2);
    }

    @Test
    void shouldHaveMeaningfulToString() {
        // given
        FraudDecision decision =
                new FraudDecision(UUID.randomUUID(), TransactionStatus.APPROVED);

        // then
        assertThat(decision.toString())
                .contains("FraudDecision")
                .contains("transactionExternalId")
                .contains("status");
    }
}
