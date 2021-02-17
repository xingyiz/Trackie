package com.example.trackie.database;

import java.util.Map;

/**
 * Interface for entry objects convertible to Map<String,Object> objects
 */
public interface MapEntry {

    /**
     * Get Map representation of entry
     * @return Map object representation
     */
    Map<String,Object> retrieveRepresentation();

}
