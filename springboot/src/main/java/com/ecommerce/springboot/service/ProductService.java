package com.ecommerce.springboot.service;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.springboot.exception.LoginException;
import com.ecommerce.springboot.exception.ProductNotFoundException;
import com.ecommerce.springboot.models.CategoryEnum;
import com.ecommerce.springboot.models.Product;
import com.ecommerce.springboot.models.ProductDto;
import com.ecommerce.springboot.models.ProductStatus;
import com.ecommerce.springboot.models.Seller;
import com.ecommerce.springboot.repository.ProductRepository;
import com.ecommerce.springboot.repository.SellerRepository;

@Component
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private SellerRepository sellerRepository;

    public Product addProductToCatalog(String token, Product product) throws LoginException{
        Product prod = null;
        Seller seller1 = sellerService.getCurrentlyLoggedInSeller(token);
        prod.setSeller(seller1);
    
        Seller existingSeller = sellerService.getSellerByMobile(prod.getSeller().getMobile(),token);
        Optional<Seller> opt = sellerRepository.findById(existingSeller.getSellerId());

        if(opt.isPresent()){
            Seller seller = opt.get();
            prod.setSeller(seller);
            prod = productRepository.save(product);
            seller.getProduct().add(prod);
            sellerRepository.save(seller);
        }else{
            prod = productRepository.save(product);
        }
        return prod;
    }

    public Product getProductFromCatalogById(Integer Id) throws ProductNotFoundException{
        Optional<Product> opt = productRepository.findById(Id);
        if(opt.isPresent()){
            return opt.get();
        }else{
            throw new ProductNotFoundException("No existing product");
        }

    }

    public Product deleteProductFromCatalog(Integer Id) throws ProductNotFoundException{
        Optional<Product> opt = productRepository.findById(Id);
        if(opt.isPresent()){
            Product prod = opt.get();
            productRepository.delete(prod);
            return prod;
        }else{
            throw new ProductNotFoundException("No Product exist");
        }
    }
    public Product updateProductInCatalog(Product product){
        Optional<Product> opt = productRepository.findById(product.getProductId());
        if(opt.isPresent()){
            opt.get();
            Product prod = productRepository.save(product);
            return prod;
        }else{
            throw new ProductNotFoundException("No product to update");
        }
        
    }

    public List<Product> getAllProductsInCatalog(){
        List<Product> products = (List<Product>)productRepository.findAll();
        if(products.size()>0)
            return products;
        throw new ProductNotFoundException("No product in catalog");

    }

    public List<ProductDto> getAllProductsOfSeller(Integer id){
        List<ProductDto> products = productRepository.getProductsOfASeller(id);
        if(products.size()>0){
            return products;
        }else{
            throw new ProductNotFoundException("No products with seller id" + id);
        }

    }
    public List<ProductDto> getProductsOfCategory(CategoryEnum cateEnum){
        List<ProductDto> lists = productRepository.getAllProductsInACategory(cateEnum);
        if(lists.size()>0){
            return lists;
        }else{
            throw new ProductNotFoundException("No products found");
        }

    }
    public List<ProductDto> getProductsOfStatus(ProductStatus status){
        List<ProductDto> prod = productRepository.getProductsWithStatus(status);
        if(prod.size()>0){
            return prod;
        }else{
            throw new ProductNotFoundException("No products with given status");
        }
    }

    public Product updateProductQuantityWithId(Integer id, ProductDto productDto){
        Product prod = null;
        Optional<Product> opt = productRepository.findById(id);
            prod = opt.get();
            prod.setQuantity(prod.getQuantity()+productDto.getQuantity());
            if(prod.getQuantity()>0){
                prod.setStatus(ProductStatus.AVAILABLE);
            }
            productRepository.save(prod);
        
        return prod;
    }

}
