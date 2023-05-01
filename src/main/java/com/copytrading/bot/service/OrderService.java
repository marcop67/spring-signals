package com.copytrading.bot.service;

import com.copytrading.bot.logging.LoggingService;
import com.copytrading.bot.model.Order;
import com.copytrading.bot.model.User;
import com.copytrading.bot.model.UserOrders;
import com.copytrading.bot.repository.OrderRepository;
import com.copytrading.bot.repository.UserOrdersRepository;
import com.copytrading.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserOrdersRepository userOrdersRepository;
    @Autowired
    LoggingService logService;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    public List<Order> listAllOrder() {
        return orderRepository.findAll();
    }

    public Order saveOrder(Order order) throws Exception {
        //String url = "https://signalmanager-articles-py-production.up.railway.app";
        String url = "http://localhost:8000";
        order = orderRepository.save(order);


        for (User user : userRepository.findAll()) {
            if (user.isIs_active()) {
                if (user.isBot()) {
                    System.out.println(user);
                    RestTemplate restTemplate = new RestTemplate();
                    if (order.getOrder_type().equalsIgnoreCase("long")) {
                        System.out.println("long");
                        if (user.isBase()) {
                            try {
                                float takeProfit = (order.getLimitt() * (order.getOscillation() * user.getTakeprofit()) / 100) + order.getLimitt();
                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url + "/api/tutorials?operation_type=long&api_key="
                                        + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + order.getTicker().toUpperCase()
                                        + "&amount_perc=" + Math.round(user.getAmount_perc()) + "&leverage="
                                        + Math.round(order.getLeverage()) + "&price=" + order.getLimitt() + "&take_profit=" + takeProfit + "&stop_loss="
                                        + order.getStoploss(), Map.class);


                                if (requestOperation.getStatusCode() == HttpStatus.OK) {
                                    Map<String, Object> mappa = (Map<String, Object>) requestOperation.getBody();
                                    String orderId = (String) mappa.get("orderId");


                                    UserOrders userOrders = new UserOrders();
                                    userOrders.setOrder_id(order.getId());
                                    userOrders.setUser_id(user.getId());
                                    userOrders.setBybit_order_id(orderId);

                                    userOrders = userOrdersRepository.save(userOrders);

                                } else {
                                    logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                                    //notifica con email che user_id non ha ricevuto il segnale
                                }
                            } catch (Exception e) {
                                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                            }

                        }
                        if (user.isPremium()) {
                            try {
                                float takeProfit = (order.getLimitt() * (order.getOscillation() * order.getTakeprofit()) / 100) + order.getLimitt();
                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url + "/api/tutorials?operation_type=long&api_key="
                                        + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + order.getTicker().toUpperCase()
                                        + "&amount_perc=" + Math.round(order.getAmount_perc()) + "&leverage="
                                        + Math.round(order.getLeverage()) + "&price=" + order.getLimitt() + "&take_profit=" + takeProfit + "&stop_loss="
                                        + order.getStoploss(), Map.class);


                                if (requestOperation.getStatusCode() == HttpStatus.OK) {
                                    Map<String, String> mappa = (Map<String, String>) requestOperation.getBody();
                                    String orderId = mappa.get("orderId");


                                    order = orderRepository.save(order);

                                    UserOrders userOrders = new UserOrders();
                                    userOrders.setOrder_id(order.getId());
                                    userOrders.setUser_id(user.getId());
                                    userOrders.setBybit_order_id(orderId);

                                    userOrders = userOrdersRepository.save(userOrders);

                                } else {
                                    logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                                }
                            } catch (Exception e) {
                                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                            }
                        }
                    }

                    if (order.getOrder_type().equalsIgnoreCase("short")) {
                        System.out.println("short");
                        if (user.isBase()) {
                            try {
                                float takeProfit = order.getLimitt() - (order.getLimitt() * (order.getOscillation() * user.getTakeprofit()) / 100);
                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url + "/api/tutorials?operation_type=short&api_key="
                                        + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + order.getTicker().toUpperCase()
                                        + "&amount_perc=" + Math.round(user.getAmount_perc()) + "&leverage="
                                        + Math.round(order.getLeverage()) + "&price=" + order.getLimitt() + "&take_profit=" + takeProfit + "&stop_loss="
                                        + order.getStoploss(), Map.class);


                                if (requestOperation.getStatusCode() == HttpStatus.OK) {
                                    Map<String, String> mappa = (Map<String, String>) requestOperation.getBody();
                                    String orderId = mappa.get("orderId");


                                    order = orderRepository.save(order);

                                    UserOrders userOrders = new UserOrders();
                                    userOrders.setOrder_id(order.getId());
                                    userOrders.setUser_id(user.getId());
                                    userOrders.setBybit_order_id(orderId);

                                    userOrders = userOrdersRepository.save(userOrders);

                                } else {
                                    logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                                }
                            } catch (Exception e) {
                                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                            }

                        }
                        if (user.isPremium()) {
                            try {
                                float takeProfit = order.getLimitt() - ((order.getLimitt() * (order.getOscillation() * order.getTakeprofit())) / 100);
                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url + "/api/tutorials?operation_type=short&api_key="
                                        + user.getApi_key() + "&api_secret=" + user.getApi_secret() + "&symbol=" + order.getTicker().toUpperCase()
                                        + "&amount_perc=" + Math.round(order.getAmount_perc()) + "&leverage="
                                        + Math.round(order.getLeverage()) + "&price=" + order.getLimitt() + "&take_profit=" + takeProfit + "&stop_loss="
                                        + order.getStoploss(), Map.class);


                                if (requestOperation.getStatusCode() == HttpStatus.OK) {
                                    Map<String, String> mappa = (Map<String, String>) requestOperation.getBody();
                                    String orderId = mappa.get("orderId");

                                    order = orderRepository.save(order);

                                    UserOrders userOrders = new UserOrders();
                                    userOrders.setOrder_id(order.getId());
                                    userOrders.setUser_id(user.getId());
                                    userOrders.setBybit_order_id(orderId);

                                    userOrders = userOrdersRepository.save(userOrders);

                                } else {
                                    logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                                }
                            } catch (Exception e) {
                                logService.log("User con id" + user.getId() + "non ha ricevuto il segnale");
                            }
                        }
                    }

                    //todo review logica con marco
