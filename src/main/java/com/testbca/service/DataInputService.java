package com.testbca.service;

import com.testbca.model.LoanData;

import java.io.IOException;

public interface DataInputService {
    LoanData readFromFile(String filePath) throws IOException;
    LoanData getLoanDataFromUser();

}
