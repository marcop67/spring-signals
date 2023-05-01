package com.copytrading.bot.model;

public class TakeProfitInfo {
      private String price;
      private String tp_premium;
      private String tp_basic;
      private String premium;

      public String getPrice() {
            return price;
      }

      public void setPrice(String price) {
            this.price = price;
      }

      public String getTp_premium() {
            return tp_premium;
      }

      public void setTp_premium(String tp_premium) {
            this.tp_premium = tp_premium;
      }

      public String getTp_basic() {
            return tp_basic;
      }

      public void setTp_basic(String tp_basic) {
            this.tp_basic = tp_basic;
      }

      public String getPremium() {
            return premium;
      }

      public void setPremium(String premium) {
            this.premium = premium;
      }

      @Override
      public String toString() {
            return "TakeProfitInfo{" +
                    "price='" + price + '\'' +
                    ", tp_premium='" + tp_premium + '\'' +
                    ", tp_basic='" + tp_basic + '\'' +
                    ", premium='" + premium + '\'' +
                    '}';
      }
}
