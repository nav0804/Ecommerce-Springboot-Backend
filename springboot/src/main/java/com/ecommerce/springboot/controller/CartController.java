package com.ecommerce.springboot.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.springboot.models.Cart;
import com.ecommerce.springboot.models.CartDto;
import com.ecommerce.springboot.repository.CartRepository;
import com.ecommerce.springboot.repository.CustomerRepository;
import com.ecommerce.springboot.service.CartService;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired 
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping(value = "/cart/add")
    public ResponseEntity<Cart> addProductToCart(@RequestBody CartDto cartDto, @RequestHeader ("token") String token){
        Cart cart = cartService.addProduct(cartDto,token);
        return new ResponseEntity<>(cart,HttpStatus.CREATED);
    }


}


