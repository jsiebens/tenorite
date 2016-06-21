package net.tenorite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("net.tenorite")
@EnableAutoConfiguration
public class TenoriteServer {

    public static void main(String[] args) {
        SpringApplication.run(TenoriteServer.class, args);
    }

}
