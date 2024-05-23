package com.ecommerce.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import com.ecommerce.springboot.models.Address;


public interface AddressRepository extends CrudRepository<Address,Integer> {

}
