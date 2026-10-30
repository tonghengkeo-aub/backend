package com.hotelbooking.controller;

import com.hotelbooking.entity.PaymentMethod;
import com.hotelbooking.entity.PaymentType;
import com.hotelbooking.service.BakongService;
import com.hotelbooking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/bakong")
@CrossOrigin(origins = "*")
public class BakongController {

    private final BakongService bakongService;
    private final BookingService bookingService;

    public BakongController(BakongService bakongService, BookingService bookingService) {
        this.bakongService = bakongService;
        this.bookingService = bookingService;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(bakongService.getConfig());
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<Map<String, Object>> generateQr(@RequestBody Map<String, Object> body) {
        BigDecimal amount = BigDecimal.valueOf(1.00);
        if (body.get("amount") != null) {
            amount = new BigDecimal(body.get("amount").toString());
        }
        String currency = body.get("currency") != null ? body.get("currency").toString() : "USD";
        String billNumber = body.get("billNumber") != null ? body.get("billNumber").toString() : null;

        return ResponseEntity.ok(bakongService.generateKhqr(amount, currency, billNumber));
    }

    @PostMapping("/check-transaction")
    public ResponseEntity<Map<String, Object>> checkTransaction(@RequestBody Map<String, Object> body) {
        String md5 = body.get("md5") != null ? body.get("md5").toString() : null;
        Map<String, Object> checkResult = bakongService.checkTransaction(md5);

        Boolean isPaid = (Boolean) checkResult.get("paid");
        if (Boolean.TRUE.equals(isPaid) && body.get("bookingId") != null) {
            try {
                Long bookingId = Long.valueOf(body.get("bookingId").toString());
                BigDecimal amount = body.get("amount") != null ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO;
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    var booking = bookingService.getBookingById(bookingId);
                    amount = booking.getBalanceDue() != null && booking.getBalanceDue().compareTo(BigDecimal.ZERO) > 0
                            ? booking.getBalanceDue()
                            : booking.getFinalPrice();
                }
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    bookingService.recordPayment(bookingId, amount, PaymentMethod.KHQR, PaymentType.PAYMENT);
                }
            } catch (Exception ignored) {
            }
        }

        return ResponseEntity.ok(checkResult);
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            Long bookingId = Long.valueOf(body.get("bookingId").toString());
            var booking = bookingService.getBookingById(bookingId);

            BigDecimal amount = body.get("amount") != null ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO;
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                amount = booking.getBalanceDue() != null && booking.getBalanceDue().compareTo(BigDecimal.ZERO) > 0
                        ? booking.getBalanceDue()
                        : booking.getFinalPrice();
            }

            var updated = bookingService.recordPayment(bookingId, amount, PaymentMethod.KHQR, PaymentType.PAYMENT);
            resp.put("success", true);
            resp.put("paid", true);
            resp.put("bookingCode", updated.getBookingCode());
            resp.put("status", updated.getStatus().name());
            resp.put("paymentStatus", updated.getPaymentStatus());
            resp.put("amountPaid", updated.getAmountPaid());
            resp.put("balanceDue", updated.getBalanceDue());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
