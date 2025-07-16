package com.testbca.service;

import com.testbca.model.CalculationResult;
import com.testbca.model.LoanData;
import java.util.List;

/**
 * REFACTOR: Interface untuk layanan kalkulasi pinjaman.
 */
public interface LoanCalculatorService {
    List<CalculationResult> calculateInstallments(LoanData data);
}
