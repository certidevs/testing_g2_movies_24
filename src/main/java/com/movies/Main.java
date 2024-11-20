package com.movies;

import com.movies.model.Categoria;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CategoriaRepository;
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
				.id(6L)
				.nombre("Cliente")
				.apellido("1")
				.email("123@gmail.com")
				.password("123")
				.build();

		Customer customer2 = Customer.builder()
				.id(7L)
				.nombre("Cliente")
				.apellido("2")
				.email("456@gmail.com")
				.password("456")
				.build();

		productRepository.saveAll(List.of(customer, customer2));

		CategoriaRepository categoriaRepository = context.getBean(CategoriaRepository.class);
		Categoria categoria = Categoria.builder()
				.id(6L)
				.nombre("Categoria")
				.descripcion("Descripcion")
				.build();
		categoriaRepository.save(categoria);

		MovieRepository movieRepository = context.getBean(MovieRepository.class);
		Movie movie = Movie.builder()
				.id(6L)
				.name("Pelicula")
				.duration(60)
				.year(2021)
				.categoria(categoria)
				.build();

		System.out.println(movie);
		movieRepository.save(movie);

		ValoracionRepository valoracionRepository = context.getBean(ValoracionRepository.class);
		Valoracion valoracion = Valoracion.builder()
				.movie(movie)
				.customer(customer)
				.id(11L)
				.puntuacion(5)
				.comentario("Comentario dummy")
				.build();
		valoracionRepository.save(valoracion);
	}

}
//TODO: BUG : LA TABLA CUSTOMER_MOVIES SE DUPLICA EN MYSQL AL ARRANCAR LA APP
//todo: SE DUPLICA CUSTOMER_ID Y MOVIES_ID