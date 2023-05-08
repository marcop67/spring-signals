package com.copytrading.bot.model;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

//TODO: This code is an approximate java code for the python code in view.py. without domain knowledge its hard to perfectlty
// match everything. hence you might need to make some minor changes and adjustments to the code
// make sure the add the new dependencies in pom.xml with the solution I have provided the modified pom.xml code check it

public class Bybit {
    private OkHttpClient httpClient;
    private String recvWindow;
    private String url;

    private String apiKey;

    private String apiSecret;

    public Bybit(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.recvWindow = "10000";
        this.url = "https://api.bybit.com";

        this.httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(30, TimeUnit.SECONDS);
    }

    public Response HTTP_Request(String apiKey, String apiSecret, String endPoint, String method, String payload, String info) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        System.out.println(timeStamp);
        System.out.println(recvWindow);
        String signature = genSignature(apiKey, apiSecret, payload);

        Request.Builder requestBuilder = new Request.Builder()
                .header("X-BAPI-API-KEY", apiKey)
                .header("X-BAPI-SIGN", signature)
                .header("X-BAPI-SIGN-TYPE", "2")
                .header("X-BAPI-TIMESTAMP", timeStamp)
                .header("X-BAPI-RECV-WINDOW", recvWindow)
                .header("Content-Type", "application/json");

        if (method.equalsIgnoreCase("POST")) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload);
            requestBuilder.url(url + endPoint).post(body);
        } else {
            requestBuilder.url(url + endPoint + "?" + payload).get();
        }

        Request request = requestBuilder.build();
        try {
            long startTime = System.currentTimeMillis();
            Response response = httpClient.newCall(request).execute();
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(response.body().string());
            System.out.println(info + " Elapsed Time: " + elapsedTime + " ms");
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String genSignature(String apiKey, String apiSecret, String payload) {
        String paramStr = System.currentTimeMillis() + apiKey + recvWindow + payload;
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            byte[] bytes = hmacSha256.doFinal(paramStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    public ResponseBody updateTPOrderLong(String apiKey, String apiSecret, String symbol, String takeProfit) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"takeProfit\":\"%s\",\"positionIdx\": \"0\"}", symbol, takeProfit);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }


    public ResponseBody updateTPOrderShort(String apiKey, String apiSecret, String symbol, String takeProfit) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"takeProfit\":\"%s\",\"positionIdx\": \"0\"}", symbol, takeProfit);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }

    public ResponseBody updateSLOrder(String apiKey, String apiSecret, String symbol, String stopLoss) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"stopLoss\":\"%s\",\"positionIdx\": \"0\"}", symbol, stopLoss);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }

    public ResponseBody cancelOrder(String apiKey, String apiSecret, String symbol, String orderId) throws IOException {
        String endpoint = "/contract/v3/private/order/cancel";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"orderId\": \"%s\"}", symbol, orderId);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }


    public ResponseBody cancelAllOrders(String apiKey, String apiSecret, String symbol) throws IOException {
        String endpoint = "/contract/v3/private/order/cancel-all";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\"}", symbol);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }


    public ResponseBody longOrder(String apiKey, String apiSecret, String symbol, String qty, String price, String take_profit, String stop_loss) throws IOException {
        String endpoint = "/contract/v3/private/order/create";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\": \"%s\",\"side\": \"Buy\",\"positionIdx\": \"0\",\"orderType\": \"Limit\",\"qty\": \"%s\",\"price\": \"%s\",\"is_isolated\": false,\"tpTriggerBy\": \"MarkPrice\",\"slTriggerBy\": \"MarkPrice\",\"triggerBy\": \"MarkPrice\",\"triggerDirection\": 2,\"timeInForce\": \"GoodTillCancel\",\"orderLinkId\": \"%s\",\"takeProfit\": \"%s\",\"stopLoss\": \"%s\",\"reduce_only\": false,\"closeOnTrigger\": false}", symbol, qty, price, orderLinkId, take_profit, stop_loss);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }


    public ResponseBody shortOrder(String apiKey, String apiSecret, String symbol, String qty, String price, String take_profit, String stop_loss) throws IOException {
        String endpoint = "/contract/v3/private/order/create";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\": \"%s\",\"side\": \"Sell\",\"is_isolated\": false,\"positionIdx\": \"0\",\"orderType\": \"Limit\",\"qty\": \"%s\",\"price\": \"%s\",\"tpTriggerBy\": \"MarkPrice\",\"slTriggerBy\": \"MarkPrice\",\"triggerBy\": \"MarkPrice\",\"triggerDirection\": 1,\"timeInForce\": \"GoodTillCancel\",\"orderLinkId\": \"%s\",\"takeProfit\": \"%s\",\"stopLoss\": \"%s\",\"reduce_only\": false,\"closeOnTrigger\": false}", symbol, qty, price, orderLinkId, take_profit, stop_loss);
        System.out.println(params);
        return HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body();
    }


    public void buy(String apiKey, String apiSecret, String symbol, String qty, String price) throws IOException {
        String endpoint = "/spot/v3/private/order";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\":\"%s\",\"orderType\":\"Limit\",\"side\":\"Buy\",\"orderLinkId\":\"%s\",\"orderQty\":\"%s\",\"orderPrice\":\"%s\",\"timeInForce\":\"GTC\"}", symbol, orderLinkId, qty, price);
        System.out.println(HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body().string());
    }


    public void sell(String apiKey, String apiSecret, String symbol, String qty, String price) throws IOException {
        String endpoint = "/spot/v3/private/order";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\":\"%s\",\"orderType\":\"Limit\",\"side\":\"Sell\",\"orderLinkId\":\"%s\",\"orderQty\":\"%s\",\"orderPrice\":\"%s\",\"timeInForce\":\"GTC\"}", symbol, orderLinkId, qty, price);
        System.out.println(HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create").body().string());
    }

    public double availableBalance(String apiKey, String apiSecret, String coin) throws IOException {
        String endpoint = "/contract/v3/private/account/wallet/balance";
        String method = "GET";
        String params = "coin=" + coin;


        Response response = HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Info");

        if (response.isSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray list = jsonResponse.getJSONObject("result").getJSONArray("list");
            return list.getJSONObject(0).getDouble("availableBalance");
        } else {
            throw new IOException("Request failed: " + response.message());
        }
    }


    public JSONObject setLeverage(String apiKey, String apiSecret, String symbol, int leverage) throws IOException {
        String endpoint = "/contract/v3/private/position/set-leverage";
        String method = "POST";
        String params = "{\"symbol\":\"" + symbol + "\",\"buyLeverage\":\"" + leverage + "\",\"sellLeverage\":\"" + leverage + "\"}";
        System.out.println(params);
        Response response = HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create");
        String responseBody = response.body().string();
        return new JSONObject(responseBody);
    }

    public JSONObject setOneWay(String apiKey, String apiSecret, String symbol) throws IOException {
        String mode = "0";
        String endpoint = "/contract/v3/private/position/switch-mode";
        String method = "POST";
        String params = "{\"symbol\":\"" + symbol + "\",\"mode\":" + mode + "}";
        System.out.println(params);
        Response response = HTTP_Request(apiKey, apiSecret, endpoint, method, params, "Create");
        String responseBody = response.body().string();
        return new JSONObject(responseBody);
    }

}
