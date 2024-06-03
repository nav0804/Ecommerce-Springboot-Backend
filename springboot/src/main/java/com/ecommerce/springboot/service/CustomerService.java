package com.ecommerce.springboot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.stereotype.Service;

import com.ecommerce.springboot.exception.CustomerException;
import com.ecommerce.springboot.exception.CustomerNotFoundException;
import com.ecommerce.springboot.exception.LoginException;
import com.ecommerce.springboot.models.Address;
import com.ecommerce.springboot.models.Cart;
import com.ecommerce.springboot.models.CreditCard;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.CustomerDto;
import com.ecommerce.springboot.models.Order;
import com.ecommerce.springboot.models.SessionDto;
import com.ecommerce.springboot.models.UserSession;
import com.ecommerce.springboot.repository.CustomerRepository;
import com.ecommerce.springboot.repository.SessionRespository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoginLogoutService loginService;
    @Autowired
    private SessionRespository sessionRepository;

    public Customer addCustomer(Customer customer) {
        customer.setCreatedOn(LocalDateTime.now());
        Cart c = new Cart();
        customer.setCustomerCart(c);
        customer.setOrders(new ArrayList<Order>());
        Optional<Customer> existing = customerRepository.findByMobileNo(customer.getMobileNo());

        if (existing.isPresent())
            throw new Error();
        customerRepository.save(customer);
        return customer;
    }

    public Customer getLoggedInCustomerDetails(String token) {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        return existingCustomer;
    }

    // Method to get all customers - only seller or admin can get all customers -
    // check validity of seller token

    
    public List<Customer> getAllCustomers(String token) throws CustomerNotFoundException {

        // update to seller

        if (token.contains("seller") == false) {
            throw new LoginException("Invalid session token.");
        }

        loginService.checkTokenStatus(token);

        List<Customer> customers = (List<Customer>)customerRepository.findAll();

        if (customers.size() == 0)
            throw new CustomerNotFoundException("No record exists");

        return customers;
    }

    // Method to update entire customer details - either mobile number or email id
    // should be correct

    
    public Customer updateCustomer(CustomerDto customer, String token) throws CustomerNotFoundException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        Optional<Customer> opt = customerRepository.findByMobileNo(customer.getMobileId());

        Optional<Customer> res = customerRepository.findByEmailId(customer.getEmail());

        if (opt.isEmpty() && res.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist with given mobile no or email-id");

        Customer existingCustomer = null;

        if (opt.isPresent())
            existingCustomer = opt.get();
        else
            existingCustomer = res.get();

        UserSession user = sessionRepository.findByToken(token).get();

        if (existingCustomer.getCustomerId() == user.getUserId()) {

            if (customer.getEmail() != null) {
                existingCustomer.setEmailId(customer.getEmail());
            }

            if (customer.getMobileId() != null) {
                existingCustomer.setMobileNo(customer.getMobileId());
            }

            if (customer.getPassword() != null) {
                existingCustomer.setPassword(customer.getPassword());
            }


            customerRepository.save(existingCustomer);
            return existingCustomer;

        } else {
            throw new CustomerException("Error in updating. Verification failed.");
        }

    }

    // Method to update customer mobile number - details updated for current logged
    // in user

    
    public Customer updateCustomerMobileNoOrEmailId(CustomerDto customerUpdateDTO, String token)
            throws CustomerNotFoundException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        if (customerUpdateDTO.getEmail() != null) {
            existingCustomer.setEmailId(customerUpdateDTO.getEmail());
        }

        existingCustomer.setMobileNo(customerUpdateDTO.getMobileId());

        customerRepository.save(existingCustomer);

        return existingCustomer;

    }

    // Method to update password - based on current token

    public SessionDto updateCustomerPassword(CustomerDto customerDTO, String token) {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        if (customerDTO.getMobileId().equals(existingCustomer.getMobileNo()) == false) {
            throw new CustomerException("Verification error. Mobile number does not match");
        }

        existingCustomer.setPassword(customerDTO.getPassword());

        customerRepository.save(existingCustomer);

        SessionDto session = new SessionDto();

        session.setToken(token);

        loginService.logoutCustomer(session);

        return session;

    }

    // Method to add/update Address

    public Customer updateAddress(Address address, String type, String token) throws CustomerException {
        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        existingCustomer.getAddress().put(type, address);

        return customerRepository.save(existingCustomer);

    }

    // Method to update Credit card

    public Customer updateCreditCardDetails(String token, CreditCard card) throws CustomerException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        existingCustomer.setCreditCard(card);

        return customerRepository.save(existingCustomer);
    }

    // Method to delete a customer by mobile id

    
    public SessionDto deleteCustomer(CustomerDto customerDTO, String token) throws CustomerNotFoundException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        SessionDto session = new SessionDto();

        session.setMessage("");

        session.setToken(token);

        if (existingCustomer.getMobileNo().equals(customerDTO.getMobileId())
                && existingCustomer.getPassword().equals(customerDTO.getPassword())) {

            customerRepository.delete(existingCustomer);

            loginService.logoutCustomer(session);

            session.setMessage("Deleted account and logged out successfully");

            return session;
        } else {
            throw new CustomerException("Verification error in deleting account. Please re-check details");
        }

    }

    
    public Customer deleteAddress(String type, String token) throws CustomerException, CustomerNotFoundException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        if (existingCustomer.getAddress().containsKey(type) == false)
            throw new CustomerException("Address type does not exist");

        existingCustomer.getAddress().remove(type);

        return customerRepository.save(existingCustomer);
    }

    
    public List<Order> getCustomerOrders(String token) throws CustomerException {

        if (token.contains("customer") == false) {
            throw new LoginException("Invalid session token for customer");
        }

        loginService.checkTokenStatus(token);

        UserSession user = sessionRepository.findByToken(token).get();

        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if (opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");

        Customer existingCustomer = opt.get();

        List<Order> myOrders = existingCustomer.getOrders();

        if (myOrders.size() == 0)
            throw new CustomerException("No orders found");

        return myOrders;
    }

}
