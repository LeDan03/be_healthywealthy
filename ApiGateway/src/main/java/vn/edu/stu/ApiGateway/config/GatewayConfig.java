package vn.edu.stu.ApiGateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class GatewayConfig {

    // @Bean
    // RouteLocator customRoutesLocator(RouteLocatorBuilder builder) {
    //     return builder.routes()
    //             .route("account_recipe_aggregation", r->r
    //                     .path("/account-recipes/**")
    //                     .filters(f->f.filter(accountRecipesAggregationFilter))
    //                     .uri("no://op")) //Không cần service, filter đã trả response
    //             .build();
    // }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowedOrigins(List.of("http://localhost:5173","http://192.168.89.156:5173")); // Hoặc "*", nếu đang dev
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
