package net.tenorite.system.config;

import akka.actor.ActorSystem;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig {

    @Bean(destroyMethod = "shutdown")
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    public Jongo jongo() {
        return new Jongo(mongoClient.getDB(mongoProperties.getMongoClientDatabase()));
    }

}
