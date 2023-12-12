package com.alexbezsh.microservices.configserver.repository;

import com.alexbezsh.microservices.configserver.model.db.Property;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findByAppNameAndProfileAndLabelAndKey(
        String appName, String profile, String label, String key);

}
