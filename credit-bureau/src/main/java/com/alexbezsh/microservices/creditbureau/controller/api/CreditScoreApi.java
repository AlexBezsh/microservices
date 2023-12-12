package com.alexbezsh.microservices.creditbureau.controller.api;

import com.alexbezsh.microservices.common.swagger.Default400And404And500Responses;
import com.alexbezsh.microservices.common.swagger.Default503Response;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Credit Score")
@RequestMapping("/api/v1/credit-score")
public interface CreditScoreApi {

    @Default503Response
    @Default400And404And500Responses
    @Operation(summary = "Get Credit Score")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CreditScoreResponse getCreditScore(@RequestBody @Valid @NotNull CreditScoreRequest request);

}
