package com.ecommerce.springboot.service;

import com.ecommerce.springboot.exception.CartItemNotFound;
import com.ecommerce.springboot.exception.CustomerNotFoundException;
import com.ecommerce.springboot.exception.LoginException;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.springboot.models.Cart;
import com.ecommerce.springboot.models.CartDto;
import com.ecommerce.springboot.models.CartItem;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.Product;
import com.ecommerce.springboot.models.UserSession;
import com.ecommerce.springboot.repository.CartItemRepository;
import com.ecommerce.springboot.repository.CartRepository;
import com.ecommerce.springboot.repository.CustomerRepository;
import com.ecommerce.springboot.repository.SessionRespository;
import com.ecommerce.springboot.repository.ProductRepository;
import com.ecommerce.springboot.service.LoginLogoutService;

@Component
public class CartService {
     
    @Autowired 
    private CartRepository cartRepository;
    @Autowired
    private SessionRespository sessionRespository;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoginLogoutService loginService;
    @Autowired 
    private ProductRepository productRepository;


    public Cart addProduct(CartDto cartDto, String token){
        if(token.contains("customer")==false){
            throw new LoginException("Invalid session token for customer");
        }
        loginService.checkTokenStatus(token);

        UserSession user = sessionRespository.findByToken(token).get();
        Optional<Customer> opt = customerRepository.findById(user.getUserId());
        
        if(opt.isEmpty())
            throw new CustomerNotFoundException("Customer doesn't exist");
        Customer existingCustomer = opt.get();
        Cart customerCart = existingCustomer.getCustomerCart();
        List<CartItem> cartItems = customerCart.getCartItems();
        CartItem item = cartItemService.createItemForCart(cartDto);
        if(cartItems.size()==0){
            cartItems.add(item);
            customerCart.setCartTotal(item.getCartProduct().getPrice());
        }else{
            boolean flag = false;
            for(CartItem c : cartItems){
                if(c.getCartProduct().getProductId()==cartDto.getProductId()){
                    c.setCartItemQuantity(c.getCartItemQuantity()+1);
                    customerCart.setCartTotal(customerCart.getCartTotal()+c.getCartProduct().getPrice());
                    flag = true;
                }
            }
            if(!flag){
                cartItems.add(item);
                customerCart.setCartTotal(customerCart.getCartTotal()+item.getCartProduct().getPrice());
            }
        }

        return cartRepository.save(existingCustomer.getCustomerCart());

    }

    public Cart getCartProduct(String token){
        if(token.contains("customer")==false){
            throw new LoginException("Invalid session token");
        }
        loginService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Optional<Customer> opt = customerRepository.findById(user.getUserId());
        if(opt.isEmpty()){
            throw new CustomerNotFoundException("Customer does not exist");
        }
        Customer existingCustomer = opt.get();

        Integer cartId = existingCustomer.getCustomerCart().getCartId();
        Optional<Cart> optCart = cartRepository.findById(cartId);
        if(optCart.isEmpty())
            throw new CartItemNotFound("Cart item not found for this id");
        return optCart.get();
    }

    public Cart removeProductFromCart(CartDto cartDto, String token){
        if(token.contains("customer")==false){
            throw new LoginException("Invalid session token");
        }
        loginService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Optional<Customer> opt = customerRepository.findById(user.getUserId());

        if(opt.isEmpty()){
            throw new CustomerNotFoundException("No user exist");
        }
        Customer existingCustomer = opt.get();
        Cart customerCart = existingCustomer.getCustomerCart();
        List<CartItem> cartItems = customerCart.getCartItems();
        if(cartItems.size()==0){
            throw new CartItemNotFound("Cart is Empty");
        }

        boolean flag = false;

        for(CartItem c: cartItems){
            if(c.getCartProduct().getProductId()==cartDto.getProductId()){
                c.setCartItemQuantity(c.getCartItemQuantity()-1);
                customerCart.setCartTotal(customerCart.getCartTotal() - c.getCartProduct().getPrice());

                if(c.getCartItemQuantity() == 0){
                    cartItems.remove(c);
                    return cartRepository.save(customerCart);
                }
                flag = true;
            }
        }
        if(!flag){
            throw new CartItemNotFound("Prodcut not added to cart");
        }
        if(cartItems.size()==0){
            cartRepository.save(customerCart);
            throw new CartItemNotFound("Cart is empty now");
        }
        return cartRepository.save(customerCart);
    }


    public Cart clearCart(String token){
        if(token.contains("customer")==false){
            throw new LoginException("Invalid session");
        }

        loginService.checkTokenStatus(token);
        UserSession user = sessionRespository.findByToken(token).get();
        Optional<Customer> opt = customerRepository.findById(user.getUserId());
        if(opt.isEmpty())
            throw new CustomerNotFoundException("Customer does not exist");
        Customer existingCustomer = opt.get();
        Cart customerCart = existingCustomer.getCustomerCart();
        if(customerCart.getCartItems().size()==0){
            throw new CartItemNotFound("Cart already empty");
        }
        List<CartItem> emptyCart = new ArrayList<>();
        customerCart.setCartItems(emptyCart);
        customerCart.setCartTotal(0.0);
        return cartRepository.save(customerCart);

    }
}
