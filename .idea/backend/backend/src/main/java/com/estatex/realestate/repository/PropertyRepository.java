package com.estatex.realestate.repository;

import com.estatex.realestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository
        extends JpaRepository<Property, Long> {

    List<Property> findByLocationContainingIgnoreCase(
            String location
    );

    List<Property> findByTypeIgnoreCase(String type);
}