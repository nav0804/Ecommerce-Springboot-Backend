package com.ecommerce.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ecommerce.springboot.models.CategoryEnum;
import com.ecommerce.springboot.models.Product;
import com.ecommerce.springboot.models.ProductDto;
import com.ecommerce.springboot.models.ProductStatus;

public interface ProductRepository extends CrudRepository<Product,Integer> {

    @Query("select new com.ecommerce.springboot.models.ProductDto(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.category=:catenum")
	public List<ProductDto> getAllProductsInACategory(@Param("catenum") CategoryEnum catenum);
	
	@Query("select new com.ecommerce.springboot.models.ProductDto(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.status=:status")
	public List<ProductDto> getProductsWithStatus(@Param("status") ProductStatus status);
	
	@Query("select new com.ecommerce.springboot.models.ProductDto(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.seller.sellerId=:id")
	public List<ProductDto> getProductsOfASeller(@Param("id") Integer id);
}
