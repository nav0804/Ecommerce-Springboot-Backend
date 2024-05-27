package com.ecommerce.springboot.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginException extends RuntimeException {

    public LoginException(String message){
        super(message);
    }

}
