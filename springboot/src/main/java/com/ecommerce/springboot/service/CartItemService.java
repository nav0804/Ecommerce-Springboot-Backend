package com.ecommerce.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.springboot.exception.ProductNotFoundException;
import com.ecommerce.springboot.models.CartDto;
import com.ecommerce.springboot.models.CartItem;
import com.ecommerce.springboot.models.Product;
import com.ecommerce.springboot.models.ProductStatus;
import com.ecommerce.springboot.repository.ProductRepository;

@Component
public class CartItemService {

    
    @Autowired
    private ProductRepository productRepository;

    public CartItem createItemForCart(CartDto cartDto){
        Product existingProduct = productRepository.findById(cartDto.getProductId()).orElseThrow(()-> new ProductNotFoundException("Product not found"));

        if(existingProduct.getStatus().equals(ProductStatus.OUTOFSTOCK)||existingProduct.getQuantity()==0){
            throw new ProductNotFoundException("Product out of stock");
        }
        CartItem newItem = new CartItem();
        newItem.setCartItemQuantity(1);
        newItem.setCartProduct(existingProduct);

        return newItem;
    }
}
