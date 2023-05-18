package com.copytrading.bot.controller;

import com.copytrading.bot.model.Bybit;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TutorialControllerTest {

    @Mock
    private Bybit bybit;

    @InjectMocks
    private TutorialController tutorialController;

    private String apiKey;
    private String apiSecret;
    private String coin;
    private String symbol;
    private Integer leverage;
    private String amountPerc;
    private String price;
    private String takeProfit;
    private String stopLoss;

    @BeforeEach
    public void setUp() throws IOException {
        apiKey = "api_key_example";
        apiSecret = "api_secret_example";
        coin = "USDT";
        symbol = "BTCUSDT";
        leverage = 10;
        amountPerc = "0.5";
        price = "10000";
        takeProfit = "11000";
        stopLoss = "9000";
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testUpdateSLOrderBasic() throws IOException, JSONException {
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), new JSONObject().put("result", "success").toString());
        when(bybit.updateSLOrder( symbol, stopLoss)).thenReturn(responseBody);

        ResponseEntity<?> response = tutorialController.tutorialList("updateSLOrder.basic", apiKey, apiSecret, coin, symbol, leverage, amountPerc, price, takeProfit, stopLoss);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Add more assertions for the response content
        JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.getBody()).toString());
        assertNotNull(jsonResponse.getString("result"));
        Mockito.lenient().when(bybit.updateSLOrder(symbol, stopLoss)).thenReturn(responseBody);
        // Close the response body after reading its content
        responseBody.close();
    }

}