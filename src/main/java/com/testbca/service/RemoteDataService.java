package com.testbca.service;

import com.testbca.model.LoanData;
import java.io.IOException;


public interface RemoteDataService {
    LoanData loadFromWebService() throws IOException, InterruptedException;
}
