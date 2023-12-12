package com.alexbezsh.microservices.configserver;

import com.alexbezsh.microservices.configserver.model.api.PropertyDto;
import com.alexbezsh.microservices.configserver.model.api.UpsertPropertiesRequest;
import com.alexbezsh.microservices.configserver.model.db.Property;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

    public static final long PROPERTY_1_ID = 1L;
    public static final String APP_NAME_1 = "app1";
    public static final String PROFILE_1 = "profile1";
    public static final String LABEL_1 = "label1";
    public static final String KEY_1 = "key1";
    public static final String VALUE_1 = "value1";

    public static final String APP_NAME_2 = "app2";
    public static final String PROFILE_2 = "profile2";
    public static final String LABEL_2 = "master";
    public static final String KEY_2 = "key2";
    public static final String VALUE_2 = "value2";

    public static UpsertPropertiesRequest upsertPropertiesRequest() {
        return UpsertPropertiesRequest.builder()
            .properties(List.of(propertyDto1(), propertyDto2()))
            .build();
    }

    public static PropertyDto propertyDto1() {
        return PropertyDto.builder()
            .appName(APP_NAME_1)
            .profile(PROFILE_1)
            .label(LABEL_1)
            .key(KEY_1)
            .value(VALUE_1)
            .build();
    }

    public static PropertyDto propertyDto2() {
        return PropertyDto.builder()
            .appName(APP_NAME_2)
            .profile(PROFILE_2)
            .key(KEY_2)
            .value(VALUE_2)
            .build();
    }

    public static Property property1() {
        return Property.builder()
            .id(PROPERTY_1_ID)
            .appName(APP_NAME_1)
            .profile(PROFILE_1)
            .label(LABEL_1)
            .key(KEY_1)
            .value(VALUE_1)
            .build();
    }

    public static Property property2() {
        return Property.builder()
            .appName(APP_NAME_2)
            .profile(PROFILE_2)
            .label(LABEL_2)
            .key(KEY_2)
            .value(VALUE_2)
            .build();
    }

}
