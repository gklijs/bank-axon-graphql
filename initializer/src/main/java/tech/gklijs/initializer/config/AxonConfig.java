package tech.gklijs.initializer.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[]{
                "java.util.**",
                "tech.gklijs.tech.**"
        });
        return xStream;
    }
}
