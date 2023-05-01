package com.copytrading.bot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "orderr")
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "order_type")
    private String order_type;
    @Column(name = "limitt")
    private Float limitt;
    @Column(name = "amount_perc")
    private Float amount_perc;
    @Column(name = "leverage")
    private Float leverage;
    @Column(name = "takeprofit")
    private Float takeprofit;
    @Column(name = "stoploss")
    private Float stoploss;
    @Column(name = "oscillation")
    private Float oscillation;
    @Column(name = "active")
    private boolean active;

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public Float getLimitt() {
        return limitt;
    }

    public void setLimitt(Float limitt) {
        this.limitt = limitt;
    }

    public Float getAmount_perc() {
        return amount_perc;
    }

    public void setAmount_perc(Float amount_perc) {
        this.amount_perc = amount_perc;
    }

    public Float getLeverage() {
        return leverage;
    }

    public void setLeverage(Float leverage) {
        this.leverage = leverage;
    }

    public Float getTakeprofit() {
        return takeprofit;
    }

    public void setTakeprofit(Float takeprofit) {
        this.takeprofit = takeprofit;
    }

    public Float getStoploss() {
        return stoploss;
    }

    public void setStoploss(Float stoploss) {
        this.stoploss = stoploss;
    }

    public Float getOscillation() {
        return oscillation;
    }

    public void setOscillation(Float oscillation) {
        this.oscillation = oscillation;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", modifyDate=" + modifyDate +
                ", ticker='" + ticker + '\'' +
                ", order_type='" + order_type + '\'' +
                ", limitt=" + limitt +
                ", amount_perc=" + amount_perc +
                ", leverage=" + leverage +
                ", takeprofit=" + takeprofit +
                ", stoploss=" + stoploss +
                ", oscillation=" + oscillation +
                ", active=" + active +
                '}';
    }
}

