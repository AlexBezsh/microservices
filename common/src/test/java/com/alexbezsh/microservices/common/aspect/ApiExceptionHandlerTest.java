package com.alexbezsh.microservices.common.aspect;

import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.exception.NotFoundException;
import com.alexbezsh.microservices.common.exception.ValidationException;
import com.alexbezsh.microservices.common.model.ErrorResponse;
import feign.FeignException;
import feign.Request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static com.alexbezsh.microservices.common.utils.JsonUtils.toJson;
import static feign.Request.HttpMethod.GET;
import static feign.Request.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = {
    ApiExceptionHandlerTest.TestController.class,
    ApiExceptionHandlerTest.TestService.class,
    MethodValidationPostProcessor.class,
    ApiExceptionHandler.class},
    properties = {
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.config.enabled=false"})
class ApiExceptionHandlerTest {

    private static final String TEST_URL = "/test";
    private static final String NUM_PARAM = "num";
    private static final String DEFAULT_NUM = "1";
    private static final String DEFAULT_VALUE = "value";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestService testService;

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        TestPojo request = new TestPojo("");
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "value: must not be blank");

        mockMvc.perform(post(TEST_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleMethodArgumentTypeMismatchException() throws Exception {
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "Failed to convert value of type "
            + "'java.lang.String' to required type 'java.lang.Integer'; For input string: \"a\"");

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, "a"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleConstraintViolationException() throws Exception {
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, "test.num: must be greater than 0");

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, "-10"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleValidationException() throws Exception {
        String message = "validation failed";
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, message);

        doThrow(new ValidationException(message)).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleNotFoundException() throws Exception {
        String message = "not found";
        ErrorResponse expected = new ErrorResponse(NOT_FOUND, message);

        doThrow(new NotFoundException(message)).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleInternalServerException() throws Exception {
        String message = "some message";
        ErrorResponse expected = new ErrorResponse(INTERNAL_SERVER_ERROR,
            "Unexpected error. Reason: " + message);

        doThrow(new RuntimeException(message)).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleNotAvailableException() throws Exception {
        String message = "some message";
        ErrorResponse expected = new ErrorResponse(SERVICE_UNAVAILABLE,
            "Currently service is unavailable. Reason: " + message);

        doThrow(new NotAvailableException(message)).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleFeignExceptionIfBodyIsJson() throws Exception {
        String message = "some message";
        ErrorResponse originalResponse = new ErrorResponse(NOT_FOUND, message);
        byte[] body = toJson(originalResponse).getBytes();
        Request request = Request.create(POST, "", Map.of(), new byte[] {}, UTF_8, null);
        FeignException exception = new FeignException.NotFound(message, request, body, Map.of());

        doThrow(exception).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isNotFound())
            .andExpect(content().json(toJson(originalResponse)));
    }

    @Test
    void handleFeignExceptionIfBodyIsNotJson() throws Exception {
        String message = "some message";
        byte[] body = message.getBytes();
        Request request = Request.create(POST, "", Map.of(), new byte[] {}, UTF_8, null);
        FeignException exception = new FeignException.BadRequest(message, request, body, Map.of());
        ErrorResponse expected = new ErrorResponse(BAD_REQUEST, message);

        doThrow(exception).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));
    }

    @Test
    void handleFeignExceptionIfNoBody() throws Exception {
        Request request = Request.create(GET, "", Map.of(), null, UTF_8, null);
        FeignException exception = new FeignException.ServiceUnavailable(null, request, null, null);
        ErrorResponse expected = new ErrorResponse(SERVICE_UNAVAILABLE, "Unknown client error");

        doThrow(exception).when(testService).test();

        mockMvc.perform(post(TEST_URL)
                .content(toJson(new TestPojo(DEFAULT_VALUE)))
                .contentType(APPLICATION_JSON)
                .param(NUM_PARAM, DEFAULT_NUM))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().json(toJson(expected)));
    }

    @Validated
    @RestController
    @RequestMapping(TEST_URL)
    @RequiredArgsConstructor
    public static class TestController {

        private final TestService testService;

        @PostMapping(consumes = APPLICATION_JSON_VALUE)
        public TestPojo test(@RequestBody @Valid TestPojo request,
                             @RequestParam(NUM_PARAM) @Positive Integer num) {
            return testService.test();
        }

    }

    @Component
    public static class TestService {

        public TestPojo test() {
            return TestPojo.builder()
                .value("test")
                .build();
        }

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestPojo {

        @NotBlank
        private String value;

    }

}
