package com.testbca.view;

import com.testbca.model.CalculationResult;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);

    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void displayResults(List<CalculationResult> results) {
        if (results.isEmpty()) {
            showMessage("Tidak ada hasil untuk ditampilkan.");
            return;
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        showMessage("\n--- Hasil Kalkulasi Cicilan ---");
        for (CalculationResult result : results) {
            String formattedInstallment = currencyFormatter.format(result.monthlyInstallment());
            String output = String.format("tahun %d : %s/bln , Suku Bunga : %.1f%%",
                    result.year(),
                    formattedInstallment,
                    result.interestRate()
            );
            showMessage(output);
        }
        showMessage("-----------------------------\n");
    }

    public void displayError(String errorMessage) {
        System.err.println("ERROR: " + errorMessage);
    }

    public void showCommands() {
        showMessage("\n--- Perintah yang Tersedia ---");
        showMessage("calculate - Memulai kalkulasi baru secara interaktif.");
        showMessage("load      - Memuat data dari web service dan menampilkan hasil.");
        showMessage("show      - Menampilkan daftar perintah ini.");
        showMessage("exit      - Keluar dari aplikasi.");
        showMessage("------------------------------\n");
    }
}
