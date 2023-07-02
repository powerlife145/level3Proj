package com.sparta.blogproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class BlogProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogProjApplication.class, args);
	}

}
