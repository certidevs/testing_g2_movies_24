package com.movies;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);
		CustomerRepository productRepository = context.getBean(CustomerRepository.class);
		Customer customer = Customer.builder()
				.id(1L)
				.nombre("Cliente")
				.apellido("1")
				.email("123@gmail.com")
				.password("123")
				.build();

		Customer customer2 = Customer.builder()
				.id(2L)
				.nombre("Cliente")
				.apellido("2")
				.email("456@gmail.com")
				.password("456")
				.build();

		productRepository.saveAll(List.of(customer, customer2));

		MovieRepository movieRepository = context.getBean(MovieRepository.class);
		Movie movie = Movie.builder()
				.id(1L)
				.name("Pelicula")
				.duration(60)
				.year(2021)
				.build();

		System.out.println(movie);
		movieRepository.save(movie);

		ValoracionRepository valoracionRepository = context.getBean(ValoracionRepository.class);
		Valoracion valoracion = Valoracion.builder()
				.movie(movie)
				.customer(customer)
				.id(2)
				.puntuacion(5)
				.comentario("Comentario dummy")
				.build();
	}

}
