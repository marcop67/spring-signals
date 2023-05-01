package com.copytrading.bot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_orders")
public class UserOrders {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private String user_id;
    @Column(name = "order_id")
    private int order_id;
    @Column(name = "bybit_order_id", nullable = true)
    private String bybit_order_id;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "retries")
    private Integer retries;


    public UserOrders() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getBybit_order_id() {
        return bybit_order_id;
    }

    public void setBybit_order_id(String bybit_order_id) {
        this.bybit_order_id = bybit_order_id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    @Override
    public String toString() {
        return "UserOrders{" +
                "id=" + id +
                ", user_id='" + user_id + '\'' +
                ", order_id=" + order_id +
                ", bybit_order_id='" + bybit_order_id + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", retries=" + retries +
                '}';
    }
}

