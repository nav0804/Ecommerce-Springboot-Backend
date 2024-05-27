package com.ecommerce.springboot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.springboot.models.Cart;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.Order;
import com.ecommerce.springboot.repository.CustomerRepository;
// import com.ecommerce.springboot.repository.SessionRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    // @Autowired
    // private LoginLogoutService loginService;
    // @Autowired
    // private SessionRepository sessionRepository;

    public Customer addCustomer(Customer customer){
        customer.setCreatedOn(LocalDateTime.now());
        Cart c = new Cart();
        customer.setCustomerCart(c);
        customer.setOrders(new ArrayList<Order>());
        Optional<Customer> existing = customerRepository.findByMobileNo(customer.getMobileNo());

        if(existing.isPresent())
            throw new Error();
        customerRepository.save(customer);
        return customer;
    }
    
}   
