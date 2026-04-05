package com.example.Food_Health_Classifier.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users") // good practice
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   // ✅ added
    private String email;
    private String password;

    // Subscription fields
    @Column(name = "free_scans_left")
    private int freeScansLeft = 5;

    @Column(name = "paid_scans_left")
    private int paidScansLeft = 0;  // ✅ added

    private String planType = "FREE"; // FREE / BASIC / PREMIUM / PRO
    private LocalDate planExpiry;

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getFreeScansLeft() { return freeScansLeft; }
    public void setFreeScansLeft(int freeScansLeft) { this.freeScansLeft = freeScansLeft; }

    public int getPaidScansLeft() { return paidScansLeft; }
    public void setPaidScansLeft(int paidScansLeft) { this.paidScansLeft = paidScansLeft; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public LocalDate getPlanExpiry() { return planExpiry; }
    public void setPlanExpiry(LocalDate planExpiry) { this.planExpiry = planExpiry; }
}