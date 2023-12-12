package com.alexbezsh.microservices.creditbureau;

import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static final String USER_ID = "1";
    public static final BigDecimal CREDIT_SCORE = new BigDecimal(572);

    public static CreditScoreRequest creditScoreRequest() {
        return CreditScoreRequest.builder()
            .userId(USER_ID)
            .build();
    }

    public static CreditScoreResponse creditScoreResponse() {
        return CreditScoreResponse.builder()
            .score(CREDIT_SCORE)
            .build();
    }

}
