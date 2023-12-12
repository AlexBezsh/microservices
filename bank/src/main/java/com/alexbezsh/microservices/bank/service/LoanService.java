package com.alexbezsh.microservices.bank.service;

import com.alexbezsh.microservices.bank.client.CreditScoreClient;
import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.bank.model.client.CreditScoreRequest;
import com.alexbezsh.microservices.bank.properties.LoanProperties;
import com.alexbezsh.microservices.common.exception.NotAvailableException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
@EnableConfigurationProperties(LoanProperties.class)
public class LoanService {

    private final LoanProperties properties;
    private final CreditScoreClient creditScoreClient;

    public LoanResponse checkLoanEligibility(LoanRequest request) {
        String userId = request.getUserId();
        log.info("Checking loan eligibility for user with ID " + userId);
        if (properties.isBlockAll()) {
            throw new NotAvailableException("Currently bank doesn't give any loans");
        }
        CreditScoreRequest creditScoreRequest = CreditScoreRequest.builder().userId(userId).build();
        BigDecimal score = creditScoreClient.getCreditScore(creditScoreRequest).getScore();
        LoanProperties.Eligibility eligibility = properties.getEligibility();
        boolean isApproved = score.compareTo(eligibility.getMinCreditScore()) > 0
            && request.getAmount().compareTo(eligibility.getMaxLoanAmount()) <= 0;
        return LoanResponse.builder()
            .eligible(isApproved)
            .build();
    }

}
