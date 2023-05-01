package com.copytrading.bot.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document("bot")
public class MongoOrderInfo {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private Integer msg_id_basic;
    private Integer msg_id_prem;
    private String pair;
    private String entry;
    private String type;
    private String position_basic_valid;
    private String position_premium_valid;

    private Map<String, TakeProfitInfo> takeprofits = new HashMap<>();

    private String tp;

    private String sl_basic;

    private String sl_prem;

    private String leverage;

    private String oscillation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getMsg_id_basic() {
        return msg_id_basic;
    }

    public void setMsg_id_basic(Integer msg_id_basic) {
        this.msg_id_basic = msg_id_basic;
    }

    public Integer getMsg_id_prem() {
        return msg_id_prem;
    }

    public void setMsg_id_prem(Integer msg_id_prem) {
        this.msg_id_prem = msg_id_prem;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition_basic_valid() {
        return position_basic_valid;
    }

    public void setPosition_basic_valid(String position_basic_valid) {
        this.position_basic_valid = position_basic_valid;
    }

    public String getPosition_premium_valid() {
        return position_premium_valid;
    }

    public void setPosition_premium_valid(String position_premium_valid) {
        this.position_premium_valid = position_premium_valid;
    }

    public Map<String, TakeProfitInfo> getTakeprofits() {
        return takeprofits;
    }

    public void setTakeprofits(Map<String, TakeProfitInfo> takeprofits) {
        this.takeprofits = takeprofits;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getSl_basic() {
        return sl_basic;
    }

    public void setSl_basic(String sl_basic) {
        this.sl_basic = sl_basic;
    }

    public String getSl_prem() {
        return sl_prem;
    }

    public void setSl_prem(String sl_prem) {
        this.sl_prem = sl_prem;
    }

    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }

    public String getOscillation() {
        return oscillation;
    }

    public void setOscillation(String oscillation) {
        this.oscillation = oscillation;
    }

    @Override
    public String toString() {
        return "MongoOrderInfo{" +
                "id='" + id + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", msg_id_basic=" + msg_id_basic +
                ", msg_id_prem=" + msg_id_prem +
                ", pair='" + pair + '\'' +
                ", entry='" + entry + '\'' +
                ", type='" + type + '\'' +
                ", position_basic_valid='" + position_basic_valid + '\'' +
                ", position_premium_valid='" + position_premium_valid + '\'' +
                ", takeprofits=" + takeprofits +
                ", tp='" + tp + '\'' +
                ", sl_basic='" + sl_basic + '\'' +
                ", sl_prem='" + sl_prem + '\'' +
                ", leverage='" + leverage + '\'' +
                ", oscillation='" + oscillation + '\'' +
                '}';
    }
}
