package com.example.tracer;

import com.example.jtracer.JTracerSession;
import com.example.calculator.Calculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.sun.jdi.VMDisconnectedException;

/**
 * Tracer tests that analyze the Calculator's runtime behavior.
 * These tests demonstrate how LLM agents can use JTracer to understand
 * and verify the Calculator's implementation.
 */
public class CalculatorTracerTest {
    
    @Test
    void analyzeCalculatorMemoryBehavior() throws Exception {
        // First verify the calculator works as expected
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
        assertEquals(4, calc.subtract(7, 3));
        
        String debugEvents = null;
        
        // Now use JTracer to analyze its behavior
        try (JTracerSession tracer = new JTracerSession("com.example.tracer.CalculatorDriver")) {
            // Set breakpoints at memory updates
            tracer.setBreakpoint("com.example.calculator.Calculator", 9);  // memory = result in add
            tracer.setBreakpoint("com.example.calculator.Calculator", 15); // memory = result in subtract
            
            // Run the debug session and get events
            tracer.run();
            debugEvents = tracer.getEventsAsJson();
        } catch (VMDisconnectedException e) {
            // This is expected when the VM terminates
        }
        
        // Basic validation
        assertNotNull(debugEvents, "Should have captured debug events");
        assertFalse(debugEvents.isEmpty(), "Should have non-empty debug events");
        
        // Show the memory behavior analysis
        System.out.println("Calculator Memory State Analysis:");
        System.out.println(debugEvents);
    }

    @Test
    void analyzeCalculatorExceptionBehavior() throws Exception {
        // First verify the calculator throws as expected
        Calculator calc = new Calculator();
        assertThrows(IllegalStateException.class, () -> calc.subtract(7, 3));
        
        String debugEvents = null;
        
        // Now use JTracer to analyze the exception behavior
        try (JTracerSession tracer = new JTracerSession("com.example.tracer.CalculatorDriver")) {
            // Set breakpoint at the exception throw
            tracer.setBreakpoint("com.example.calculator.Calculator", 10);  // where we throw
            
            // Run the debug session and get events
            tracer.run();
            debugEvents = tracer.getEventsAsJson();
        } catch (VMDisconnectedException e) {
            // This is expected when the VM terminates
        }
        
        // Basic validation
        assertNotNull(debugEvents, "Should have captured debug events");
        assertFalse(debugEvents.isEmpty(), "Should have non-empty debug events");
        
        // Show the exception analysis
        System.out.println("Calculator Exception Analysis:");
        System.out.println(debugEvents);
    }
} 