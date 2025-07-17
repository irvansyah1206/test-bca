package com.testbca.model;

/**
 * REFACTOR: Nama field diubah untuk kejelasan agar lebih mewakili data yang disimpan.
 * 'totalYearlyObligation' sekarang menyimpan total pokok + bunga untuk tahun tersebut.
 * 'monthlyInstallment' sekarang dengan benar menyimpan pembayaran bulanan yang dihitung.
 */
public record CalculationResult(
        int year,
        double interestRate,
        double totalYearlyObligation,
        double monthlyInstallment
) {
}
