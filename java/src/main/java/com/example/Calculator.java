package com.example;

public class Calculator {
    private int memory = 0;

    public int add(int a, int b) {
        int result = a + b;
        memory = result;
        return result;
    }

    public int subtract(int a, int b) {
        int result = a - b;
        memory = result;
        return result;
    }

    public int getMemory() {
        return memory;
    }
} 