package com.example.Food_Health_Classifier.controller;

import com.example.Food_Health_Classifier.services.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CreateOrderController {

	 @Value("${razorpay.key.id}")
	    private String key;

	    @Value("${razorpay.key.secret}")
	    private String secret;
    @Autowired
    private RazorpayService razorpayService;

    @GetMapping("/create-order")
    public Map<String, Object> createOrder(@RequestParam String plan) throws Exception {

        int amount = 0;

        if (plan.equals("BASIC")) {
            amount = 4900; // ₹49
        } 
        else if (plan.equals("PREMIUM")) {
            amount = 9900; // ₹99
        } 
        else if (plan.equals("PRO")) {
            amount = 89900; // ₹899
        }

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", amount); // ⚠️ in paise
        options.put("currency", "INR");
        options.put("receipt", "order_" + System.currentTimeMillis());

        Order order = client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", amount);

        return response;
    }
}