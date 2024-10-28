package com.movies;

import com.movies.model.Customer;
import com.movies.repository.CustomerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);
		CustomerRepository productRepository = context.getBean(CustomerRepository.class);
		Customer customer = Customer.builder()
				.id(1)
				.nombre("Cliente")
				.apellido("1")
				.email("123@gmail.com")
				.password("123")
				.build();

		System.out.println(customer);
		productRepository.save(customer);
	}

}
