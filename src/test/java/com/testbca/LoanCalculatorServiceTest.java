package com.testbca;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.service.LoanCalculatorService;
import com.testbca.service.implement.LoanCalculatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanCalculatorServiceTest {
    private LoanCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        // FIX: Instantiate the implementation class, not the interface.
        calculatorService = new LoanCalculatorServiceImpl();
    }

    @Test
    void testCarLoanCalculation() {
        // Data: Mobil Baru, Pinjaman 100jt, DP 35jt, Tenor 3 tahun
        LoanData data = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                2024,
                100_000_000,
                3,
                35_000_000
        );

        List<CalculationResult> results = calculatorService.calculateInstallments(data);

        // Verifikasi jumlah hasil
        assertEquals(3, results.size());

        // Verifikasi cicilan tahun pertama
        CalculationResult firstYear = results.get(0);

        // FIX: Use record-style accessors (e.g., year() instead of getYear()).
        assertEquals(1, firstYear.year());
        assertEquals(8.0, firstYear.interestRate(), 0.01);
        
        // Perhitungan Manual untuk verifikasi:
        // Pokok Pinjaman: 100,000,000 - 35,000,000 = 65,000,000
        // Total Pinjaman Thn 1: 65,000,000 * (1 + 0.08) = 70,200,000
        // Sisa tenor saat itu: 3 tahun
        // Cicilan Tahunan Thn 1: 70,200,000 / 3 = 23,400,000
        // Cicilan Bulanan Thn 1: 23,400,000 / 12 = 1,950,000
        assertEquals(1_950_000.0, firstYear.monthlyInstallment(), 0.01);
    }
}
