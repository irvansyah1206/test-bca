package com.testbca;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import com.testbca.service.*;
import com.testbca.service.implement.ConsoleInputService;
import com.testbca.service.implement.LoanCalculatorServiceImpl;
import com.testbca.service.implement.MockApiServiceImpl;
import com.testbca.view.ConsoleView;

import java.util.List;

public class TestBcaApplication {

    private final ConsoleView view;
    private final DataInputService inputService;
    private final LoanCalculatorService calculatorService;
    private final RemoteDataService remoteDataService;

    public TestBcaApplication() {
        this.view = new ConsoleView();
        // Di sini kita menghubungkan interface dengan implementasi konkretnya.
        // Dalam aplikasi besar, ini biasanya ditangani oleh framework Dependency Injection (misalnya Spring).
        this.inputService = new ConsoleInputService(view);
        this.calculatorService = new LoanCalculatorServiceImpl();
        this.remoteDataService = new MockApiServiceImpl();
    }

    public static void main(String[] args) {
        TestBcaApplication app = new TestBcaApplication();
        try {
            if (args.length > 0) {
                app.runWithFile(args[0]);
            } else {
                app.runInteractive();
            }
        } catch (Exception e) {
            app.view.displayError("Terjadi kesalahan yang tidak terduga: " + e.getMessage());
        }
    }

    public void runInteractive() {
        view.showMessage("Selamat Datang di Aplikasi Credit Simulator!");
        view.showCommands();
        boolean running = true;
        while (running) {
            String command = view.prompt("Masukkan perintah (calculate, load, show, exit): ").toLowerCase().trim();
            switch (command) {
                case "calculate" -> runCalculation();
                case "load" -> loadAndCalculate();
                case "show" -> view.showCommands();
                case "exit" -> running = false;
                default -> view.displayError("Perintah tidak dikenal. Ketik 'show' untuk melihat daftar perintah.");
            }
        }
        view.showMessage("Terima kasih telah menggunakan aplikasi.");
    }

    private void runWithFile(String filePath) {
        try {
            LoanData data = inputService.readFromFile(filePath);
            processAndDisplayResults(data);
        } catch (Exception e) {
            view.displayError("Gagal memproses file: " + e.getMessage());
        }
    }

    private void runCalculation() {
        try {
            LoanData data = inputService.getLoanDataFromUser();
            processAndDisplayResults(data);
        } catch (Exception e) {
            view.displayError("Terjadi kesalahan saat kalkulasi: " + e.getMessage());
        }
    }

    private void loadAndCalculate() {
        try {
            view.showMessage("Memuat data dari web service...");
            LoanData data = remoteDataService.loadFromWebService();
            view.showMessage("Data berhasil dimuat. Melakukan kalkulasi...");
            processAndDisplayResults(data);
        } catch (Exception e) {
            view.displayError("Gagal memuat atau memproses data dari API: " + e.getMessage());
        }
    }
    
    private void processAndDisplayResults(LoanData data) {
        try {
            List<CalculationResult> results = calculatorService.calculateInstallments(data);
            view.displayResults(results);
        } catch (Exception e) {
            view.displayError("Terjadi kesalahan pada saat pemrosesan: " + e.getMessage());
        }
    }
}
