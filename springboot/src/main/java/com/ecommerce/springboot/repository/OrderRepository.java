package com.ecommerce.springboot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.Order;
import java.util.List;
import java.time.LocalDate;


public interface OrderRepository extends CrudRepository<Order, Integer>{

    public List<Order> findByDate(LocalDate date);
    @Query("select c from Customer c where c.customerId = customerId")
	public Customer getCustomerByOrderId(@Param("customerId") Integer customerId);
}
