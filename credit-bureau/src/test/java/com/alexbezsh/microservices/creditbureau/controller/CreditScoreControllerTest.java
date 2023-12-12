package com.alexbezsh.microservices.creditbureau.controller;

import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.exception.NotFoundException;
import com.alexbezsh.microservices.common.model.ErrorResponse;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreRequest;
import com.alexbezsh.microservices.creditbureau.model.CreditScoreResponse;
import com.alexbezsh.microservices.creditbureau.service.CreditScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static com.alexbezsh.microservices.common.utils.JsonUtils.toJson;
import static com.alexbezsh.microservices.creditbureau.TestUtils.creditScoreRequest;
import static com.alexbezsh.microservices.creditbureau.TestUtils.creditScoreResponse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class CreditScoreControllerTest {

    private static final String BASE_URL = "/api/v1/credit-score";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditScoreService creditScoreService;

    @Test
    void getCreditScore() throws Exception {
        CreditScoreRequest request = creditScoreRequest();
        CreditScoreResponse expected = creditScoreResponse();

        doReturn(expected).when(creditScoreService).getCreditScore(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getCreditScoreShouldReturn400() throws Exception {
        CreditScoreRequest request = creditScoreRequest();
        request.setUserId(null);
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "userId: must not be blank");

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getCreditScoreShouldReturn404() throws Exception {
        CreditScoreRequest request = creditScoreRequest();
        String message = "message";
        ErrorResponse expected = new ErrorResponse(NOT_FOUND, message);

        doThrow(new NotFoundException(message)).when(creditScoreService).getCreditScore(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getCreditScoreShouldReturn500() throws Exception {
        CreditScoreRequest request = creditScoreRequest();
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR,
            "Unexpected error. Reason: null");

        doThrow(RuntimeException.class).when(creditScoreService).getCreditScore(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void getCreditScoreShouldReturn503() throws Exception {
        CreditScoreRequest request = creditScoreRequest();
        String message = "message";
        ErrorResponse expected = new ErrorResponse(SERVICE_UNAVAILABLE,
            "Currently service is unavailable. Reason: " + message);

        doThrow(new NotAvailableException(message))
            .when(creditScoreService).getCreditScore(request);

        mockMvc.perform(post(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().json(toJson(expected)));
    }

}
