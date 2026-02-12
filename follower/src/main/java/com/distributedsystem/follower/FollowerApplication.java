package com.distributedsystem.follower;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
public class FollowerApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FollowerApplication.class);

        // programmatically load XML config into the context
        app.addInitializers((ApplicationContextInitializer<GenericApplicationContext>) ctx -> {
            new XmlBeanDefinitionReader(ctx)
                    .loadBeanDefinitions("classpath:/spring/spring-follower.xml");
        });
        app.run(args);
    }
}
