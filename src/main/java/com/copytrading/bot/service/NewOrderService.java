package com.copytrading.bot.service;

import com.copytrading.bot.logging.LoggingService;
import com.copytrading.bot.model.Constants;
import com.copytrading.bot.model.Order;
import com.copytrading.bot.model.User;
import com.copytrading.bot.model.UserOrders;
import com.copytrading.bot.repository.OrderRepository;
import com.copytrading.bot.repository.UserOrdersRepository;
import com.copytrading.bot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NewOrderService {

    private static final String TARGET_BASE_URL = "http://localhost:8000";
    //private static final String TARGET_BASE_URL = "https://signalmanager-articles-py-production.up.railway.app";
    private final OrderRepository orderRepository;

    private final UserOrdersRepository userOrdersRepository;
    private final LoggingService logService;

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    private final BotMessageSender botMessageSender;

    public NewOrderService(OrderRepository orderRepository, UserOrdersRepository userOrdersRepository, LoggingService logService, RestTemplate restTemplate, UserRepository userRepository, BotMessageSender botMessageSender) {
        this.orderRepository = orderRepository;
        this.userOrdersRepository = userOrdersRepository;
        this.logService = logService;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.botMessageSender = botMessageSender;
    }

    public List<Order> listAllOrder() {
        return orderRepository.findAll();
    }


    public void saveOrder(Order orderDto) {
        //Save all orderInformation in order to make an order history where all the spec are stored
        //default active order
        orderDto.setActive(true);
        var order = orderRepository.save(orderDto);

        botMessageSender.sendMessage(orderDto, false);

        userRepository.findActiveBotUsers().stream()
                .parallel()
                .forEach(user -> {
                    var userOrder = saveNotFinalizedUserOrder(order.getId(), user.getId()); //save Oder-User pair info
                    var takeProfitChoose = user.isBase() ? user.getTakeprofit() : order.getTakeprofit(); //choose takeProfit based on UserType
                    var amountPercentageChoose = user.isBase() ? user.getAmount_perc() : order.getAmount_perc(); //choose takeProfit based on UserType
                    var calculatedTakeProfit = calculateTakeProfit(order.getOrder_type(), order.getLimitt(), order.getOscillation(), takeProfitChoose);

                    try {
                        var response = performOrder(order, user, calculatedTakeProfit, amountPercentageChoose);
                        manageOrderResponse(response, userOrder, user);
                    } catch (Exception e) {
                        logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                    }
                });

        botMessageSender.sendMessage(orderDto, true);
    }

    public void manageOrdersToRetry(String orderId) {
        List<UserOrders> userOrdersToRetry = userOrdersRepository.findUserOrdersToRetry(orderId);
        if (userOrdersToRetry.isEmpty()) {
            return;
        }

        userOrdersToRetry.parallelStream()
                .forEach(userOrder -> {
                    Order order = orderRepository.findById(userOrder.getOrder_id()).get();
                    if (order.isActive()) {
                        try {
                            manageRetriesOrder(userOrder);
                        } catch (Exception e) {
                            logService.log("User con id" + userOrder.getUser_id() + "non ha ricevuto il segnale");
                        }
                    }
                    //check if still something to process
                });
    }


    public void checkApiAccounts() {
        List<User> userBots = userRepository.findActiveBotUsers();
        if (userBots.isEmpty()) {
            return;
        }
        for (User user : userBots) {
            try {
                restTemplate.getForEntity(TARGET_BASE_URL + "/api/tutorials?operation_type=long&api_key="
                        + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=TWTUSDT&amount_perc=" + Math.round(5) + "&leverage="
                        + Math.round(10) + "&price=0.10&take_profit=5&stop_loss=0.05", Map.class);
            } catch (Exception e) {
                logService.log("**User con id " + user.getId() + " non ha ricevuto il segnale**");
            } finally {

            }
        }
    }


    public String updateOrder(Order orderDto) {
        final var updateOrderType = orderDto.getOrder_type();
        var activeBotUsers = userRepository.findActiveBotUsers();
        if (updateOrderType.contains("TP")) {
            activeBotUsers.parallelStream()
                    .forEach(user -> {
                        if (user.isPremium()) {
                            logService.log("Executing " + updateOrderType + "on user: " + user.getEmail() + "with id: " + user.getId());
                            String orderType = updateOrderType.split("\\.")[1];
                            var takeProfit = calculateTakeProfit(orderType, orderDto.getLimitt(), orderDto.getOscillation(), orderDto.getTakeprofit());

                    try {
                        var requestOperation = restTemplate.getForEntity(TARGET_BASE_URL + "/api/tutorials?operation_type=" + updateOrderType + "&api_key="
                                + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + orderDto.getTicker().toUpperCase()
                                + "&take_profit=" + takeProfit, Map.class);


                                var body = (Map<String, Object>) requestOperation.getBody();

                                if (requestOperation.getStatusCode() == HttpStatus.OK) {
                                    logService.log("Successful update TP for event " + updateOrderType + "on user: " + user.getId());
                                    logService.log(String.valueOf(body.get("retMsg")));
                                } else {
                                    logService.log("Failing update TP for event " + updateOrderType + "on user: " + user.getId());
                                    logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
                                }
                            } catch (Exception e) {
                                logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
                            }
                        }
                    });
        } else if (updateOrderType.contains("SL")) {
            if (updateOrderType.contains("basic")) {
                activeBotUsers.parallelStream()
                        .forEach(user -> {
                            if (user.isBase()) {
                                updateSLPosition(updateOrderType, user, orderDto);
                            }
                        });
            } else { //premium
                activeBotUsers.parallelStream()
                        .forEach(user -> {
                            if (user.isPremium()) {
                                updateSLPosition(updateOrderType, user, orderDto);
                            }
                        });
            }

        } else {
            logService.log("No valid Update Order Type -> " + updateOrderType);
        }
        return "Signal Updated";
    }


    @Transactional
    public void cancelOrderAndPosition(Order orderDto) {
        logService.log("canceling orders");
        var updateOrderType = orderDto.getOrder_type();
        userRepository.findActiveBotUsers()
                .parallelStream()
                .forEach(user -> {
                    try {
                        var requestOperation = restTemplate.getForEntity(TARGET_BASE_URL + "/api/tutorials?operation_type=" + updateOrderType + "&api_key="
                                + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + orderDto.getTicker().toUpperCase(), Map.class);

                        var body = (Map<String, Object>) requestOperation.getBody();

                        if (requestOperation.getStatusCode() == HttpStatus.OK) {
                            orderRepository.findActiveOrdersByPairBetween(orderDto.getTicker().toUpperCase(), LocalDateTime.now().minusDays(7), LocalDateTime.now())
                                    .stream()
                                    .peek(o -> {
                                        o.setActive(false);
                                        logService.log("Deactivate orderId: " + o.getId());
                                    })
                                    .forEach(orderRepository::save);

                            logService.log("Successful cancel for event " + updateOrderType + "on user: " + user.getId());
                            logService.log(String.valueOf(body.get("retMsg")));
                        } else {
                            logService.log("Failing cancel for event " + updateOrderType + "on user: " + user.getId());
                            logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
                        }
                    } catch (Exception e) {
                        logService.log(e.getMessage());
                        logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
                    }
                });
    }

    private void updateSLPosition(String updateOrderType, User user, Order orderDto) {
        try {
            var requestOperation = restTemplate.getForEntity(TARGET_BASE_URL + "/api/tutorials?operation_type=" + updateOrderType + "&api_key="
                    + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + orderDto.getTicker().toUpperCase()
                    + "&stop_loss=" + orderDto.getStoploss(), Map.class);

            var body = (Map<String, Object>) requestOperation.getBody();

            if (requestOperation.getStatusCode() == HttpStatus.OK) {
                logService.log("Successful update for event " + updateOrderType + "on user: " + user.getId());
                logService.log(String.valueOf(body.get("retMsg")));
            } else {
                logService.log("Failing update for event " + updateOrderType + "on user: " + user.getId());
                logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
            logService.log("User con id" + user.getId() + "non ha ricevuto " + updateOrderType);
        }

    }

    private Float calculateTakeProfit(String orderType, Float limit, Float oscillation, Float takeProfitChoose) {
        Float takeProfit;
        if (Constants.ORDER_TYPE_LONG.equals(orderType)) {
            takeProfit = limit + (limit * (oscillation * takeProfitChoose) / 100);
        } else { //SHORT
            takeProfit = limit - (limit * (oscillation * takeProfitChoose) / 100);
        }

        return takeProfit;
    }


    private ResponseEntity<Map> performOrder(Order order, User user, Float takeProfit, Float amountPercentage) {
        return restTemplate.getForEntity(TARGET_BASE_URL + "/api/tutorials?operation_type=" + order.getOrder_type() + "&api_key="
                + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + order.getTicker()
                + "&amount_perc=" + Math.round(amountPercentage) + "&leverage="
                + Math.round(order.getLeverage()) + "&price=" + order.getLimitt() + "&take_profit=" + takeProfit + "&stop_loss="
                + order.getStoploss(), Map.class);
    }

    private void manageOrderResponse(ResponseEntity<Map> response, UserOrders userOrder, User user) {
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            var responseBody = (Map<String, Object>) response.getBody();
            var retMessage = String.valueOf(responseBody.get("retMsg"));
            var result = (Map<String, String>) responseBody.get("result");
            var exchangeOrderId = result.get("orderId");
            var retCode = String.valueOf(responseBody.get("retCode"));

            if (exchangeOrderId != null) {
                finalizeUserOrder(userOrder, exchangeOrderId);
            } else if ("10001".equals(retCode)) {
                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale - retCode 10001");
                logService.log(retMessage);
                userOrder.setRetries(3);
                if (retMessage.contains("minimum")) {
                    updateUserOrderStatus(userOrder, "LOW_BUDGET");
                    userOrder.setRetries(0);
                    botMessageSender.sendErrorMessageToAlertChat(user,"Low Budget!");
                } else if (retMessage.contains("maximum")) {
                    updateUserOrderStatus(userOrder, "BUDGET_OVERFLOW");
                    //manage retries
                    userOrder.setRetries(100);
                } else if (retMessage.contains("position idx not match position mode")) {
                    userOrder.setRetries(3);
                    updateUserOrderStatus(userOrder, "POSITION_IDX");
                    botMessageSender.sendErrorMessageToAlertChat(user,"Position IDX Error!");
                }

            }
            else if ("10005".equals(retCode)) {
                botMessageSender.sendErrorMessageToAlertChat(user,"Invalid API configuration!");
            }
            else {
                logService.log("UNMANAGED ERROR");
                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                userOrder.setOrderStatus("KO_UNMANAGED" + retCode);
                userOrder.setRetries(0);
                userOrdersRepository.save(userOrder);
                botMessageSender.sendErrorMessageToAlertChat(user,"Unmanaged Error!");
            }
        } else {
            logService.log("UNMANAGED ERROR");
            logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
            userOrder.setOrderStatus("KO_RESPONSE_NOT_200");
            userOrder.setRetries(0);
            userOrdersRepository.save(userOrder);
            botMessageSender.sendErrorMessageToAlertChat(user,"Unmanaged Error - not 200/OK from Python Service!");
        }
    }


    private void manageRetriesOrder(UserOrders userOrder) {
        var user = userRepository.findById(userOrder.getUser_id()).get();
        var order = orderRepository.findById(userOrder.getOrder_id()).get();
        //recalculate
        var takeProfitChoose = user.isBase() ? user.getTakeprofit() : order.getTakeprofit(); //choose takeProfit based on UserType
        var amountPercentageChoose = user.isBase() ? user.getAmount_perc() : order.getAmount_perc(); //choose takeProfit based on UserType
        var calculatedTakeProfit = calculateTakeProfit(order.getOrder_type(), order.getLimitt(), order.getOscillation(), takeProfitChoose);

        if ("POSITION_IDX".equals(userOrder.getOrderStatus())) { //retries until exhaust the number of retries
            var exchangeOrderId = performPositionIdxRetries(order, user, calculatedTakeProfit, amountPercentageChoose, userOrder.getRetries());
            userOrder.setOrderStatus(exchangeOrderId != null ? "OK_AFTER_POSITION_IDX_RETRIES" : "KO_MAX_RETRIES_EXCEEDED_IDX");
            userOrder.setBybit_order_id(exchangeOrderId);
            userOrder.setRetries(0);
            logService.log(String.format("saving userOrder with id %s with status %s and exchangeOrderId %s", userOrder.getId(), userOrder.getOrderStatus(), userOrder.getBybit_order_id()));
            userOrdersRepository.save(userOrder);
        } else if ("BUDGET_OVERFLOW".equals(userOrder.getOrderStatus())) { //reduce the amount until it works
            var exchangeOrderId = performBudgetOverflowRetries(order, user, calculatedTakeProfit, amountPercentageChoose, userOrder.getRetries());
            if(exchangeOrderId.contains("KO")){
                botMessageSender.sendErrorMessageToAlertChat(user,"Too much budget - fail after " + userOrder.getRetries() + " retries!");
            }

            userOrder.setOrderStatus(exchangeOrderId != null ? "OK_AFTER_BUDGET_OVERFLOW_RETRIES" : "KO_MAX_RETRIES_EXCEEDED_BUDGET_OVERFLOW");
            userOrder.setBybit_order_id(exchangeOrderId);
            userOrder.setRetries(0);
            logService.log(String.format("saving userOrder with id %s with status %s and exchangeOrderId %s", userOrder.getId(), userOrder.getOrderStatus(), userOrder.getBybit_order_id()));
            userOrdersRepository.save(userOrder);
        } else if (userOrder.getRetries() == null && userOrder.getOrderStatus() == null) { //generate order for first time
            try {
                var response = performOrder(order, user, calculatedTakeProfit, amountPercentageChoose);
                manageOrderResponse(response, userOrder, user);
            } catch (Exception e) {
                logService.log(e.getMessage());
                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
            }
        } else { // unmanaged error , set 0 retries and status KO
            logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
            userOrder.setOrderStatus("KO_UNMANAGED");
            userOrder.setRetries(0);
            userOrdersRepository.save(userOrder);
            botMessageSender.sendErrorMessageToAlertChat(user,"Unmanaged Error!");
        }
    }

    private String performPositionIdxRetries(Order order, User user, Float takeProfit, Float amountPercentage, Integer retries) {
        String exchangeOrderId = null;
        for (int i = retries; i > 0; i--) {
            logService.log("performing position Idx retry with retries n: " + retries);
            var response = performOrder(order, user, takeProfit, amountPercentage);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                var responseBody = (Map<String, Object>) response.getBody();
                if (Integer.valueOf(0).equals(responseBody.get("retCode"))) {
                    var result = (Map<String, String>) responseBody.get("result");
                    exchangeOrderId = result.get("orderId");
                    break;
                }
            }
        }
        return exchangeOrderId;
    }

    private String performBudgetOverflowRetries(Order order, User user, Float takeProfit, Float amountPercentage, Integer retries) {
        String exchangeOrderId = null;
        for (int i = retries; i > 0; i--) {
            amountPercentage = amountPercentage - 0.5F;
            logService.log("performing budgetOverflow retry with retries n: " + i + " and amount percentage: " + amountPercentage);
            var response = performOrder(order, user, takeProfit, amountPercentage);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                var responseBody = (Map<String, Object>) response.getBody();
                if (Integer.valueOf(0).equals(responseBody.get("retCode"))) {
                    logService.log("Success after retrying for budget overflow");
                    var result = (Map<String, String>) responseBody.get("result");
                    exchangeOrderId = result.get("orderId");
                    break;
                }
            }
        }
        return exchangeOrderId;
    }

    private UserOrders saveNotFinalizedUserOrder(Integer orderId, String userId) {
        var userOrder = new UserOrders();
        userOrder.setOrder_id(orderId);
        userOrder.setUser_id(userId);
        return userOrdersRepository.save(userOrder);
    }

    private void finalizeUserOrder(UserOrders userOrder, String exchangeOrderId) {
        userOrder.setBybit_order_id(exchangeOrderId);
        userOrder.setOrderStatus("OK");
        userOrder.setRetries(0);
        userOrdersRepository.save(userOrder);
    }

    private void updateUserOrderStatus(UserOrders userOrder, String status) {
        userOrder.setOrderStatus(status);
        userOrdersRepository.save(userOrder);
    }


}
