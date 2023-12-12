package com.alexbezsh.microservices.bank;

import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.bank.model.client.CreditScoreRequest;
import com.alexbezsh.microservices.bank.model.client.CreditScoreResponse;
import com.alexbezsh.microservices.bank.properties.LoanProperties;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static final String USER_ID = "1";
    public static final BigDecimal AMOUNT = new BigDecimal("123.45");
    public static final BigDecimal CREDIT_SCORE = new BigDecimal(630);
    public static final BigDecimal MIN_CREDIT_SCORE = new BigDecimal(620);
    public static final BigDecimal MAX_LOAN_AMOUNT = new BigDecimal(10000);

    public static LoanRequest loanRequest() {
        return LoanRequest.builder()
            .userId(USER_ID)
            .amount(AMOUNT)
            .build();
    }

    public static LoanResponse loanResponse() {
        return LoanResponse.builder()
            .eligible(true)
            .build();
    }

    public static CreditScoreRequest creditScoreRequest() {
        return CreditScoreRequest.builder().userId(USER_ID).build();
    }

    public static CreditScoreResponse creditScoreResponse() {
        return CreditScoreResponse.builder().score(CREDIT_SCORE).build();
    }

    public static LoanProperties loanProperties() {
        return LoanProperties.builder()
            .blockAll(false)
            .eligibility(LoanProperties.Eligibility.builder()
                .minCreditScore(MIN_CREDIT_SCORE)
                .maxLoanAmount(MAX_LOAN_AMOUNT)
                .build())
            .build();
    }

}
