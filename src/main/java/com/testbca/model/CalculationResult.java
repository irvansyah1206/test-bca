package com.testbca.model;

public record CalculationResult(
        int year,
        double monthlyInstallment,
        double interestRate
) {
}
