package com.yape.antifraud.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.service.ValidateTransactionService;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ApplicationBeansConfig {

    @Bean
    public ValidateTransactionService validateTransactionUseCase(
            @Value("${antifraud.transaction.limit}") BigDecimal limit,
            TransactionStatusPublisherPort publisher) {

        return new ValidateTransactionService(limit, publisher);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
