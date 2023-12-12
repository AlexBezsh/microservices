package com.alexbezsh.microservices.bank.controller;

import com.alexbezsh.microservices.bank.controller.api.LoanApi;
import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.bank.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoanController implements LoanApi {

    private final LoanService loanService;

    @Override
    public LoanResponse checkLoanEligibility(LoanRequest request) {
        return loanService.checkLoanEligibility(request);
    }

}
