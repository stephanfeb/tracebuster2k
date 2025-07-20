package com.example.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Production tests for the Calculator class.
 */
public class CalculatorTest {
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void testAdd() {
        assertEquals(5, calculator.add(2, 3), "2 + 3 should equal 5");
        assertEquals(5, calculator.getMemory(), "Memory should store last result");
    }
    
    @Test
    void testSubtract() {
        assertEquals(-1, calculator.subtract(2, 3), "2 - 3 should equal -1");
        assertEquals(-1, calculator.getMemory(), "Memory should store last result");
    }
    
    @Test
    void testMemoryUpdates() {
        calculator.add(10, 5);
        assertEquals(15, calculator.getMemory(), "Memory should be 15 after 10 + 5");
        
        calculator.subtract(7, 3);
        assertEquals(4, calculator.getMemory(), "Memory should be 4 after 7 - 3");
    }
    
    @Test
    void testMultipleOperations() {
        assertEquals(5, calculator.add(2, 3), "First operation");
        assertEquals(10, calculator.add(7, 3), "Second operation");
        assertEquals(-2, calculator.subtract(3, 5), "Third operation");
        assertEquals(-2, calculator.getMemory(), "Final memory state");
    }
} 