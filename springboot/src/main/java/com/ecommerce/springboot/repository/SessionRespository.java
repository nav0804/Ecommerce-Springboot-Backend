package com.ecommerce.springboot.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ecommerce.springboot.models.UserSession;

public interface SessionRespository extends CrudRepository<UserSession,Integer>{
    
    Optional<UserSession> findByToken(String token);
    Optional<UserSession> findByUserId(Integer userId);

}
