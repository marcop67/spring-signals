package com.copytrading.bot.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.sql.Date;

public class UserDto {
    private String id;
    private boolean is_active;
    private String name;
    private String last_name;
    private String email;
    private String tg_id;
    private String tg_name;
    private String api_key;
    private String api_secret;
    private String new_status;
    private Date expire_date;
    private boolean premium;
    private boolean base;
    private boolean bot;
    private boolean groupp;
    private boolean lifetime;

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

    public String getNew_status() {
        return new_status;
    }

    public void setNew_status(String new_status) {
        this.new_status = new_status;
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

    @Override
    public String toString() {
        return "UserDto{" +
                "id='" + id + '\'' +
                ", is_active=" + is_active +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", tg_id=" + tg_id +
                ", tg_name='" + tg_name + '\'' +
                ", api_key='" + api_key + '\'' +
                ", api_secret='" + api_secret + '\'' +
                ", new_status='" + new_status + '\'' +
                ", expire_date=" + expire_date +
                ", premium=" + premium +
                ", base=" + base +
                ", bot=" + bot +
                ", groupp=" + groupp +
                ", lifetime=" + lifetime +
                '}';
    }
}
