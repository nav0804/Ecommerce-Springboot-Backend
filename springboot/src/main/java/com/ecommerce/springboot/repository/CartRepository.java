package com.ecommerce.springboot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ecommerce.springboot.models.Cart;

public interface CartRepository extends CrudRepository<Cart,Integer>{

}
