package com.ecommerce.springboot.exception;


public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(){

    }

    public ProductNotFoundException(String message){
        super(message);
    }

}
