package com.testbca.service.implement;

import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.view.ConsoleView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// Kelas mock untuk ConsoleView untuk tujuan pengujian
class MockConsoleView extends ConsoleView {
    private final List<String> inputs;
    private int inputIndex = 0;

    public MockConsoleView(List<String> inputs) {
        this.inputs = inputs;
    }

    @Override
    public String prompt(String message) {
        if (inputIndex < inputs.size()) {
            return inputs.get(inputIndex++);
        }
        fail("Input mock tidak cukup untuk prompt: " + message);
        return ""; // Seharusnya tidak tercapai
    }
}

class ConsoleInputServiceTest {

    private ConsoleInputService consoleInputService;

    @TempDir
    Path tempDir;

    @Test
    void testReadFromFile_Success() throws IOException {
        // Arrange
        consoleInputService = new ConsoleInputService(null); // View tidak digunakan di metode ini
        Path file = tempDir.resolve("input.txt");
        List<String> lines = Arrays.asList(
                "MOBIL",
                "BARU",
                "2024",
                "100000000",
                "4",
                "35000000"
        );
        Files.write(file, lines);

        // Act
        LoanData data = consoleInputService.readFromFile(file.toString());

        // Assert
        assertNotNull(data);
        assertEquals(VehicleType.MOBIL, data.vehicleType());
        assertEquals(VehicleCondition.BARU, data.vehicleCondition());
        assertEquals(2024, data.vehicleYear());
        assertEquals(100_000_000L, data.totalLoanAmount());
        assertEquals(4, data.loanTenure());
        assertEquals(35_000_000L, data.downPayment());
    }

    @Test
    void testReadFromFile_IncompleteFile_ThrowsException() throws IOException {
        // Arrange
        consoleInputService = new ConsoleInputService(null);
        Path file = tempDir.resolve("incomplete_input.txt");
        Files.write(file, Arrays.asList("MOBIL", "BARU"));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            consoleInputService.readFromFile(file.toString());
        });
        assertEquals("File input tidak lengkap. Harus berisi 6 baris.", exception.getMessage());
    }

    @Test
    void testGetLoanDataFromUser_Success() {
        // Arrange
        List<String> userInputs = Arrays.asList(
                "MOBIL",
                "BARU",
                "2024",
                "100000000",
                "4",
                "35000000"
        );
        MockConsoleView mockView = new MockConsoleView(userInputs);
        consoleInputService = new ConsoleInputService(mockView);

        // Act
        LoanData data = consoleInputService.getLoanDataFromUser();

        // Assert
        assertNotNull(data);
        assertEquals(VehicleType.MOBIL, data.vehicleType());
        assertEquals(VehicleCondition.BARU, data.vehicleCondition());
        assertEquals(2024, data.vehicleYear());
        assertEquals(100_000_000L, data.totalLoanAmount());
        assertEquals(4, data.loanTenure());
        assertEquals(35_000_000L, data.downPayment());
    }

    @Test
    void testValidateLoanData_InsufficientDownPayment_ThrowsException() {
        // Arrange
        // Test ini secara langsung memvalidasi data pinjaman yang sudah ada.
        // DP (10jt) kurang dari syarat minimum 35% (35jt) untuk mobil baru dengan pinjaman 100jt.
        consoleInputService = new ConsoleInputService(null); // ConsoleView tidak diperlukan untuk validasi langsung
        LoanData invalidData = new LoanData(
                VehicleType.MOBIL,
                VehicleCondition.BARU,
                Year.now().getValue(),
                100_000_000,
                4,
                10_000_000 // DP tidak cukup
        );

        // Act & Assert
        // Memastikan bahwa IllegalArgumentException dilempar ketika validasi gagal.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Panggil metode validasi secara langsung
            consoleInputService.validateLoanData(invalidData);
        });

        // Verifikasi pesan error untuk memastikan validasi yang benar telah gagal.
        assertTrue(exception.getMessage().contains("Jumlah DP untuk kendaraan baru minimal 35%"));
    }
}
