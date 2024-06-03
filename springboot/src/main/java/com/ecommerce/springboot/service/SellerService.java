package com.ecommerce.springboot.service;

import java.util.List;
import java.util.Optional;

import com.ecommerce.springboot.exception.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.springboot.exception.SellerException;
import com.ecommerce.springboot.models.Seller;
import com.ecommerce.springboot.models.SellerDto;
import com.ecommerce.springboot.models.SessionDto;
import com.ecommerce.springboot.models.UserSession;
import com.ecommerce.springboot.repository.SellerRepository;
import com.ecommerce.springboot.repository.SessionRespository;

@Component
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired 
    private LoginLogoutService loginLogoutService;
    @Autowired
    private SessionRespository sessionRespository;

    public Seller addSeller(Seller seller) {
        Seller newSeller = sellerRepository.save(seller);
        return newSeller;
    }

    public List<Seller> getAllSeller() throws SellerException{
        List<Seller> allSellers =(List<Seller>) sellerRepository.findAll();
        if(allSellers.size()>0){
            return allSellers;
        }else{
            throw new SellerException("No sellers found");
        }
    }
    public Seller getSellerById(Integer sellerId) throws SellerException{
        Optional<Seller> existingSeller = sellerRepository.findById(sellerId);
        if(existingSeller.isPresent()){
            return existingSeller.get();
        }else{
            throw new SellerException("Seller not found with this id "+sellerId);
        }

    }
    public Seller getSellerByMobile(String mobile, String token) throws SellerException{
        Optional<Seller> existingSeller = sellerRepository.findByMobile(mobile);
        if(existingSeller.isPresent())
            return existingSeller.get();
        else    
            throw new SellerException("No seller found with given mobile number");

    }
    public Seller getCurrentlyLoggedInSeller(String token) throws SellerException,LoginException{
        if(token.contains("seller")==false)
            throw new LoginException("Invalid session");
        loginLogoutService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Seller existingSeller = sellerRepository.findById(user.getUserId()).orElseThrow(()-> new SellerException("Seller not found"));
        return existingSeller;
    }

    public SessionDto updateSellerPassword(SellerDto sellerDto, String token) throws SellerException,LoginException{

        if(token.contains("seller")==false)
            throw new LoginException("Invalid session");
        
        loginLogoutService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Optional<Seller> opt = sellerRepository.findById(user.getUserId());

        if(opt.isEmpty())
            throw new SellerException("Seller does not exist");
        Seller existingSeller = opt.get();
        if(sellerDto.getMobile().equals(existingSeller.getMobile())==false)
            throw new SellerException("Verification error");
        existingSeller.setPassword(sellerDto.getPassword());
        sellerRepository.save(existingSeller);
        SessionDto session = new SessionDto();
        session.setToken(token);
        loginLogoutService.logoutSeller(session);
        return session;
    }
    public Seller updateSeller(Seller seller, String token) throws SellerException, LoginException{
        if(token.contains("seller") == false)
            throw new LoginException("Invalid session");
        loginLogoutService.checkTokenStatus(token);
        Seller existingSeller = sellerRepository.findById(seller.getSellerId()).orElseThrow(()-> new SellerException("Seller not found for this id" + seller.getSellerId()));
        Seller newSeller = sellerRepository.save(seller);
        return newSeller;

    }
    public Seller updateSellerMobile(SellerDto sellerDto, String token) throws SellerException,LoginException{
        if(token.contains("seller")==false){
            throw new LoginException("Invalid session");
        }
        loginLogoutService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Seller existingSeller = sellerRepository.findById(user.getUserId()).orElseThrow(()-> new SellerException("Seller not found for this ID"));
        if(existingSeller.getPassword().equals(sellerDto.getPassword())){
            existingSeller.setMobile(sellerDto.getMobile());
            return sellerRepository.save(existingSeller);
        }else{
            throw new SellerException("Error occured in updating number");
        }
    }
    public Seller deleteSellerById(Integer sellerId, String token) throws SellerException,LoginException{
        if(token.contains("seller") == false)
        throw new LoginException("Invalid session token for seller");
        loginLogoutService.checkTokenStatus(token);
        
        Optional<Seller> opt = sellerRepository.findById(sellerId);
        if(opt.isPresent()){
            UserSession user = sessionRespository.findByToken(token).get();
            Seller existingSeller = opt.get();
            if(user.getUserId() == existingSeller.getSellerId()){
                sellerRepository.delete(existingSeller);
                SessionDto session = new SessionDto();
                loginLogoutService.logoutSeller(session);
                return existingSeller;
            }else{
                throw new SellerException("Verification error in deleting seller account");
            }    
        }
        else
            throw new SellerException("Seller not found for this id" + sellerId);
    }
    
}
