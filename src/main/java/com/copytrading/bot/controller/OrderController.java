package com.copytrading.bot.controller;

import com.copytrading.bot.model.Order;
import com.copytrading.bot.service.NewOrderService;
import com.copytrading.bot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    NewOrderService newOrderService;

    @GetMapping("")
    public List<Order> list() {
        return orderService.listAllOrder();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable int id) {
        try {
            Order order = orderService.getOrder(id);
            return new ResponseEntity<Order>(order, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody Order order) throws Exception {
        if(order.getOrder_type().toLowerCase().contains("update")){
            newOrderService.updateOrder(order);
        } else if (order.getOrder_type().equalsIgnoreCase("cancelAllOrders")) {
            newOrderService.cancelOrderAndPosition(order);
        } else {
            newOrderService.saveOrder(order);
        }

        //retry failed orders
        //newOrderService.manageOrdersToRetry();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/retries/{order_id}")
    public ResponseEntity<?> retry(@PathVariable("order_id") String orderId) { //trigger manually retry from failed orders
      newOrderService.manageOrdersToRetry(orderId);
      return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-accounts")
    public ResponseEntity<?> checkAccounts() { //trigger manually check api with bybit valid
        newOrderService.checkApiAccounts();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        orderService.deleteOrder(id);
    }
}