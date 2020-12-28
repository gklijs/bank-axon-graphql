package nl.openweb.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InitializerApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(InitializerApplication.class, args);
        SpringApplication.exit(ctx, () -> 0);
    }
}
