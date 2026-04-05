package com.example.Food_Health_Classifier.repository;

import com.example.Food_Health_Classifier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}