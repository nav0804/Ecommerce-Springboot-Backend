package com.ecommerce.springboot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecommerce.springboot.exception.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.springboot.exception.CustomerNotFoundException;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.CustomerDto;
import com.ecommerce.springboot.models.Seller;
import com.ecommerce.springboot.models.SellerDto;
import com.ecommerce.springboot.models.SessionDto;
import com.ecommerce.springboot.models.UserSession;
import com.ecommerce.springboot.repository.SessionRespository;
import com.ecommerce.springboot.repository.SellerRepository;
import com.ecommerce.springboot.repository.CustomerRepository;

@Component
public class LoginLogoutService{
     
    @Autowired
    private SessionRespository sessionRespository;
    @Autowired 
    private CustomerRepository customerRepository;
    @Autowired
    private SellerRepository sellerRepository;

    public UserSession loginCustomer(CustomerDto login){
        Optional<Customer> res = customerRepository.findByMobileNo(login.getMobileId());
        if(res.isEmpty())
            throw new CustomerNotFoundException("User details not found with these details");
        
        Customer existingCustomer = res.get();
        Optional<UserSession> opt = sessionRespository.findByUserId(existingCustomer.getCustomerId());
        if(opt.isPresent()){
            UserSession user = opt.get();
            if(user.getSessionEndTime().isBefore(LocalDateTime.now())){
                sessionRespository.delete(user);
            }
            else{
                throw new LoginException("User already logged in");
            }
        }

        if(existingCustomer.getPassword().equals(login.getPassword())){
            UserSession newSession = new UserSession();
            newSession.setUserId(existingCustomer.getCustomerId());
            newSession.setUserType("customer");
            newSession.setSessionStartTime(LocalDateTime.now());
            newSession.setSessionEndTime(LocalDateTime.now().plusHours(1));
            UUID uuid = UUID.randomUUID();
            String token = "customer_"+uuid.toString().split("-")[0];
            newSession.setToken(token);
            return sessionRespository.save(newSession);          
        }
        else{
            throw new LoginException("Password Incorrect");
        }
    }

    public SessionDto logoutCustomer(SessionDto sessionToken){
        String token = sessionToken.getToken();
        checkTokenStatus(token);
        Optional<UserSession> opt = sessionRespository.findByToken(token);

        if(!opt.isPresent())
            throw new LoginException("User not logged in");
        UserSession session = opt.get();
        sessionRespository.delete(session);
        sessionToken.setMessage("Logged out successfully");
        return sessionToken;
    }

    public UserSession loginSeller(SellerDto seller){
        Optional<Seller> res = sellerRepository.findByMobile(seller.getMobile());
        if(res.isEmpty())
            throw new CustomerNotFoundException("User details not found with these details");
        
        Seller existingSeller = res.get();
        Optional<UserSession> opt = sessionRespository.findByUserId(existingSeller.getSellerId());
        if(opt.isPresent()){
            UserSession user = opt.get();
            if(user.getSessionEndTime().isBefore(LocalDateTime.now())){
                sessionRespository.delete(user);
            }
            else{
                throw new LoginException("User already logged in");
            }
        }

        if(existingSeller.getPassword().equals(seller.getPassword())){
            UserSession newSession = new UserSession();
            newSession.setUserId(existingSeller.getSellerId());
            newSession.setUserType("seller");
            newSession.setSessionStartTime(LocalDateTime.now());
            newSession.setSessionEndTime(LocalDateTime.now().plusHours(1));
            UUID uuid = UUID.randomUUID();
            String token = "seller_"+uuid.toString().split("-")[0];
            newSession.setToken(token);
            return sessionRespository.save(newSession);          
        }
        else{
            throw new LoginException("Password Incorrect");
        }
    }

    public SessionDto logoutSeller(SessionDto sessionDto){
        String token = sessionDto.getToken();

        checkTokenStatus(token);
        Optional<UserSession> opt = sessionRespository.findByToken(token);

        if(!opt.isPresent())
            throw new LoginException("User not logged in.");
        
        UserSession user = opt.get();
        sessionRespository.delete(user);
        return sessionDto;
    }

    public void checkTokenStatus(String token){
        Optional<UserSession> opt = sessionRespository.findByToken(token);
        if(opt.isPresent()){
            UserSession session = opt.get();
            LocalDateTime endTime = session.getSessionEndTime();
            boolean flag = false;
            if(endTime.isBefore(LocalDateTime.now())){
                sessionRespository.delete(session);
                flag=true;
            }
            deleteExpiredTokens();
            if(flag)
                throw new LoginException("Session Expired");
            else 
                throw new LoginException("User not logged in");
        }
    }


    public void deleteExpiredTokens(){
        List<UserSession> users = (List<UserSession>)sessionRespository.findAll();
        if(users.size()>0){
            for(UserSession user : users){
                LocalDateTime endTime = user.getSessionEndTime();
                if(endTime.isBefore(LocalDateTime.now())){
                    sessionRespository.delete(user);
                }
            }
        }
    }
}
