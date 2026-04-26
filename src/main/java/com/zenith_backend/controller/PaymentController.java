package com.zenith_backend.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // STEP 1: Create Order for Razorpay Checkout
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam int amount) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Amount in paise (e.g., Rs 1 = 100 paise)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(orderRequest);
            return ResponseEntity.ok(order.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Razorpay order");
        }
    }

    // STEP 2: Verify the Payment Signature sent by the Frontend
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            // Verify signature using the secret key
            boolean isValid = Utils.verifyPaymentSignature(new JSONObject(data), keySecret);

            if (isValid) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification failed");
        }
    }
}