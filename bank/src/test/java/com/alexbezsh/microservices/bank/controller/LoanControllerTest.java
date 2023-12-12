package com.alexbezsh.microservices.bank.controller;

import com.alexbezsh.microservices.bank.model.api.LoanRequest;
import com.alexbezsh.microservices.bank.model.api.LoanResponse;
import com.alexbezsh.microservices.bank.service.LoanService;
import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.model.ErrorResponse;
import feign.FeignException;
import feign.Request;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static com.alexbezsh.microservices.bank.TestUtils.loanRequest;
import static com.alexbezsh.microservices.bank.TestUtils.loanResponse;
import static com.alexbezsh.microservices.common.utils.JsonUtils.toJson;
import static feign.Request.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
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
class LoanControllerTest {

    private static final String BASE_URL = "/api/v1/loans";
    private static final String ELIGIBILITY_URL = BASE_URL + "/eligibility";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    void checkLoanEligibility() throws Exception {
        LoanRequest request = loanRequest();
        LoanResponse expected = loanResponse();

        doReturn(expected).when(loanService).checkLoanEligibility(request);

        mockMvc.perform(post(ELIGIBILITY_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void checkLoanEligibilityShouldReturn400() throws Exception {
        LoanRequest request = loanRequest();
        request.setUserId(null);
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "userId: must not be blank");

        mockMvc.perform(post(ELIGIBILITY_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void checkLoanEligibilityShouldReturn404() throws Exception {
        LoanRequest request = loanRequest();
        String message = "some message";
        ErrorResponse originalResponse = new ErrorResponse(NOT_FOUND, message);
        byte[] body = toJson(originalResponse).getBytes();
        Request feignRequest = Request.create(POST, "", Map.of(), new byte[] {}, UTF_8, null);
        FeignException exception =
            new FeignException.NotFound(message, feignRequest, body, Map.of());
        ErrorResponse expected = new ErrorResponse(NOT_FOUND, message);

        doThrow(exception).when(loanService).checkLoanEligibility(request);

        mockMvc.perform(post(ELIGIBILITY_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void checkLoanEligibilityShouldReturn500() throws Exception {
        LoanRequest request = loanRequest();
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR,
            "Unexpected error. Reason: null");

        doThrow(RuntimeException.class).when(loanService).checkLoanEligibility(request);

        mockMvc.perform(post(ELIGIBILITY_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void checkLoanEligibilityShouldReturn503() throws Exception {
        LoanRequest request = loanRequest();
        String message = "message";
        ErrorResponse expected = new ErrorResponse(SERVICE_UNAVAILABLE,
            "Currently service is unavailable. Reason: " + message);

        doThrow(new NotAvailableException(message)).when(loanService).checkLoanEligibility(request);

        mockMvc.perform(post(ELIGIBILITY_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().json(toJson(expected)));
    }

}
