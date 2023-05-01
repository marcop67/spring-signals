package com.copytrading.bot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Table;
import java.sql.Date;

@Entity
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "is_active")
    private boolean is_active;
    @Column(name = "name")
    private String name;
    @Column(name = "last_name")
    private String last_name;
    @Column(name = "email")
    private String email;
    @Column(name = "tg_id")
    private String tg_id;
    @Column(name = "tg_name")
    private String tg_name;
    @Column(name = "api_key")
    private String api_key;
    @Column(name = "api_secret")
    private String api_secret;
    @Column(name = "expire_date")
    private Date expire_date;
    @Column(name = "premium")
    private boolean premium;
    @Column(name = "base")
    private boolean base;
    @Column(name = "bot")
    private boolean bot;
    @Column(name = "groupp")
    private boolean groupp;
    @Column(name = "lifetime")
    private boolean lifetime;

    @Column(name = "amount_perc")
    private Float amount_perc;
    @Column(name = "takeprofit")
    private Float takeprofit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTg_id() {
        return tg_id;
    }

    public void setTg_id(String tg_id) {
        this.tg_id = tg_id;
    }

    public String getTg_name() {
        return tg_name;
    }

    public void setTg_name(String tg_name) {
        this.tg_name = tg_name;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_secret() {
        return api_secret;
    }

    public void setApi_secret(String api_secret) {
        this.api_secret = api_secret;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isGroupp() {
        return groupp;
    }

    public void setGroupp(boolean groupp) {
        this.groupp = groupp;
    }

    public boolean isLifetime() {
        return lifetime;
    }

    public void setLifetime(boolean lifetime) {
        this.lifetime = lifetime;
    }

    public User() {
    }

    public User(String id, boolean is_active, String name, String last_name, String email, String tg_id, String tg_name, String api_key, String api_secret, Date expire_date, boolean premium, boolean base, boolean bot, boolean groupp, boolean lifetime) {
        this.id = id;
        this.is_active = is_active;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.tg_id = tg_id;
        this.tg_name = tg_name;
        this.api_key = api_key;
        this.api_secret = api_secret;
        this.expire_date = expire_date;
        this.premium = premium;
        this.base = base;
        this.bot = bot;
        this.groupp = groupp;
        this.lifetime = lifetime;
    }

    public Float getAmount_perc() {
        return amount_perc;
    }

    public void setAmount_perc(Float amount_perc) {
        this.amount_perc = amount_perc;
    }

    public Float getTakeprofit() {
        return takeprofit;
    }

    public void setTakeprofit(Float takeprofit) {
        this.takeprofit = takeprofit;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", is_active=" + is_active +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", tg_id=" + tg_id +
                ", tg_name='" + tg_name + '\'' +
                ", api_key='" + api_key + '\'' +
                ", api_secret='" + api_secret + '\'' +
                ", expire_date=" + expire_date +
                ", premium=" + premium +
                ", base=" + base +
                ", bot=" + bot +
                ", groupp=" + groupp +
                ", lifetime=" + lifetime +
                ", amount_perc=" + amount_perc +
                ", takeprofit=" + takeprofit +
                '}';
    }
}

