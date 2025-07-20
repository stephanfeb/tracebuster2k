package com.example;

import com.example.jtracer.JTracerSession;

public class TracerDemo {
    public static void main(String[] args) {
        try {
            System.out.println("Starting JTracer demonstration...");
            
            // Create a debugging session for the Calculator main class
            try (JTracerSession tracer = new JTracerSession("com.example.CalculatorMain")) {
                System.out.println("Created JTracer session for CalculatorMain");
                
                // Set breakpoints at interesting locations in Calculator class
                tracer.setBreakpoint("com.example.Calculator", 7);  // Inside add method
                tracer.setBreakpoint("com.example.Calculator", 13); // Inside subtract method
                System.out.println("Set breakpoints in Calculator class");
                
                // Run the debugging session
                System.out.println("Starting debug session...");
                tracer.run();
                System.out.println("Debug session completed");
                
                // Get the collected debug events as JSON
                String debugEvents = tracer.getEventsAsJson();
                System.out.println("\nCollected Debug Events:");
                System.out.println(debugEvents);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 