package com.distributedsystem.node;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class NodeApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NodeApplication.class);
        // programmatically load XML config into the context
        app.addInitializers((ApplicationContextInitializer<GenericApplicationContext>) ctx -> {
            new XmlBeanDefinitionReader(ctx)
                    .loadBeanDefinitions("classpath:/spring/spring-node.xml");
        });
        app.run(args);
    }
}

