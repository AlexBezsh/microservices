package com.alexbezsh.microservices.bank.controller.api;

import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.common.swagger.Default400And404And500Responses;
import com.alexbezsh.microservices.common.swagger.Default503Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Loans")
@RequestMapping("/api/v1/loans")
public interface LoanApi {

    @Default503Response
    @Default400And404And500Responses
    @Operation(summary = "Check Loan Eligibility")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/eligibility", consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    LoanResponse checkLoanEligibility(@RequestBody @Valid LoanRequest request);

}
