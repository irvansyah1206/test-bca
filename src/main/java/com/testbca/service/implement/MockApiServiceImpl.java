package com.testbca.service.implement;

import com.testbca.model.LoanData;
import com.testbca.model.VehicleCondition;
import com.testbca.model.VehicleType;
import com.testbca.service.RemoteDataService;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MockApiServiceImpl implements RemoteDataService {
    // URL diperbarui ke endpoint Mocky yang aktif
    private static final String API_URL = "https://www.mocky.io/v2/5d11a58d310000b23508cd62";
    private final HttpClient httpClient;

    public MockApiServiceImpl() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public LoanData loadFromWebService() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch data from API. Status code: " + response.statusCode());
        }

        return parseJsonToLoanData(response.body());
    }

    private LoanData parseJsonToLoanData(String jsonBody) {
        JSONObject json = new JSONObject(jsonBody);
        return new LoanData(
                VehicleType.valueOf(json.getString("vehicleType").toUpperCase()),
                VehicleCondition.valueOf(json.getString("vehicleCondition").toUpperCase()),
                json.getInt("vehicleYear"),
                json.getLong("totalLoanAmount"),
                json.getInt("loanTenure"),
                json.getLong("downPayment")
        );
    }
}
