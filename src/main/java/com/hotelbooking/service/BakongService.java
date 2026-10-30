package com.hotelbooking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BakongService {

    @Value("${app.bakong.token}")
    private String bakongToken;

    @Value("${app.bakong.acquiring-bank:bkrtkhppxxx}")
    private String acquiringBank;

    @Value("${app.bakong.account-id:sen_tongheng@bkrt}")
    private String bakongAccountId;

    @Value("${app.bakong.merchant-name:Boss Heng Luxury Hotel}")
    private String merchantName;

    @Value("${app.bakong.merchant-city:Phnom Penh}")
    private String merchantCity;

    @Value("${app.bakong.aba-usd-account:003952523}")
    private String abaUsdAccount;

    @Value("${app.bakong.aba-khr-account:500855050}")
    private String abaKhrAccount;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bakongAccountId", bakongAccountId);
        config.put("acquiringBank", acquiringBank);
        config.put("merchantName", merchantName);
        config.put("merchantCity", merchantCity);
        config.put("abaUsdAccount", abaUsdAccount);
        config.put("abaKhrAccount", abaKhrAccount);
        return config;
    }

    /**
     * Generate an official, EMVCo-compliant KHQR string for the user's ABA Bank account.
     */
    public Map<String, Object> generateKhqr(BigDecimal amount, String currency, String billNumber) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            amount = BigDecimal.valueOf(1.00);
        }
        boolean isKhr = "KHR".equalsIgnoreCase(currency) || "116".equals(currency);
        String currencyCode = isKhr ? "116" : "840";
        String formattedAmount = isKhr ? String.valueOf(amount.setScale(0, RoundingMode.HALF_UP).longValue())
                                      : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        if (billNumber == null || billNumber.trim().isEmpty()) {
            billNumber = "BILL-" + System.currentTimeMillis();
        }

        // EMVCo TLV Construction
        StringBuilder sb = new StringBuilder();
        sb.append(tlv(0, "01")); // Format indicator
        sb.append(tlv(1, "12")); // Dynamic point of initiation (12 = dynamic with amount)

        // Account Routing: Tag 30 (ABA Bank Account Number) or Tag 29 (Bakong ID)
        String targetAccount = isKhr ? abaKhrAccount : abaUsdAccount;
        if (targetAccount != null) {
            targetAccount = targetAccount.replaceAll("\\s+", "");
        }

        if (bakongAccountId != null && !bakongAccountId.trim().isEmpty()) {
            // Tag 29: Individual Merchant Account Info (Official NBC Bakong Standard)
            String subtag00 = tlv(0, bakongAccountId.trim());
            String bankName = "Bakong";
            String subtag02 = tlv(2, bankName);
            sb.append(tlv(29, subtag00 + subtag02));
        } else {
            // Tag 30: ABA Bank direct account routing
            String bankCode = (acquiringBank != null && !acquiringBank.trim().isEmpty()) ? acquiringBank.trim() : "abaakhppxxx";
            String accNum = (targetAccount != null && !targetAccount.isEmpty()) ? targetAccount : (isKhr ? "500855050" : "003952523");
            String sub00 = tlv(0, bankCode);
            String sub01 = tlv(1, accNum);
            sb.append(tlv(30, sub00 + sub01));
        }

        sb.append(tlv(52, "5999")); // MCC
        sb.append(tlv(53, currencyCode)); // Currency
        sb.append(tlv(54, formattedAmount)); // Amount
        sb.append(tlv(58, "KH")); // Country code
        sb.append(tlv(59, merchantName)); // Merchant name
        sb.append(tlv(60, merchantCity)); // Merchant city

        // Tag 62: Additional Data Template
        String subtagBill = tlv(1, billNumber);
        sb.append(tlv(62, subtagBill));

        // Tag 99: Timestamp Template (Mandatory for Dynamic KHQR 01=12 under NBC EMVCo KHQR Standard)
        long nowMillis = System.currentTimeMillis();
        long expireMillis = nowMillis + (10 * 60 * 1000); // 10 minutes validity
        String subtag00Time = tlv(0, String.valueOf(nowMillis));
        String subtag01Expire = tlv(1, String.valueOf(expireMillis));
        sb.append(tlv(99, subtag00Time + subtag01Expire));

        String raw = sb.toString() + "6304";
        String crc = calculateCrc16(raw);
        String fullKhqr = raw + crc;
        String md5 = md5Hex(fullKhqr);

        // Fetch official Bakong Deep Link
        String deeplink = null;
        String fullDeeplink = null;
        try {
            Map<String, String> dlReq = Map.of("qr", fullKhqr);
            String jsonBody = objectMapper.writeValueAsString(dlReq);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-bakong.nbc.gov.kh/v1/generate_deeplink_by_qr"))
                    .timeout(Duration.ofSeconds(6))
                    .header("Authorization", "Bearer " + bakongToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Map<String, Object> respMap = objectMapper.readValue(resp.body(), Map.class);
                if (respMap.get("data") instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) respMap.get("data");
                    deeplink = (String) data.get("shortLink");
                    fullDeeplink = (String) data.get("fullLink");
                }
            }
        } catch (Exception ignored) {
            // Deeplink optional; raw KHQR works universally
        }

        Map<String, Object> result = new HashMap<>();
        result.put("khqr", fullKhqr);
        result.put("md5", md5);
        result.put("deeplink", deeplink);
        result.put("fullDeeplink", fullDeeplink);
        result.put("amount", amount);
        result.put("currency", isKhr ? "KHR" : "USD");
        result.put("merchantName", merchantName);
        result.put("bakongAccountId", bakongAccountId);
        result.put("abaUsdAccount", abaUsdAccount);
        result.put("abaKhrAccount", abaKhrAccount);
        result.put("billNumber", billNumber);
        return result;
    }

    /**
     * Check transaction status on NBC Bakong Open API by MD5 hash
     */
    public Map<String, Object> checkTransaction(String md5) {
        Map<String, Object> result = new HashMap<>();
        if (md5 == null || md5.trim().isEmpty()) {
            result.put("paid", false);
            result.put("message", "MD5 hash required");
            return result;
        }

        try {
            Map<String, String> reqBody = Map.of("md5", md5.trim().toLowerCase());
            String json = objectMapper.writeValueAsString(reqBody);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-bakong.nbc.gov.kh/v1/check_transaction_by_md5"))
                    .timeout(Duration.ofSeconds(6))
                    .header("Authorization", "Bearer " + bakongToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("[Bakong Check MD5] " + md5 + " -> Status: " + resp.statusCode() + ", Resp: " + resp.body());
            Map<String, Object> respMap = objectMapper.readValue(resp.body(), Map.class);

            Object respCode = respMap.get("responseCode");
            boolean isSuccess = respCode != null && (Integer.valueOf(0).equals(respCode) || "0".equals(respCode.toString()));

            result.put("paid", isSuccess);
            result.put("responseCode", respCode);
            result.put("errorCode", respMap.get("errorCode"));
            result.put("responseMessage", respMap.get("responseMessage"));
            result.put("data", respMap.get("data"));
            return result;
        } catch (Exception e) {
            result.put("paid", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    private static String tlv(int tag, String value) {
        if (value == null) value = "";
        return String.format("%02d%02d%s", tag, value.length(), value);
    }

    public static String calculateCrc16(String data) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;
        for (int i = 0; i < data.length(); i++) {
            crc ^= ((int) data.charAt(i) << 8);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = ((crc << 1) ^ polynomial) & 0xFFFF;
                } else {
                    crc = (crc << 1) & 0xFFFF;
                }
            }
        }
        return String.format("%04X", crc);
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
