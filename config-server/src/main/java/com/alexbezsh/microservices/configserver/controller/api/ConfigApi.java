package com.alexbezsh.microservices.configserver.controller.api;

import com.alexbezsh.microservices.common.swagger.Default400And500Responses;
import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Custom Endpoints")
@RequestMapping("/api/v1/properties")
public interface ConfigApi {

    @Default400And500Responses
    @Operation(summary = "Update Properties")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    void upsert(@RequestBody @Valid UpsertPropertiesRequest request);

}
