package com.yape.antifraud.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.antifraud.application.usecase.ValidateTransactionUseCase;
import com.yape.antifraud.domain.port.TransactionStatusPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeansConfig {

    @Bean
    public ValidateTransactionUseCase validateTransactionUseCase(TransactionStatusPublisherPort publisher) {
        return new ValidateTransactionUseCase(publisher);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
