package com.ecommerce.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value = "/cart")
    public ResponseEntity<Cart> getCartProduct(@RequestHeader  ("token") String token){
        return new ResponseEntity<>(cartService.getCartProduct(token),HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/cart/removeProduct")
    public ResponseEntity<Cart> removeProductFromCart(@RequestBody CartDto cartDto, @RequestHeader  ("token") String token){
        Cart cart = cartService.removeProductFromCart(cartDto, token);
        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cart/clear")
    public ResponseEntity<Cart> clearCart(@RequestHeader ("token")String token){
        return new ResponseEntity<>(cartService.clearCart(token),HttpStatus.ACCEPTED);
    }

}


