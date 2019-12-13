package me.next.servicebus.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.JmsTransactionManager;

@EnableJms
@SpringBootApplication
public class NextServiceBusExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NextServiceBusExampleApplication.class, args);
    }

}