//                    if(order.getOrder_type().equalsIgnoreCase("updateTPOrder.short")){
//                        System.out.println("updateTPOrder.short");
//                        if(user.isPremium()){
//                            try{
//                                float takeProfit =order.getLimitt() - (order.getLimitt()*(order.getOscillation()*order.getTakeprofit())/100);
//                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url+"/api/tutorials?operation_type=updateTPOrder.short&api_key="
//                                        +user.getApi_key()+"&api_secret="+user.getApi_secret()+"&symbol="+order.getTicker().toUpperCase()
//                                        +"&take_profit="+takeProfit, Map.class);
//
//
//                                if(requestOperation.getStatusCode() == HttpStatus.OK){
//                                    System.out.println();
//                                }else{
//                                    logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                }
//                            }catch(Exception e){
//                                logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                            }
//                        }
//                    }
//
//                    if(order.getOrder_type().equalsIgnoreCase("updateTPOrder.long")){
//                        System.out.println("updateTPOrder.long");
//                        if(user.isPremium()){
//                            try{
//                                float takeProfit =(order.getLimitt()*(order.getOscillation()*order.getTakeprofit())/100)+order.getLimitt();
//                                ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url+"/api/tutorials?operation_type=updateTPOrder.long&api_key="
//                                        +user.getApi_key()+"&api_secret="+user.getApi_secret()+"&symbol="+order.getTicker().toUpperCase()
//                                        +"&take_profit="+takeProfit, Map.class);
//
//
//                                if(requestOperation.getStatusCode() == HttpStatus.OK){
//                                    System.out.println();
//                                }else{
//                                    logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                }
//                            }catch(Exception e){
//                                logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                            }
//
//                        }
//                    }


                    //todo review logica con marco
