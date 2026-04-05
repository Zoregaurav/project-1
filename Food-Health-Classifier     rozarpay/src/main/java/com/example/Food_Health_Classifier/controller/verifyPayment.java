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
            
            System.out.println("FULL DATA: " + data);

            System.out.println("Order ID: " + data.get("razorpay_order_id"));
            System.out.println("Payment ID: " + data.get("razorpay_payment_id"));
            System.out.println("Signature: " + data.get("razorpay_signature"));

            String username = data.get("username");
            String plan = data.get("plan");
          

         // 🔥 STOP if invalid
         if (username == null || username.isEmpty()) {
             return "ERROR: Username missing";
         }

         System.out.println("USERNAME RECEIVED: " + username);
            // 🔍 Find user
            User user = userRepository.findByUsername(username);

            // 🆕 Create if not exists
            if (user == null) {
                user = new User();
                user.setUsername(username);

                // default free trial
                user.setFreeScansLeft(5);
                user.setPaidScansLeft(0);
                user.setPlanType("FREE");
            }

            // 🔥 SET PLAN
            user.setPlanType(plan);

            // 🔥 ADD SCANS BASED ON PLAN
            int scansToAdd = 0;

            if (plan.equals("BASIC")) {
                scansToAdd = 50;
                user.setPlanExpiry(LocalDate.now().plusMonths(1));
            }
            else if (plan.equals("PREMIUM")) {
                scansToAdd = 150;
                user.setPlanExpiry(LocalDate.now().plusMonths(1));
            }
            else if (plan.equals("PRO")) {
                scansToAdd = 500;
                user.setPlanExpiry(LocalDate.now().plusYears(1));
            }

            // 🔥 VERY IMPORTANT (MAIN FIX)
            int currentPaid = user.getPaidScansLeft();
            user.setPaidScansLeft(currentPaid + scansToAdd);

            // ❗ Remove free scans after upgrade
            user.setFreeScansLeft(0);

            // 💾 Save to DB
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