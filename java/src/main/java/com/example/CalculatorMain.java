package com.example;

public class CalculatorMain {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        
        // Perform some calculations
        System.out.println("Adding 2 + 3: " + calc.add(2, 3));
        System.out.println("Memory: " + calc.getMemory());
        
        System.out.println("Subtracting 7 - 4: " + calc.subtract(7, 4));
        System.out.println("Memory: " + calc.getMemory());
    }
} 