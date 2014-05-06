package fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server.javaconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gregory Boissinot
 */
@Configuration
@ComponentScan("fr.synchrotron.soleil.ica.ci.ms.vertx.maven.legacyproxy.server")
public class AppConfig {

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer properties() {
//        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//
//        //propertySourcesPlaceholderConfigurer.setPropertySources(new MutablePropertySources());
//
//
//
//        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("infra.properties"));
//        return propertySourcesPlaceholderConfigurer;
//    }
}
