package com.testbca.service.implement;

import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.service.DataInputService;
import com.testbca.view.ConsoleView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.function.Supplier;

/**
 * Service class for handling console-based input related to loan data.
 * Provides methods for reading loan details from a file or interactively
 * obtaining loan details from a user through a console interface.
 * This class validates the input values to ensure consistency and correctness.
 */
public class ConsoleInputService implements DataInputService {

    private static final int MIN_TENURE = 1;
    private static final int MAX_TENURE = 6;
    private static final long MAX_LOAN_AMOUNT = 1_000_000_000L;
    private static final double DP_PERCENTAGE_NEW = 0.35;
    private static final double DP_PERCENTAGE_USED = 0.25;

    private final ConsoleView view;

    public ConsoleInputService(ConsoleView view) {
        this.view = view;
    }

    @Override
    public LoanData readFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (lines.size() < 6) {
            throw new IOException("File input tidak lengkap. Harus berisi 6 baris.");
        }

        LoanData data = new LoanData(
                VehicleType.valueOf(lines.get(0).toUpperCase()),
                VehicleCondition.valueOf(lines.get(1).toUpperCase()),
                Integer.parseInt(lines.get(2)),
                Long.parseLong(lines.get(3)),
                Integer.parseInt(lines.get(4)),
                Long.parseLong(lines.get(5))
        );

        validateLoanData(data);
        return data;
    }

    @Override
    public LoanData getLoanDataFromUser() {
        return retryOnFailure(() -> {
            VehicleType vehicleType = getVehicleTypeFromUser();
            VehicleCondition condition = getVehicleConditionFromUser();
            int vehicleYear = getVehicleYearFromUser(condition);
            long totalLoan = getTotalLoanFromUser();
            int tenure = getLoanTenureFromUser();
            long downPayment = getDownPaymentFromUser(totalLoan, condition);

            LoanData data = new LoanData(vehicleType, condition, vehicleYear, totalLoan, tenure, downPayment);
            validateLoanData(data); // Lakukan validasi terpusat sebelum mengembalikan
            return data;
        });
    }

    private VehicleType getVehicleTypeFromUser() {
        return retryOnFailure(() -> VehicleType.valueOf(view.prompt("Input Jenis Kendaraan (Motor|Mobil): ").toUpperCase()));
    }

    private VehicleCondition getVehicleConditionFromUser() {
        return retryOnFailure(() -> VehicleCondition.valueOf(view.prompt("Input Kondisi Kendaraan (Baru|Bekas): ").toUpperCase()));
    }

    private int getVehicleYearFromUser(VehicleCondition condition) {
        return retryOnFailure(() -> {
            String input = view.prompt("Input Tahun Kendaraan (4 digit numerik): ");
            if (input.length() != 4) throw new IllegalArgumentException("Tahun kendaraan harus 4 digit.");
            return Integer.parseInt(input);
        });
    }

    private long getTotalLoanFromUser() {
        return retryOnFailure(() -> {
            long amount = Long.parseLong(view.prompt("Input Jumlah Pinjaman Total (maks 1 Miliar): "));
            if (amount > MAX_LOAN_AMOUNT) throw new IllegalArgumentException("Jumlah pinjaman tidak boleh lebih dari 1 Miliar.");
            if (amount <= 0) throw new IllegalArgumentException("Jumlah pinjaman harus positif.");
            return amount;
        });
    }

    private int getLoanTenureFromUser() {
        return retryOnFailure(() -> {
            int tenure = Integer.parseInt(view.prompt("Input Tenor Pinjaman (1-6 tahun): "));
            if (tenure < MIN_TENURE || tenure > MAX_TENURE) {
                throw new IllegalArgumentException("Tenor pinjaman harus antara " + MIN_TENURE + " sampai " + MAX_TENURE + " tahun.");
            }
            return tenure;
        });
    }

    private long getDownPaymentFromUser(long totalLoan, VehicleCondition condition) {
         return retryOnFailure(() -> {
            long dp = Long.parseLong(view.prompt("Input Jumlah DP: "));
            if (dp < 0) throw new IllegalArgumentException("DP tidak boleh negatif.");
            return dp;
        });
    }
    
    private void validateLoanData(LoanData data) {
        validateVehicleYear(data.vehicleYear(), data.vehicleCondition());
        validateDownPayment(data.downPayment(), data.totalLoanAmount(), data.vehicleCondition());
    }

    private void validateVehicleYear(int year, VehicleCondition condition) {
        if (condition == VehicleCondition.BARU) {
            int currentYear = Year.now().getValue();
            if (year < (currentYear - 1)) {
                throw new IllegalArgumentException("Untuk kendaraan BARU, tahun tidak boleh lebih tua dari " + (currentYear - 1));
            }
        }
    }

    private void validateDownPayment(long downPayment, long totalLoanAmount, VehicleCondition condition) {
        double requiredDpPercentage = (condition == VehicleCondition.BARU) ? DP_PERCENTAGE_NEW : DP_PERCENTAGE_USED;
        long minimumDp = (long) (totalLoanAmount * requiredDpPercentage);

        if (downPayment < minimumDp) {
            throw new IllegalArgumentException(String.format(
                    "Jumlah DP untuk kendaraan %s minimal %.0f%% dari total pinjaman (Rp %,d).",
                    condition.toString().toLowerCase(),
                    requiredDpPercentage * 100,
                    minimumDp
            ));
        }
    }
    
    private <T> T retryOnFailure(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (Exception e) {
                view.displayError("Input tidak valid: " + e.getMessage());
                view.showMessage("Silakan coba lagi.\n");
            }
        }
    }
}
