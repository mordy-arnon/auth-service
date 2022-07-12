package com.codect.authService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.codect.authService.db.AclClass;
import com.codect.authService.db.AclClassesRepository;

@SpringBootApplication
public class Main{

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		SpringApplication.run(Main.class);
	}

	@Bean
	public CommandLineRunner initDb(AclClassesRepository classes) {
		return (args) -> {
			if (classes.findAll().size()>0)
				return;
			classes.save(new AclClass((short) 0,"Device"));
			classes.save(new AclClass((short) 1,"Image"));
			classes.save(new AclClass((short) 2,"User"));
			classes.save(new AclClass((short) 3,"UserGroup"));
			
			
		};
	}

}