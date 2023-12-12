package com.alexbezsh.microservices.bank.client;

import com.alexbezsh.microservices.bank.model.client.CreditScoreRequest;
import com.alexbezsh.microservices.bank.model.client.CreditScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "credit-bureau", path = "/api/v1/credit-score")
public interface CreditScoreClient {

    @PostMapping
    CreditScoreResponse getCreditScore(CreditScoreRequest request);

}
