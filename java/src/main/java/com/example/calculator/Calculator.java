package com.example.calculator;

/**
 * A simple calculator that maintains a memory of its last result.
 */
public class Calculator {
    private int memory = 0;

    public int add(int a, int b) {
        int result = a + b;
        memory = result;
        return result;
    }

    public int subtract(int a, int b) {
        if (memory == 0) {
            throw new IllegalStateException("Cannot subtract when memory is zero!");
        }
        int result = a - b;
        memory = result;
        return result;
    }

    public int getMemory() {
        return memory;
    }
} 