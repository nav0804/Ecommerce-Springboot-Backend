package com.ecommerce.springboot.exception;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(){
        super();
    }

    public SellerNotFoundException(String message){
        super(message);
    }

}
