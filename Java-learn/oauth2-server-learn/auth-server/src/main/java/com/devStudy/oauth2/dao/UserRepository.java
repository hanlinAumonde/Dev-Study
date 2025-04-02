package com.devStudy.oauth2.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devStudy.oauth2.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
