package com.ecommerce.springboot.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ecommerce.springboot.models.Seller;


public interface SellerRepository extends CrudRepository<Seller, Integer> {
    Optional<Seller> findByMobile(String mobile);
}
