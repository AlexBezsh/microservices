package com.alexbezsh.microservices.configserver.controller;

import com.alexbezsh.microservices.configserver.controller.api.ConfigApi;
import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import com.alexbezsh.microservices.configserver.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfigController implements ConfigApi {

    private final ConfigService configService;

    @Override
    public void upsert(UpsertPropertiesRequest request) {
        configService.upsert(request);
    }

}
