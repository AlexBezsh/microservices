package com.alexbezsh.microservices.creditbureau.service;

import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.exception.NotFoundException;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static com.alexbezsh.microservices.creditbureau.TestUtils.USER_ID;
import static com.alexbezsh.microservices.creditbureau.TestUtils.creditScoreRequest;
import static com.alexbezsh.microservices.creditbureau.TestUtils.creditScoreResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CreditScoreServiceTest {

    @InjectMocks
    private CreditScoreService testedInstance;

    @Spy
    private Random random = new Random(1);

    @Test
    void getCreditScore() {
        CreditScoreRequest request = creditScoreRequest();
        CreditScoreResponse expected = creditScoreResponse();

        ReflectionTestUtils.setField(testedInstance, "isMaintenanceActive", false);

        CreditScoreResponse actual = testedInstance.getCreditScore(request);

        assertEquals(expected, actual);
    }

    @Test
    void getCreditScoreShouldThrowNotAvailableException() {
        CreditScoreRequest request = creditScoreRequest();

        ReflectionTestUtils.setField(testedInstance, "isMaintenanceActive", true);

        NotAvailableException exception = assertThrows(NotAvailableException.class,
            () -> testedInstance.getCreditScore(request));

        assertEquals("Credit Bureau is under maintenance. Try again later", exception.getMessage());
    }

    @Test
    void getCreditScoreShouldThrowNotFoundException() {
        CreditScoreRequest request = creditScoreRequest();

        ReflectionTestUtils.setField(testedInstance, "isMaintenanceActive", false);
        doReturn(1).when(random).nextInt(10);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> testedInstance.getCreditScore(request));

        assertEquals("User " + USER_ID + " not found", exception.getMessage());
    }

}
