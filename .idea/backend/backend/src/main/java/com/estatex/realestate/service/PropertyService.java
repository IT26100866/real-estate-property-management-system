package com.estatex.realestate.service;

import com.estatex.realestate.model.Property;
import com.estatex.realestate.repository.PropertyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(
            PropertyRepository propertyRepository
    ) {
        this.propertyRepository = propertyRepository;
    }

    public Property addProperty(Property property) {
        property.setId(null);

        if (property.getStatus() == null
                || property.getStatus().isBlank()) {
            property.setStatus("Available");
        }

        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Property not found"
                        )
                );
    }

    public List<Property> searchByLocation(String location) {
        return propertyRepository
                .findByLocationContainingIgnoreCase(location);
    }

    public List<Property> searchByType(String type) {
        return propertyRepository.findByTypeIgnoreCase(type);
    }

    public Property updateProperty(
            Long id,
            Property updatedProperty
    ) {
        Property existingProperty = getPropertyById(id);

        existingProperty.setPrice(updatedProperty.getPrice());
        existingProperty.setLocation(updatedProperty.getLocation());
        existingProperty.setType(updatedProperty.getType());
        existingProperty.setDescription(
                updatedProperty.getDescription()
        );
        existingProperty.setFeatures(
                updatedProperty.getFeatures()
        );
        existingProperty.setStatus(updatedProperty.getStatus());

        return propertyRepository.save(existingProperty);
    }

    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
    }
}