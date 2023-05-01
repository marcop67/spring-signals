package com.copytrading.bot.service;

import com.copytrading.bot.model.MongoOrderInfo;
import com.copytrading.bot.model.Order;
import com.copytrading.bot.model.TakeProfitInfo;
import com.copytrading.bot.model.User;
import com.copytrading.bot.repository.MongoOrderInfoRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BotMessageSender {

    private static final String TARGET_URL_SEND_MESSAGE = "https://api.telegram.org/";
    private static final String BOT_PREMIUM_CHAT_ID = "-1001893567576";
    private static final String BOT_BASIC_CHAT_ID = "-1001752397769";
    private static final String GROUP_BASIC_CHAT_ID = "-1001838578449";
    private static final String GROUP_PREMIUM_CHAT_ID = "-1001628216577";
    private static final String ALERT_GROUP_CHAT_ID = "-854868534";


    private static final String BOT_ID = "5971602829:AAGb7nG9l6fPlCfF5_xyMon_X27nA57OdmU";

    private final static DecimalFormat df = new DecimalFormat("#.000000");


    private final MongoOrderInfoRepo mongoOrderInfoRepo;
    private final RestTemplate restTemplate;

    public BotMessageSender(MongoOrderInfoRepo mongoOrderInfoRepo, RestTemplate restTemplate) {
        this.mongoOrderInfoRepo = mongoOrderInfoRepo;
        this.restTemplate = restTemplate;
    }


    public void sendMessage(Order orderDto, boolean botMessage) {

        //first send message to each group with different body


       //var signal_message_basic = f"**{pair}**\n(**{type} {levarage}x**)\n\n**ENTRY**: {entry}\n\n**TAKE PROFIT**:\nTarget 1: {tp_one}\nTarget 2: {tp_two}\nTarget 3: {tp_three}\nTarget 4: {tp_four}\nTarget 5: {tp_five}\nTarget 6: {tp_six}\nTarget 7: {tp_seven}\nTarget 8: {tp_eight}\nTarget 9: {tp_nine}\nTarget 10: {tp_ten}\n\n**STOP LOSS**: {sl}"
        //var signal_message_groups_bot = f"Nuova posizione aperta sulla moneta **{pair}**\n\nPer qualsiasi problema, dubbio o domanda contattare il proprio tutor."


        //send message to telegram


        //Create and save
        var mongoOrderInfo = new MongoOrderInfo();
        mongoOrderInfo.setPair(orderDto.getTicker());
        mongoOrderInfo.setEntry(String.valueOf(orderDto.getLimitt()));
        mongoOrderInfo.setType(orderDto.getOrder_type().toUpperCase());
        mongoOrderInfo.setPosition_basic_valid("yes");
        mongoOrderInfo.setPosition_premium_valid("yes");


        var takeprofits = mongoOrderInfo.getTakeprofits();
        var stringBuilderTakeProfits = new StringBuilder("TAKE PROFIT: \n");
        for (int i = 1; i <= 10; i++) {
            var takeProfitInfo = new TakeProfitInfo();
            float price;
            if (orderDto.getOrder_type().equalsIgnoreCase("long")) {
                price = orderDto.getLimitt() + (orderDto.getLimitt() * ((orderDto.getOscillation() * i) / 100));
                df.format(price);
            } else {
                price = orderDto.getLimitt() - (orderDto.getLimitt() * ((orderDto.getOscillation() * i) / 100));
                df.format(price);
            }
            takeProfitInfo.setPrice(String.valueOf(price));
            takeProfitInfo.setTp_basic("no");
            takeProfitInfo.setTp_premium("no");
            takeProfitInfo.setPremium("yes");

            /*if (i <= Math.round(orderDto.getTakeprofit())) {
                takeProfitInfo.setPremium("yes");
            } else {
                takeProfitInfo.setPremium("no");
            }*/

            takeprofits.put("tp" + i, takeProfitInfo);

            stringBuilderTakeProfits.append("TP"+i+": " + takeProfitInfo.getPrice() + "\n");

        }

        mongoOrderInfo.setTp(String.valueOf(orderDto.getTakeprofit()));
        mongoOrderInfo.setOscillation(String.valueOf(orderDto.getOscillation()));
        mongoOrderInfo.setLeverage(String.valueOf(orderDto.getLeverage()));
        mongoOrderInfo.setSl_basic(String.valueOf(orderDto.getStoploss()));
        mongoOrderInfo.setSl_prem(String.valueOf(orderDto.getStoploss()));

//        var stringBuilder = new StringBuilder("TAKE PROFIT: \n");
////        //mando messaggio a telegram
////
////        HashMap<String, TakeProfitInfo> collect = takeprofits.entrySet().stream()
////                .sorted(Map.Entry.comparingByKey())
////                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
////                        (oldValue, newValue) -> oldValue, HashMap::new));
////
////        collect.entrySet().stream()
////                .sorted(Map.Entry.comparingByKey())
////                .forEachOrdered(entry-> stringBuilder.append(entry.getKey().toUpperCase()).append(": ").append(entry.getValue().getPrice()).append("\n"));

        var takeProfitMessage = stringBuilderTakeProfits.toString();

        var premiumMessageStringBuilder = new StringBuilder();
        premiumMessageStringBuilder.append(orderDto.getTicker())
                .append("\n")
                .append("(" + orderDto.getOrder_type() + " " + orderDto.getLeverage() + "x)")
                .append("\n\n")
                .append("ENTRY: " + orderDto.getLimitt())
                .append("\n\n")
                .append(takeProfitMessage)
                .append("\n\n")
                .append("STOP LOSS: " + orderDto.getStoploss())
                .append("\n\n\n")
                .append("🚀PREMIUM")
                .append("\n\n")
                .append("BUDGET: " + Math.round(orderDto.getAmount_perc()) + "%")
                .append("\n")
                .append("TP: " + Math.round(orderDto.getTakeprofit()));

        var basicMessageStringBuilder = new StringBuilder();
        basicMessageStringBuilder.append(orderDto.getTicker())
                .append("\n")
                .append("(" + orderDto.getOrder_type() + " " + orderDto.getLeverage() + "x)")
                .append("\n\n")
                .append("ENTRY: " + orderDto.getLimitt())
                .append("\n\n")
                .append(takeProfitMessage)
                .append("\n\n")
                .append("STOP LOSS: " + orderDto.getStoploss());
//
//
        var botMemberStringBuilder = new StringBuilder()
                .append("Nuova posizione aperta sulla moneta " + orderDto.getTicker())
                .append("\n\n")
                .append("Per qualsiasi problema, dubbio o domanda contattare il proprio tutor.");

////        String message = String.format("**%s**\n(**%s %sx**)\n\n**ENTRY**: %s\n\n%s**STOP LOSS**: %s\n\n\n**PREMIUM**\n\n**BUDGET**: %s \n**TP**: %s",
////                orderDto.getTicker(), orderDto.getOrder_type(), orderDto.getLeverage(), orderDto.getLimitt(),orderDto.getStoploss() ,takeProfitMessage, orderDto.getAmount_perc(), orderDto.getTakeprofit());
//
        var botPremiumMessageUrl = TARGET_URL_SEND_MESSAGE + "bot" + BOT_ID + "/sendMessage?" + "chat_id=" + BOT_PREMIUM_CHAT_ID + "&text=" + botMemberStringBuilder;
        var botBasicMessageUrl = TARGET_URL_SEND_MESSAGE + "bot" + BOT_ID + "/sendMessage?" + "chat_id=" + BOT_BASIC_CHAT_ID + "&text=" + botMemberStringBuilder;

        var groupBasicMessageUrl = TARGET_URL_SEND_MESSAGE + "bot" + BOT_ID + "/sendMessage?" + "chat_id=" + GROUP_BASIC_CHAT_ID + "&text=" + basicMessageStringBuilder;
        var groupPremiumMessageUrl = TARGET_URL_SEND_MESSAGE + "bot" + BOT_ID + "/sendMessage?" + "chat_id=" + GROUP_PREMIUM_CHAT_ID + "&text=" + premiumMessageStringBuilder;
//
//
        if (botMessage) {
            var botPremiumMessageResponse = restTemplate.getForEntity(botPremiumMessageUrl, Map.class);
            var botBasicMessageResponse = restTemplate.getForEntity(botBasicMessageUrl, Map.class);
//
            if (botPremiumMessageResponse.getStatusCode() == HttpStatus.OK && botBasicMessageResponse.getStatusCode() == HttpStatus.OK) {
                Map body = botPremiumMessageResponse.getBody();
                var result = (Map<String, Object>) body.get("result");
                var messageId = (Integer) result.get("message_id");

                Map basicBody = botBasicMessageResponse.getBody();
                var basicResult = (Map<String, Object>) basicBody.get("result");
                var basicMessageId = (Integer) basicResult.get("message_id");

                mongoOrderInfo.setMsg_id_prem(messageId);
                mongoOrderInfo.setMsg_id_basic(basicMessageId);
                mongoOrderInfoRepo.save(mongoOrderInfo);
            }
        } else {
            //send message only to basic and premium group
            restTemplate.getForEntity(groupBasicMessageUrl, Map.class);
            restTemplate.getForEntity(groupPremiumMessageUrl, Map.class);
        }

    }


    public void sendErrorMessageToAlertChat(User user, String status){
        var message = "L'utente " +user.getName() + " " +user.getLast_name() + " con id: " + user.getId() + " e email: " + user.getEmail() +"\n"+
                "non ha ricevuto il messaggio , status -> " + status;

        var alertMessageGroupUrl = TARGET_URL_SEND_MESSAGE + "bot" + BOT_ID + "/sendMessage?" + "chat_id=" + ALERT_GROUP_CHAT_ID + "&text=" + message;
        restTemplate.getForEntity(alertMessageGroupUrl, Map.class);
    }
}
