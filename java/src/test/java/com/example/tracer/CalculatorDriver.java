package com.example.tracer;

import com.example.calculator.Calculator;

/**
 * Driver class that exercises the Calculator functionality
 * for the tracer to analyze.
 */
public class CalculatorDriver {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        
        // Trigger the exception by subtracting with memory = 0
        calc.subtract(7, 3);
    }
} 