package com.example.jtracer;

import java.util.Map;

/**
 * Represents a debugging event with its type, location, and captured variable state
 */
public class DebugEvent {
    private final String type;
    private final String location;
    private final Map<String, String> variables;

    public DebugEvent(String type, String location, Map<String, String> variables) {
        this.type = type;
        this.location = location;
        this.variables = variables;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
} 