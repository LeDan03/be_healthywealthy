package vn.edu.stu.PostService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class RecipeServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecipeServiceApplication.class, args);
	}
}
