package com.testbca.service.implement;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.service.LoanCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanCalculatorServiceTest {
    private LoanCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new LoanCalculatorServiceImpl();
    }

    @Test
    void testCarLoanCalculation() {
        // Data: Mobil Baru, Pinjaman 100jt, DP 35jt, Tenor 3 tahun
        LoanData data = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                Year.now().getValue(),
                100_000_000,
                3,
                35_000_000
        );
        List<CalculationResult> results = calculatorService.calculateInstallments(data);
        // Verifikasi jumlah hasil
        assertEquals(3, results.size());
        // --- Verifikasi Tahun Pertama ---
        CalculationResult firstYear = results.get(0);
        assertEquals(1, firstYear.year());
        assertEquals(8.0, firstYear.interestRate(), 0.01);
        assertEquals(1_950_000.0, firstYear.monthlyInstallment(), 0.01);

        // --- Verifikasi Tahun Kedua ---
        CalculationResult secondYear = results.get(1);
        assertEquals(2, secondYear.year());
        assertEquals(8.1, secondYear.interestRate(), 0.01);
        assertEquals(2_107_950.0, secondYear.monthlyInstallment(), 0.01);


        // --- Verifikasi Tahun Ketiga ---
        CalculationResult thirdYear = results.get(2);
        assertEquals(3, thirdYear.year());
        assertEquals(8.6, thirdYear.interestRate(), 0.01);
        assertEquals(2_289_233.7, thirdYear.monthlyInstallment(), 0.01);
    }

    @Test
    void testMotorLoanCalculation() {
        // Data: Motor Bekas, Pinjaman 20jt, DP 5jt, Tenor 2 tahun
        LoanData data = new LoanData(
                VehicleType.MOTOR,
                VehicleCondition.BEKAS,
                2022,
                20_000_000,
                2,
                5_000_000 // DP 25%
        );
        List<CalculationResult> results = calculatorService.calculateInstallments(data);
        assertEquals(2, results.size());

        CalculationResult firstYear = results.get(0);
        assertEquals(1, firstYear.year());
        assertEquals(9.0, firstYear.interestRate(), 0.01);
        assertEquals(681_250.0, firstYear.monthlyInstallment(), 0.01);

        CalculationResult secondYear = results.get(1);
        assertEquals(2, secondYear.year());
        assertEquals(9.1, secondYear.interestRate(), 0.01);
        assertEquals(743_243.75, secondYear.monthlyInstallment(), 0.01);
    }

    @Test
    void testInvalidNewVehicleYear_ThrowsException() {
        LoanData data = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                Year.now().getValue() - 2, // Tahun tidak valid untuk mobil baru
                100_000_000,
                1,
                35_000_000
        );
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateInstallments(data));
    }

    @Test
    void testExceedMaxLoanTenor_ThrowsException() {
        LoanData data = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                Year.now().getValue(),
                100_000_000,
                7, // Melebihi tenor maksimal (6 tahun)
                35_000_000
        );
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateInstallments(data));
    }

    @Test
    void testInsufficientDownPayment_NewVehicle_ThrowsException() {
        LoanData data = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                Year.now().getValue(),
                100_000_000,
                3,
                34_000_000 // DP < 35%
        );
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateInstallments(data));
    }

    @Test
    void testInsufficientDownPayment_UsedVehicle_ThrowsException() {
        LoanData data = new LoanData(
                VehicleType.MOTOR,
                VehicleCondition.BEKAS,
                2020,
                20_000_000,
                2,
                4_000_000 // DP < 25%
        );
        assertThrows(IllegalArgumentException.class, () -> calculatorService.calculateInstallments(data));
    }
}
