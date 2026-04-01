package com.example.Food_Health_Classifier.controller;

import com.example.Food_Health_Classifier.entity.User;
import com.example.Food_Health_Classifier.repository.UserRepository;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.Map;

@RestController
@CrossOrigin
public class verifyPayment {

    @Value("${razorpay.key.secret}")
    private String secret;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/verify-payment")
    public String verify(@RequestBody Map<String, String> data) {

        try {

            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");

            Long userId = Long.parseLong(data.get("userId"));
            String plan = data.get("plan");

            String payload = orderId + "|" + paymentId;
            String generated = generateSignature(payload, secret);

            if (!generated.equals(signature)) {
                return "FAILED";
            }

            User user = userRepository.findById(userId).orElseThrow();

            user.setPlanType(plan);
            user.setFreeScansLeft(0);

            if (plan.equals("BASIC") || plan.equals("PREMIUM")) {
                user.setPlanExpiry(LocalDate.now().plusMonths(1));
            } else {
                user.setPlanExpiry(LocalDate.now().plusYears(1));
            }

            userRepository.save(user);

            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private String generateSignature(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        return Hex.encodeHexString(mac.doFinal(data.getBytes()));
    }
}