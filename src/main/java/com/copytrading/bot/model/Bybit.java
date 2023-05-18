package com.copytrading.bot.model;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: This code is an approximate java code for the python code in view.py. without domain knowledge its hard to perfectlty
// match everything. hence you might need to make some minor changes and adjustments to the code
// make sure the add the new dependencies in pom.xml with the solution I have provided the modified pom.xml code check it

public class Bybit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bybit.class);
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

    public Response HTTP_Request(String endPoint, String method, String payload, String info) {
        String timeStamp = String.valueOf(System.currentTimeMillis());

        LOGGER.info(timeStamp);
        LOGGER.info(recvWindow);
        String signature = genSignature(payload);

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
            LOGGER.info(response.body().string());
            LOGGER.info(info + " Elapsed Time: " + elapsedTime + " ms");
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(500)
                    .message("Failed to execute HTTP request: " + e.getMessage())
                    .body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), e.getMessage()))
                    .build();
        }
    }


    public String genSignature(String payload) {
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

    public ResponseBody updateTPOrderLong(String symbol, String takeProfit) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"takeProfit\":\"%s\",\"positionIdx\": \"0\"}", symbol, takeProfit);
        LOGGER.info(params);


        return HTTP_Request(endpoint, method, params, "Create").body();
    }


    public ResponseBody updateTPOrderShort(String symbol, String takeProfit) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"takeProfit\":\"%s\",\"positionIdx\": \"0\"}", symbol, takeProfit);
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create").body();
    }

    public ResponseBody updateSLOrder(String symbol, String stopLoss) throws IOException {
        String endpoint = "/contract/v3/private/position/trading-stop";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"stopLoss\":\"%s\",\"positionIdx\": \"0\"}", symbol, stopLoss);
        LOGGER.info(params);
        return HTTP_Request(endpoint, method, params, "Create").body();
    }

    public ResponseBody cancelOrder(String symbol, String orderId) throws IOException {
        String endpoint = "/contract/v3/private/order/cancel";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\",\"orderId\": \"%s\"}", symbol, orderId);
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create").body();
    }


    public ResponseBody cancelAllOrders(String symbol) throws IOException {
        String endpoint = "/contract/v3/private/order/cancel-all";
        String method = "POST";
        String params = String.format("{\"symbol\":\"%s\"}", symbol);
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create").body();
    }


    public ResponseBody longOrder(String symbol, String qty, String price, String take_profit, String stop_loss) throws IOException {
        String endpoint = "/contract/v3/private/order/create";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\": \"%s\",\"side\": \"Buy\",\"positionIdx\": \"0\",\"orderType\": \"Limit\",\"qty\": \"%s\",\"price\": \"%s\",\"is_isolated\": false,\"tpTriggerBy\": \"MarkPrice\",\"slTriggerBy\": \"MarkPrice\",\"triggerBy\": \"MarkPrice\",\"triggerDirection\": 2,\"timeInForce\": \"GoodTillCancel\",\"orderLinkId\": \"%s\",\"takeProfit\": \"%s\",\"stopLoss\": \"%s\",\"reduce_only\": false,\"closeOnTrigger\": false}", symbol, qty, price, orderLinkId, take_profit, stop_loss);
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create").body();
    }


    public ResponseBody shortOrder(String symbol, String qty, String price, String take_profit, String stop_loss) throws IOException {
        String endpoint = "/contract/v3/private/order/create";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\": \"%s\",\"side\": \"Sell\",\"is_isolated\": false,\"positionIdx\": \"0\",\"orderType\": \"Limit\",\"qty\": \"%s\",\"price\": \"%s\",\"tpTriggerBy\": \"MarkPrice\",\"slTriggerBy\": \"MarkPrice\",\"triggerBy\": \"MarkPrice\",\"triggerDirection\": 1,\"timeInForce\": \"GoodTillCancel\",\"orderLinkId\": \"%s\",\"takeProfit\": \"%s\",\"stopLoss\": \"%s\",\"reduce_only\": false,\"closeOnTrigger\": false}", symbol, qty, price, orderLinkId, take_profit, stop_loss);
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create").body();
    }


    public void buy(String symbol, String qty, String price) throws IOException {
        String endpoint = "/spot/v3/private/order";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\":\"%s\",\"orderType\":\"Limit\",\"side\":\"Buy\",\"orderLinkId\":\"%s\",\"orderQty\":\"%s\",\"orderPrice\":\"%s\",\"timeInForce\":\"GTC\"}", symbol, orderLinkId, qty, price);
        LOGGER.info(HTTP_Request(endpoint, method, params, "Create").body().string());
    }


    public void sell(String symbol, String qty, String price) throws IOException {
        String endpoint = "/spot/v3/private/order";
        String method = "POST";
        String orderLinkId = UUID.randomUUID().toString().replaceAll("-", "");
        String params = String.format("{\"symbol\":\"%s\",\"orderType\":\"Limit\",\"side\":\"Sell\",\"orderLinkId\":\"%s\",\"orderQty\":\"%s\",\"orderPrice\":\"%s\",\"timeInForce\":\"GTC\"}", symbol, orderLinkId, qty, price);
        LOGGER.info(HTTP_Request(endpoint, method, params, "Create").body().string());
    }

    public double availableBalance(String coin) throws IOException {
        String endpoint = "/contract/v3/private/account/wallet/balance";
        String method = "GET";
        String params = "coin=" + URLEncoder.encode(coin, StandardCharsets.UTF_8.toString());
        Response response = null;
        try {
            response = HTTP_Request(endpoint, method, params, "Info");
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("result")) {
                    JSONObject result = jsonResponse.getJSONObject("result");
                    if (result.has("list")) {
                        JSONArray list = result.getJSONArray("list");
                        if (!list.isEmpty()) {
                            JSONObject firstObject = list.getJSONObject(0);
                            if (firstObject.has("availableBalance")) {
                                return firstObject.getDouble("availableBalance");
                            }
                        }
                    }
                }
            } else {
                throw new IOException("Request failed: " + response.message());
            }
        } catch (JSONException e) {
            throw new IOException("Invalid JSON response", e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
        throw new IOException("Failed to get available balance");
    }




    public Response setLeverage(String symbol, int leverage) throws IOException {
        String endpoint = "/contract/v3/private/position/set-leverage";
        String method = "POST";
        String params = "{\"symbol\":\"" + symbol + "\",\"buyLeverage\":\"" + leverage + "\",\"sellLeverage\":\"" + leverage + "\"}";
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create");
    }

    public Response setOneWay(String symbol) throws IOException {
        String mode = "0";
        String endpoint = "/contract/v3/private/position/switch-mode";
        String method = "POST";
        String params = "{\"symbol\":\"" + symbol + "\",\"mode\":" + mode + "}";
        LOGGER.info(params);

        return HTTP_Request(endpoint, method, params, "Create");
    }


}
