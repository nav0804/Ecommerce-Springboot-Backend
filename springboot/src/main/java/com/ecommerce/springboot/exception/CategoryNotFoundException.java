package com.ecommerce.springboot.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(){

    }
    public CategoryNotFoundException(String message){
        super(message);
    }
}
