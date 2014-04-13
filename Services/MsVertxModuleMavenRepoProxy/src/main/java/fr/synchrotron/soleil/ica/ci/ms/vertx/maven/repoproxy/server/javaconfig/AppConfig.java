package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Gregory Boissinot
 */
@Configuration
@ComponentScan("fr.synchrotron.soleil.ica.ci.ms.vertx.maven.repoproxy.server")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("infra.properties"));
        return propertySourcesPlaceholderConfigurer;
    }
}
