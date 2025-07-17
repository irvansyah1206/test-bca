package com.testbca.service.implement;

import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class MockApiServiceImplTest {

    private MockApiServiceImpl mockApiService;

    @BeforeEach
    void setUp() {
        mockApiService = new MockApiServiceImpl();
    }

    @Test
    void testParseJsonToLoanData_Success() throws Exception {
        // Arrange
        String jsonBody = "{" +
                "\"vehicleType\": \"MOBIL\"," +
                "\"vehicleCondition\": \"BARU\"," +
                "\"vehicleYear\": 2024," +
                "\"totalLoanAmount\": 250000000," +
                "\"loanTenure\": 5," +
                "\"downPayment\": 87500000" +
                "}";

        // Menggunakan reflection untuk mengakses metode privat
        Method parseMethod = MockApiServiceImpl.class.getDeclaredMethod("parseJsonToLoanData", String.class);
        parseMethod.setAccessible(true);

        // Act
        LoanData result = (LoanData) parseMethod.invoke(mockApiService, jsonBody);

        // Assert
        assertNotNull(result);
        assertEquals(VehicleType.MOBIL, result.vehicleType());
        assertEquals(VehicleCondition.BARU, result.vehicleCondition());
        assertEquals(2024, result.vehicleYear());
        assertEquals(250_000_000L, result.totalLoanAmount());
        assertEquals(5, result.loanTenure());
        assertEquals(87_500_000L, result.downPayment());
    }

    @Test
    void testParseJsonToLoanData_InvalidJson_ThrowsException() throws Exception {
        // Arrange
        String invalidJsonBody = "{\"vehicleType\": \"MOBIL\"}"; // JSON tidak lengkap

        Method parseMethod = MockApiServiceImpl.class.getDeclaredMethod("parseJsonToLoanData", String.class);
        parseMethod.setAccessible(true);

        // Act & Assert
        // InvocationTargetException akan membungkus eksepsi yang sebenarnya dari metode yang dipanggil
        Exception exception = assertThrows(Exception.class, () -> {
            parseMethod.invoke(mockApiService, invalidJsonBody);
        });

        // Memastikan ada eksepsi penyebab (dari pustaka JSON)
        assertNotNull(exception.getCause());
    }
}