//                    if(order.getOrder_type().equalsIgnoreCase("updateSLOrder.basic")){
//                        System.out.println("updateSLOrder.basic");
//                        if(user.isBase()){
//
//                            for(UserOrders userOrders : userOrdersRepository.findAll()){
//                                try{
//                                    Order orderItemThis=null;
//                                    for(Order orderItem : orderRepository.findAll()){
//                                        if(orderItem.getUser_id().equals(user.getId())){
//                                            orderItemThis=orderItem;
//                                        }
//                                    }
//
//                                    if(Objects.equals(userOrders.getUser_id(), user.getId()) && orderItemThis !=null && userOrders.getOrder_id()==orderItemThis.getId()){
//                                        ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url+"/api/tutorials?operation_type=updateSLOrder&api_key="
//                                                +user.getApi_key()+"&api_secret="+user.getApi_secret()+"&symbol="+order.getTicker().toUpperCase()
//                                                +"&stop_loss="+order.getStoploss()+"&orderId="+userOrders.getBybit_order_id(), Map.class);
//
//
//                                        if(requestOperation.getStatusCode() == HttpStatus.OK){
//                                            System.out.println();
//                                        }else{
//                                            logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                        }
//                                    }
//                                }catch(Exception e){
//                                    logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                }
//                            }
//
//                        }
//
//                    }

                    //todo review logica con marco
//                    if(order.getOrder_type().equalsIgnoreCase("updateSLOrder.premium")){
//                        System.out.println("updateSLOrder.premium");
//                        if(user.isPremium()){
//                            for(UserOrders userOrders : userOrdersRepository.findAll()){
//                                try{
//                                    Order orderItemThis=null;
//                                    for(Order orderItem : orderRepository.findAll()){
//                                        if(orderItem.getUser_id().equals(user.getId())){
//                                            orderItemThis=orderItem;
//                                        }
//                                    }
//
//                                    if(Objects.equals(userOrders.getUser_id(), user.getId()) && orderItemThis !=null && userOrders.getOrder_id()==orderItemThis.getId()){
//                                        ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url+"/api/tutorials?operation_type=updateSLOrder&api_key="
//                                                +user.getApi_key()+"&api_secret="+user.getApi_secret()+"&symbol="+order.getTicker().toUpperCase()
//                                                +"&stop_loss="+order.getStoploss()+"&orderId="+userOrders.getBybit_order_id(), Map.class);
//
//
//                                        if(requestOperation.getStatusCode() == HttpStatus.OK){
//                                            System.out.println();
//                                        }else{
//                                            logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                        }
//                                    }
//                                }catch(Exception e){
//                                    logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                                }
//                            }
//
//                        }
//                    }


                    //TODO LOGICA DA RIVEDERE
//                    if(order.getOrder_type().equalsIgnoreCase("cancelAllOrders")){
//                        System.out.println("cancelAllOrders");
//                        try{
//                            ResponseEntity<Map> requestOperation = restTemplate.getForEntity(url+"/api/tutorials?operation_type=cancelAllOrders&api_key="+user.getApi_key()+"&api_secret="+user.getApi_secret()+"&symbol="+order.getTicker().toUpperCase(), Map.class);
//
//                            if(requestOperation.getStatusCode() == HttpStatus.OK){
//                                userOrdersRepository.deleteAll();
//                                orderRepository.deleteAll();
//
//                            }else{
//                                logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                            }
//                        }catch(Exception e){
//                            logService.log("User con id"+user.getId()+"non ha ricevuto il segnale");
//                        }
//                    }
                }
            }
        }
        return null;

    }

    public Order getOrder(int id) {
        return orderRepository.findById(id).get();
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

}
