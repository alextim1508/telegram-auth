package com.alextim.telegramauth.service;

import com.alextim.telegramauth.property.TelegramAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    private final TelegramAuthProperties telegramAuthProperties;

    public boolean validateTelegramData(Map<String, Object> telegramData) {
        log.debug("Validating Telegram data: {}", telegramData);

        String receivedHash = (String) telegramData.get("hash");
        if (receivedHash == null || receivedHash.isEmpty()) {
            log.warn("Required 'hash' field is missing or empty");
            return false;
        }

        Map<String, Object> dataToCheck = new TreeMap<>(telegramData);
        dataToCheck.remove("hash");

        StringBuilder dataCheckStringSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : dataToCheck.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            dataCheckStringSb.append(key).append("=").append(value).append("\n");
        }

        if (dataCheckStringSb.length() > 0) {
            dataCheckStringSb.deleteCharAt(dataCheckStringSb.length() - 1);
        }
        String dataCheckString = dataCheckStringSb.toString();

        log.debug("Data check string for HMAC: {}", dataCheckString);

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = sha256.digest(telegramAuthProperties.getBotToken().getBytes(StandardCharsets.UTF_8));

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            hmac.init(secretKeySpec);

            byte[] calculatedHashBytes = hmac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));


            StringBuilder calculatedHashSb = new StringBuilder();
            for (byte b : calculatedHashBytes) {
                calculatedHashSb.append(String.format("%02x", b));
            }
            String calculatedHash = calculatedHashSb.toString();

            log.debug("Received hash: {}, Calculated hash: {}", receivedHash, calculatedHash);

            return receivedHash.equalsIgnoreCase(calculatedHash);

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 or HmacSHA256 algorithm not available", e);
            return false;
        } catch (InvalidKeyException e) {
            log.error("Invalid key provided for HMAC", e);
            return false;
        }
    }
}