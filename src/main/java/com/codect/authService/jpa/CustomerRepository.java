package com.codect.authService.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.codect.authService.entities.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

	List<Customer> findByLastName(String lastName);

	Customer findById(long id);
}