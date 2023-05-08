package com.copytrading.bot.controller;

import com.copytrading.bot.model.Bybit;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//TODO: This code is an approximate java code for the python code in view.py. without domain knowledge its hard to perfectly
// match everything. hence you might need to make some minor changes and adjustments to the code

@RestController
public class TutorialController {

    @GetMapping("/tutorial")
    public ResponseEntity<?> tutorialList(@RequestParam(name = "operation_type", required = false) String operationType,
                                          @RequestParam(name = "api_key", required = false) String apiKey,
                                          @RequestParam(name = "api_secret", required = false) String apiSecret,
                                          @RequestParam(name = "coin", required = false) String coin,
                                          @RequestParam(name = "symbol", required = false) String symbol,
                                          @RequestParam(name = "leverage", required = false) Integer leverage,
                                          @RequestParam(name = "amount_perc", required = false) String amountPerc,
                                          @RequestParam(name = "price", required = false) String price,
                                          @RequestParam(name = "take_profit", required = false) String takeProfit,
                                          @RequestParam(name = "stop_loss", required = false) String stopLoss)
            throws IOException {

        Bybit bybit = new Bybit(apiKey, apiSecret);

        if (operationType == null || operationType.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "operation_type is missing or empty");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        switch (operationType) {
            case "balance":
                if (coin == null || coin.isEmpty()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "coin is required for balance operation");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                double availableBalance = bybit.availableBalance(apiKey, apiSecret, coin);
                Map<String, String> response = new HashMap<>();
                response.put("balance", String.valueOf(availableBalance));
                return new ResponseEntity<>(response, HttpStatus.OK);

            case "long":
                if (symbol == null || symbol.isEmpty() || leverage == null || amountPerc == null || amountPerc.isEmpty() || price == null || price.isEmpty() || takeProfit == null || takeProfit.isEmpty() || stopLoss == null || stopLoss.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "symbol, leverage, amount_perc, price, take_profit, and stop_loss are required for long operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                double availableBalanceForLong = bybit.availableBalance(apiKey, apiSecret, "USDT");
                double leverageFloat = Double.parseDouble(String.valueOf(leverage));
                double amountPercFloat = Double.parseDouble(amountPerc);
                double priceFloat = Double.parseDouble(price);
                double amount = (availableBalanceForLong * (amountPercFloat / 100) * leverageFloat * (1 - (0.0016 * 2))) / priceFloat;
                amount = Math.round(amount * 1000000d) / 1000000d;
                String mode = String.valueOf(bybit.setOneWay(apiKey, apiSecret, symbol));
                String leverageResult = String.valueOf(bybit.setLeverage(apiKey, apiSecret, symbol, leverage));
                JSONObject longResult = bybit.longOrder(apiKey, apiSecret, symbol, String.valueOf(amount), price, takeProfit, stopLoss);


                Map<String, String> longResponse = new HashMap<>();
                longResponse.put("result", String.valueOf(longResult));
                return new ResponseEntity<>(longResponse, HttpStatus.OK);

            case "short":
                if (symbol == null || symbol.isEmpty() || leverage == null || amountPerc == null || amountPerc.isEmpty() || price == null || price.isEmpty() || takeProfit == null || takeProfit.isEmpty() || stopLoss == null || stopLoss.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "symbol, leverage, amount_perc, price, take_profit, and stop_loss are required for short operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                double availableBalanceShort = bybit.availableBalance(apiKey, apiSecret, "USDT");
                double leverageFloatShort = Double.parseDouble(String.valueOf(leverage));
                double amountPercFloatShort = Double.parseDouble(amountPerc);
                double priceFloatShort = Double.parseDouble(price);
                double amountShort = (availableBalanceShort * (amountPercFloatShort / 100) * leverageFloatShort * (1 - (0.0016 * 2))) / priceFloatShort;
                amountShort = Math.round(amountShort * 1000000d) / 1000000d;
                String modeShort = String.valueOf(bybit.setOneWay(apiKey, apiSecret, symbol));
                String leverageResultShort = String.valueOf(bybit.setLeverage(apiKey, apiSecret, symbol, leverage));
                JSONObject shortResult = bybit.shortOrder(apiKey, apiSecret, symbol, String.valueOf(amountShort), price, takeProfit, stopLoss);

            case "updateTPOrder.short":
                if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty() || symbol == null || symbol.isEmpty() || takeProfit == null || takeProfit.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "api_key, api_secret, symbol, and take_profit are required for updateTPOrder.short operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                JSONObject updateTPOrderShortResult = bybit.updateTPOrderShort(apiKey, apiSecret, symbol, takeProfit);

                Map<String, String> updateTPOrderShortResponse = new HashMap<>();
                updateTPOrderShortResponse.put("result", String.valueOf(updateTPOrderShortResult));
                return new ResponseEntity<>(updateTPOrderShortResponse, HttpStatus.OK);

            case "updateTPOrder.long":
                if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty() || symbol == null || symbol.isEmpty() || takeProfit == null || takeProfit.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "api_key, api_secret, symbol, and take_profit are required for updateTPOrder.long operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                JSONObject updateTPOrderLongResult = bybit.updateTPOrderLong(apiKey, apiSecret, symbol, takeProfit);

                Map<String, String> updateTPOrderLongResponse = new HashMap<>();
                updateTPOrderLongResponse.put("result", String.valueOf(updateTPOrderLongResult));
                return new ResponseEntity<>(updateTPOrderLongResponse, HttpStatus.OK);

            case "updateSLOrder.premium":
                if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty() || symbol == null || symbol.isEmpty() || stopLoss == null || stopLoss.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "api_key, api_secret, symbol, and stop_loss are required for updateSLOrder.premium operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                JSONObject updateSLOrderPremiumResult = bybit.updateSLOrder(apiKey, apiSecret, symbol, stopLoss);

                Map<String, String> updateSLOrderPremiumResponse = new HashMap<>();
                updateSLOrderPremiumResponse.put("result", String.valueOf(updateSLOrderPremiumResult));
                return new ResponseEntity<>(updateSLOrderPremiumResponse, HttpStatus.OK);

            case "updateSLOrder.basic":
                if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty() || symbol == null || symbol.isEmpty() || stopLoss == null || stopLoss.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "api_key, api_secret, symbol, and stop_loss are required for updateSLOrder.basic operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                JSONObject updateSLOrderBasicResult = bybit.updateSLOrder(apiKey, apiSecret, symbol, stopLoss);

                Map<String, String> updateSLOrderBasicResponse = new HashMap<>();
                updateSLOrderBasicResponse.put("result", String.valueOf(updateSLOrderBasicResult));
                return new ResponseEntity<>(updateSLOrderBasicResponse, HttpStatus.OK);

            case "cancelAllOrders":
                if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty() || symbol == null || symbol.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "api_key, api_secret, and symbol are required for cancelAllOrders operation");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }

                JSONObject cancelAllOrdersResult = bybit.cancelAllOrders(apiKey, apiSecret, symbol);

                Map<String, String> cancelAllOrdersResponse = new HashMap<>();
                cancelAllOrdersResponse.put("result", String.valueOf(cancelAllOrdersResult));
                return new ResponseEntity<>(cancelAllOrdersResponse, HttpStatus.OK);


            default:
                return new ResponseEntity<>("Invalid operation type", HttpStatus.BAD_REQUEST);
        }
    }
}