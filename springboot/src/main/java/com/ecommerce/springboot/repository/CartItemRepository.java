package com.ecommerce.springboot.repository;

import org.springframework.data.repository.CrudRepository;

import com.ecommerce.springboot.models.CartItem;

public interface CartItemRepository extends CrudRepository<CartItem,Integer> {

}
