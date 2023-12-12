package com.alexbezsh.microservices.creditbureau.service;

import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.exception.NotFoundException;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import java.math.BigDecimal;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class CreditScoreService {

    private final Random random;

    @Value("${credit-bureau.maintenance.active}")
    private boolean isMaintenanceActive;

    public CreditScoreResponse getCreditScore(CreditScoreRequest request) {
        if (isMaintenanceActive) {
            throw new NotAvailableException("Credit Bureau is under maintenance. Try again later");
        }
        String userId = request.getUserId();
        log.info("Getting credit score for user with ID " + userId);
        if (random.nextInt(10) == 1) {
            throw new NotFoundException("User " + userId + " not found");
        }
        return CreditScoreResponse.builder()
            .score(new BigDecimal(random.nextInt(300, 701) + 150))
            .build();
    }

}
