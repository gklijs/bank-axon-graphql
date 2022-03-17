package tech.gklijs.graphql_endpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GraphqlEndpointApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlEndpointApplication.class, args);
    }
}
