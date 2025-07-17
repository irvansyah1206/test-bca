package com.testbca.service.implement;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.service.LoanCalculatorService;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    private static final double BASE_INTEREST_RATE_MOBIL = 0.08;
    private static final double BASE_INTEREST_RATE_MOTOR = 0.09;
    private static final double ANNUAL_INTEREST_INCREASE = 0.001; // 0.1%
    private static final double BIENNIAL_INTEREST_INCREASE = 0.005; // 0.5%
    private static final int MAX_TENOR_YEARS = 6;
    private static final double MIN_DP_PERCENTAGE_BARU = 0.35;
    private static final double MIN_DP_PERCENTAGE_BEKAS = 0.25;

    @Override
    public List<CalculationResult> calculateInstallments(LoanData data) {
        validateInput(data);

        List<CalculationResult> results = new ArrayList<>();
        double currentPrincipal = (double) data.totalLoanAmount() - data.downPayment();
        double currentInterestRate = getBaseInterestRate(data.vehicleType());

        for (int year = 1; year <= data.loanTenure(); year++) {
            // Sesuaikan suku bunga untuk tahun berjalan.
            if (year > 1) {
                // Aturan kenaikan bunga: 0.1% setiap tahun genap, 0.5% setiap tahun ganjil (setelah tahun pertama)
                // Ini sesuai dengan contoh: Thn2 -> 8.1%, Thn3 -> 8.6%, Thn4 -> 8.7%, dst.
                currentInterestRate += (year % 2 == 0) ? ANNUAL_INTEREST_INCREASE : BIENNIAL_INTEREST_INCREASE;
            }

            // --- Logika Perhitungan Berdasarkan Tabel Contoh (Re-Amortisasi Tahunan) ---

            // 1. Hitung total kewajiban untuk tahun ini (Pokok Pinjaman Awal Tahun * (1 + Bunga))
            // Ini sesuai dengan kolom "Total Pinjaman" pada tabel contoh Anda untuk setiap tahun.
            double totalRepaymentForYear = currentPrincipal * (1 + currentInterestRate);

            // 2. Hitung Cicilan Tahunan dengan membaginya dengan sisa tenor.
            // Ini adalah pendekatan "flat rate" yang diaplikasikan pada sisa pokok setiap tahunnya.
            int remainingTenorYears = data.loanTenure() - year + 1;
            double yearlyInstallment = totalRepaymentForYear / remainingTenorYears;

            // 3. Hitung Cicilan Bulanan.
            double monthlyInstallment = yearlyInstallment / 12.0;

            // 4. Simpan hasil kalkulasi untuk tahun ini.
            results.add(new CalculationResult(year, currentInterestRate * 100, totalRepaymentForYear, monthlyInstallment));

            // --- Perbarui Pokok Pinjaman untuk Tahun Berikutnya ---
            // a. Hitung porsi bunga dari total pembayaran tahun ini.
            double interestPaidThisYear = currentPrincipal * currentInterestRate;
            // b. Hitung porsi pokok yang dibayarkan tahun ini.
            double principalPaidThisYear = yearlyInstallment - interestPaidThisYear;
            // c. Kurangi pokok pinjaman saat ini dengan porsi pokok yang dibayar.
            currentPrincipal -= principalPaidThisYear;
        }

        return results;
    }

    private void validateInput(LoanData data) {
        if (data.vehicleCondition() == VehicleCondition.BARU && data.vehicleYear() < (Year.now().getValue() - 1)) {
            throw new IllegalArgumentException("For a NEW vehicle, the year cannot be less than " + (Year.now().getValue() - 1));
        }

        if (data.loanTenure() > MAX_TENOR_YEARS) {
            throw new IllegalArgumentException("Loan tenor cannot exceed " + MAX_TENOR_YEARS + " years.");
        }

        double requiredDpPercentage = (data.vehicleCondition() == VehicleCondition.BARU)
                ? MIN_DP_PERCENTAGE_BARU
                : MIN_DP_PERCENTAGE_BEKAS;

        // DP minimum dihitung dari harga aset (total pinjaman).
        long minimumDp = (long) (data.totalLoanAmount() * requiredDpPercentage);

        if (data.downPayment() < minimumDp) {
            throw new IllegalArgumentException(
                    String.format("Minimum down payment for a %s vehicle is Rp %,d (%.0f%% from total loan), but provided Rp %,d.",
                            data.vehicleCondition().toString().toLowerCase(),
                            minimumDp,
                            requiredDpPercentage * 100,
                            data.downPayment())
            );
        }
    }

    private double getBaseInterestRate(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOBIL -> BASE_INTEREST_RATE_MOBIL;
            case MOTOR -> BASE_INTEREST_RATE_MOTOR;
        };
    }
}
