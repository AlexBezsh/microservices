package com.alexbezsh.microservices.configserver.service;

import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import com.alexbezsh.microservices.configserver.repository.PropertyRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.alexbezsh.microservices.configserver.TestUtils.APP_NAME_1;
import static com.alexbezsh.microservices.configserver.TestUtils.APP_NAME_2;
import static com.alexbezsh.microservices.configserver.TestUtils.KEY_1;
import static com.alexbezsh.microservices.configserver.TestUtils.KEY_2;
import static com.alexbezsh.microservices.configserver.TestUtils.LABEL_1;
import static com.alexbezsh.microservices.configserver.TestUtils.LABEL_2;
import static com.alexbezsh.microservices.configserver.TestUtils.PROFILE_1;
import static com.alexbezsh.microservices.configserver.TestUtils.PROFILE_2;
import static com.alexbezsh.microservices.configserver.TestUtils.property1;
import static com.alexbezsh.microservices.configserver.TestUtils.property2;
import static com.alexbezsh.microservices.configserver.TestUtils.upsertPropertiesRequest;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @InjectMocks
    private ConfigService testedInstance;

    @Mock
    private PropertyRepository repository;

    @Test
    void upsert() {
        UpsertPropertiesRequest request = upsertPropertiesRequest();

        doReturn(Optional.of(property1())).when(repository)
            .findByAppNameAndProfileAndLabelAndKey(APP_NAME_1, PROFILE_1, LABEL_1, KEY_1);
        doReturn(Optional.empty()).when(repository)
            .findByAppNameAndProfileAndLabelAndKey(APP_NAME_2, PROFILE_2, LABEL_2, KEY_2);

        testedInstance.upsert(request);

        verify(repository).save(property1());
        verify(repository).save(property2());
    }

}
