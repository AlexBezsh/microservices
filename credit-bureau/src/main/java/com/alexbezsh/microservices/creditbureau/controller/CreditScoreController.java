package com.alexbezsh.microservices.creditbureau.controller;

import com.alexbezsh.microservices.creditbureau.controller.api.CreditScoreApi;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import com.alexbezsh.microservices.creditbureau.service.CreditScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreditScoreController implements CreditScoreApi {

    private final CreditScoreService creditScoreService;

    @Override
    public CreditScoreResponse getCreditScore(CreditScoreRequest request) {
        return creditScoreService.getCreditScore(request);
    }

}
