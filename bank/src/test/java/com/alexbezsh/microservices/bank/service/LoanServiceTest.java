package com.alexbezsh.microservices.bank.service;

import com.alexbezsh.microservices.bank.client.CreditScoreClient;
import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.bank.model.client.CreditScoreResponse;
import com.alexbezsh.microservices.bank.properties.LoanProperties;
import com.alexbezsh.microservices.common.exception.NotAvailableException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.microservices.bank.TestUtils.MAX_LOAN_AMOUNT;
import static com.alexbezsh.microservices.bank.TestUtils.MIN_CREDIT_SCORE;
import static com.alexbezsh.microservices.bank.TestUtils.creditScoreRequest;
import static com.alexbezsh.microservices.bank.TestUtils.creditScoreResponse;
import static com.alexbezsh.microservices.bank.TestUtils.loanProperties;
import static com.alexbezsh.microservices.bank.TestUtils.loanRequest;
import static com.alexbezsh.microservices.bank.TestUtils.loanResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    private LoanService testedInstance;

    @Spy
    private LoanProperties properties = loanProperties();

    @Mock
    private CreditScoreClient creditScoreClient;

    @Test
    void checkLoanEligibilityHappyPath() {
        LoanRequest request = loanRequest();
        LoanResponse expected = loanResponse();

        doReturn(creditScoreResponse()).when(creditScoreClient)
            .getCreditScore(creditScoreRequest());

        LoanResponse actual = testedInstance.checkLoanEligibility(request);

        assertEquals(expected, actual);
    }

    @Test
    void checkLoanEligibilityWhenCreditScoreIsInsufficient() {
        LoanRequest request = loanRequest();
        CreditScoreResponse creditScoreResponse = CreditScoreResponse.builder()
            .score(MIN_CREDIT_SCORE.subtract(BigDecimal.ONE))
            .build();
        LoanResponse expected = LoanResponse.builder().eligible(false).build();

        doReturn(creditScoreResponse).when(creditScoreClient).getCreditScore(creditScoreRequest());

        LoanResponse actual = testedInstance.checkLoanEligibility(request);

        assertEquals(expected, actual);
    }

    @Test
    void checkLoanEligibilityWhenTooBigLoanAmount() {
        LoanRequest request = loanRequest();
        request.setAmount(MAX_LOAN_AMOUNT.add(BigDecimal.ONE));
        LoanResponse expected = LoanResponse.builder().eligible(false).build();

        doReturn(creditScoreResponse()).when(creditScoreClient)
            .getCreditScore(creditScoreRequest());

        LoanResponse actual = testedInstance.checkLoanEligibility(request);

        assertEquals(expected, actual);
    }

    @Test
    void checkLoanEligibilityWhenLoansAreBlocked() {
        LoanRequest request = loanRequest();

        doReturn(true).when(properties).isBlockAll();

        assertThrows(NotAvailableException.class,
            () -> testedInstance.checkLoanEligibility(request));

        verify(creditScoreClient, never()).getCreditScore(any());
    }

}
