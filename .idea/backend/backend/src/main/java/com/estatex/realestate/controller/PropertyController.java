package com.estatex.realestate.controller;

import com.estatex.realestate.model.Property;
import com.estatex.realestate.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(
            PropertyService propertyService
    ) {
        this.propertyService = propertyService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Property createProperty(
            @Valid @RequestBody Property property
    ) {
        return propertyService.addProperty(property);
    }

    // READ - All properties
    @GetMapping
    public List<Property> getAllProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long id
    ) {
        if (id != null) {
            return List.of(propertyService.getPropertyById(id));
        }

        if (location != null && !location.isBlank()) {
            return propertyService.searchByLocation(location);
        }

        if (type != null && !type.isBlank()) {
            return propertyService.searchByType(type);
        }

        return propertyService.getAllProperties();
    }

    // READ - One property
    @GetMapping("/{id}")
    public Property getPropertyById(
            @PathVariable Long id
    ) {
        return propertyService.getPropertyById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Property updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody Property property
    ) {
        return propertyService.updateProperty(id, property);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(
            @PathVariable Long id
    ) {
        propertyService.deleteProperty(id);
    }
}