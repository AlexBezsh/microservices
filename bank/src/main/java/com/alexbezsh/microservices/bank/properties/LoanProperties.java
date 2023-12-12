package com.alexbezsh.microservices.bank.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties("bank.loans")
public class LoanProperties {

    private boolean blockAll;

    @Valid
    @NotNull
    private Eligibility eligibility;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Eligibility {

        @NotNull
        private BigDecimal minCreditScore;

        @NotNull
        private BigDecimal maxLoanAmount;

    }

}
