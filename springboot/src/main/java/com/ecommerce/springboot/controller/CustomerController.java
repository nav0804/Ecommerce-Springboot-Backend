package com.ecommerce.springboot.controller;

import java.util.List;

import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.springboot.exception.CustomerException;
import com.ecommerce.springboot.exception.CustomerNotFoundException;
import com.ecommerce.springboot.models.Address;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.CustomerDto;
import com.ecommerce.springboot.models.CustomerUpdateDto;
import com.ecommerce.springboot.models.SessionDto;
import com.ecommerce.springboot.service.CustomerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @GetMapping("/allCustomers")
    public ResponseEntity<List<Customer>> allCustomers(@RequestHeader ("token") String token) throws CustomerNotFoundException{
        return new ResponseEntity<>(customerService.getAllCustomers(token),HttpStatus.ACCEPTED);
    }

    @PostMapping("/creatCustomer")
    public ResponseEntity<Customer> createNewCustomer(@RequestBody Customer customer) {
        return new ResponseEntity<>(customer,HttpStatus.NO_CONTENT);
    }

    @GetMapping("/loggedInCustomer")
    public ResponseEntity<Customer> loggedInUserDetails(@RequestHeader String token){
        return new ResponseEntity<>(customerService.getLoggedInCustomerDetails(token),HttpStatus.ACCEPTED);
    }

    @PutMapping("/editCustomer")
    public ResponseEntity<Customer> editCustomer(@RequestBody CustomerDto customerDto,@RequestHeader String token) throws CustomerNotFoundException{
        return new ResponseEntity<>(customerService.updateCustomer(customerDto, token),HttpStatus.ACCEPTED);
    }

    @PutMapping("/editCustomerDetails")
    public ResponseEntity<Customer> editCustomerMobileAndEmail(@RequestBody CustomerDto customerUpdateDTO, @RequestHeader String token){
        return new ResponseEntity<>(customerService.updateCustomerMobileNoOrEmailId(customerUpdateDTO, token),HttpStatus.ACCEPTED);
    } 

    @PutMapping("/updatePassword")
    public ResponseEntity<SessionDto> editPassword(@RequestBody CustomerDto customerDto, @RequestHeader String token) {
        return new ResponseEntity<>(customerService.updateCustomerPassword(customerDto, token), HttpStatus.ACCEPTED);
    }

    @PutMapping("/updateAddress")
    public ResponseEntity<Customer> editAddress(@RequestBody Address address, @RequestParam String type, @RequestHeader String token) throws CustomerException{
        return new ResponseEntity<>(customerService.updateAddress(address, type, token),HttpStatus.ACCEPTED);
    }
    






}
