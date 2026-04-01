package com.example.Food_Health_Classifier.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    

    private int freeScansLeft = 5;
    private String planType = "FREE";
    private LocalDate planExpiry;

    // getters setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getFreeScansLeft() { return freeScansLeft; }
    public void setFreeScansLeft(int freeScansLeft) { this.freeScansLeft = freeScansLeft; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public LocalDate getPlanExpiry() { return planExpiry; }
    public void setPlanExpiry(LocalDate planExpiry) { this.planExpiry = planExpiry; }
}