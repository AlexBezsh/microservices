package com.alexbezsh.microservices.configserver.service;

import com.alexbezsh.microservices.configserver.model.api.PropertyDto;
import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import com.alexbezsh.microservices.configserver.model.db.Property;
import com.alexbezsh.microservices.configserver.repository.PropertyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private static final String DEFAULT_LABEL = "master";

    private final PropertyRepository repository;

    public void upsert(UpsertPropertiesRequest request) {
        request.getProperties().forEach(this::save);
    }

    @SuppressWarnings("AvoidInlineConditionals")
    private void save(PropertyDto propertyDto) {
        Property property = Property.builder()
            .appName(propertyDto.getAppName())
            .profile(propertyDto.getProfile())
            .label(isBlank(propertyDto.getLabel()) ? DEFAULT_LABEL : propertyDto.getLabel())
            .key(propertyDto.getKey())
            .value(propertyDto.getValue())
            .build();
        Optional<Property> dbProperty = repository.findByAppNameAndProfileAndLabelAndKey(
            property.getAppName(), property.getProfile(), property.getLabel(), property.getKey());
        dbProperty.ifPresent(p -> property.setId(p.getId()));
        repository.save(property);
    }

}
