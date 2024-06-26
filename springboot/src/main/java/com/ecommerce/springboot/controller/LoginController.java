package com.ecommerce.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.CustomerDto;
import com.ecommerce.springboot.models.UserSession;
import com.ecommerce.springboot.service.CustomerService;
import com.ecommerce.springboot.service.LoginLogoutService;
import com.ecommerce.springboot.service.SellerService;

import jakarta.validation.Valid;

@RestController
public class LoginController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoginLogoutService loginService;

    @Autowired
    private SellerService sellerService;

    @PostMapping("/register/customer")
    public ResponseEntity<Customer> registerCustomerHandler(@RequestBody Customer customer){
        return new ResponseEntity<>(customerService.addCustomer(customer),HttpStatus.CREATED);
    }

    @PostMapping(value = "login/customer")
    public ResponseEntity<UserSession> loginCustomer(@RequestBody CustomerDto customerDto){
        return new ResponseEntity<>(loginService.loginCustomer(customerDto),HttpStatus.ACCEPTED);
    }
}
