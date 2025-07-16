package com.testbca.service.implement;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import com.testbca.model.VehicleType;
import com.testbca.service.LoanCalculatorService;

import java.util.ArrayList;
import java.util.List;

// REFACTOR: Nama kelas diubah untuk menandakan ini adalah sebuah implementasi.
public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    // REFACTOR: Ekstrak "Magic Numbers" menjadi konstanta yang jelas.
    // Ini membuat kode lebih mudah dibaca dan diubah di satu tempat.
    private static final double BASE_INTEREST_RATE_CAR = 0.08;
    private static final double BASE_INTEREST_RATE_MOTORCYCLE = 0.09;
    private static final double INTEREST_INCREASE_REGULAR = 0.001; // 0.1%
    private static final double INTEREST_INCREASE_SPECIAL = 0.005; // 0.5%

    @Override
    public List<CalculationResult> calculateInstallments(LoanData data) {
        List<CalculationResult> results = new ArrayList<>();
        long principalLoan = data.totalLoanAmount() - data.downPayment();
        double remainingLoan = principalLoan;

        double baseInterestRate = (data.vehicleType() == VehicleType.MOBIL)
                ? BASE_INTEREST_RATE_CAR
                : BASE_INTEREST_RATE_MOTORCYCLE;

        double currentInterestRate = baseInterestRate;

        for (int year = 1; year <= data.loanTenure(); year++) {
            if (year > 1) {
                // REFACTOR: Logika penentuan kenaikan bunga dibuat lebih eksplisit.
                currentInterestRate += (year % 2 != 0)
                        ? INTEREST_INCREASE_SPECIAL
                        : INTEREST_INCREASE_REGULAR;
            }

            double totalLoanThisYear = remainingLoan * (1 + currentInterestRate);
            int remainingTenure = data.loanTenure() - year + 1;
            double yearlyInstallment = totalLoanThisYear / remainingTenure;
            double monthlyInstallment = yearlyInstallment / 12;

            // REFACTOR: Menggunakan accessor dari record.
            results.add(new CalculationResult(year, monthlyInstallment, currentInterestRate * 100));

            remainingLoan = totalLoanThisYear - yearlyInstallment;
        }

        return results;
    }
}
