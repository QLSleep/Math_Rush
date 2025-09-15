package com.example.msbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.msbackend.mapper")
public class MsbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsbackendApplication.class, args);
	}

}
