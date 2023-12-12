package com.alexbezsh.microservices.configserver.controller;

import com.alexbezsh.microservices.common.model.ErrorResponse;
import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import com.alexbezsh.microservices.configserver.service.ConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import static com.alexbezsh.microservices.common.utils.JsonUtils.toJson;
import static com.alexbezsh.microservices.configserver.TestUtils.upsertPropertiesRequest;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ConfigControllerTest {

    private static final String BASE_URL = "/api/v1/properties";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    @Test
    void upsertProperties() throws Exception {
        UpsertPropertiesRequest request = upsertPropertiesRequest();

        mockMvc.perform(put(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(configService).upsert(request);
    }

    @Test
    void upsertPropertiesShouldReturn400() throws Exception {
        UpsertPropertiesRequest request = upsertPropertiesRequest();
        request.getProperties().get(0).setKey(null);
        ErrorResponse expected = new ErrorResponse(HttpStatus.BAD_REQUEST,
            "properties[0].key: must not be blank");

        mockMvc.perform(put(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(toJson(expected)));

        verify(configService, never()).upsert(request);
    }

    @Test
    void upsertPropertiesShouldReturn500() throws Exception {
        UpsertPropertiesRequest request = upsertPropertiesRequest();
        ErrorResponse expected = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "Unexpected error. Reason: null");

        doThrow(RuntimeException.class).when(configService).upsert(request);

        mockMvc.perform(put(BASE_URL)
                .content(toJson(request))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json(toJson(expected)));
    }

}
