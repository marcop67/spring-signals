package com.copytrading.bot.service;

import com.copytrading.bot.logging.LoggingService;
import com.copytrading.bot.model.Order;
import com.copytrading.bot.model.User;
import com.copytrading.bot.model.UserOrders;
import com.copytrading.bot.repository.OrderRepository;
import com.copytrading.bot.repository.UserOrdersRepository;
import com.copytrading.bot.repository.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Calendar;
import java.util.List;

@Service
public class UserOrdersService {
    @Autowired
    private UserOrdersRepository userOrdersRepository;
    @Autowired
    LoggingService logService;

    public List<UserOrders> listAllUserOrders() {
        return userOrdersRepository.findAll();
    }

    public UserOrders saveOrder(UserOrders userOrders) throws Exception {
        return userOrdersRepository.save(userOrders);
    }

    public UserOrders getUserOrders(int id) {
        return userOrdersRepository.findById(id).get();
    }

    public void deleteUserOrders(int id) {
        userOrdersRepository.deleteById(id);
    }

    public static String encode(String apisecret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(apisecret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8")));

    }
    /*private String sha256_HMAC(String apikey, String secret, String payload) {
        String hash = "";
        try {
            Double esponenziale =Math.pow(10, 3);
            Calendar calendar = Calendar.getInstance();
            Double timestampInMills = calendar.getTimeInMillis()*esponenziale;
            String time = String.valueOf(timestampInMills.intValue());

            String hashedString =apikey.concat(time);
             hashedString = hashedString.concat("5000");
            //hashedString =hashedString.concat(payload);

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(hashedString.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;

    }
    private String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    private String generate_signature(String apikey, String api_secret, String payload){ return sha256_HMAC( apikey,  api_secret,  payload); }
*/
}
