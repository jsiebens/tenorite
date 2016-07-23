package net.tenorite.system.config;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Johan Siebens
 */
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
        DB db = mongoClient.getDB(mongoProperties.getMongoClientDatabase());

        Mapper mapper =
            new JacksonMapper.Builder()
                .registerModule(new Jdk8Module())
                .registerModule(new GuavaModule())
                .setVisibilityChecker(VisibilityChecker.Std.defaultInstance())
                .build();

        return new Jongo(db, mapper);
    }

}
