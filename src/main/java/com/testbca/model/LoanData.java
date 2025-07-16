package com.testbca.model;

public record LoanData(
        VehicleType vehicleType,
        VehicleCondition vehicleCondition,
        int vehicleYear,
        long totalLoanAmount,
        int loanTenure,
        long downPayment
) {
}
