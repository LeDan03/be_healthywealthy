package vn.edu.stu.WebBlogNauAn;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@EnableDiscoveryClient
@EnableScheduling
public class WebBlogNauAnApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBlogNauAnApplication.class, args);
	}

}
