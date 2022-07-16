package tech.gklijs.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tech.gklijs.initializer.config.AxonConfig;

@SpringBootApplication
@Import({AxonConfig.class})
public class InitializerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InitializerApplication.class, args);
    }
}
