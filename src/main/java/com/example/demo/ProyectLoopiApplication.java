package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectLoopiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectLoopiApplication.class, args);
		
        System.setProperty("java.net.preferIPv4Stack", "true");
		
		SpringApplication.run(ProyectLoopiApplication.class, args);
	}

}
