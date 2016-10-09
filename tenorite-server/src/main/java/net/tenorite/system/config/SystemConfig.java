/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    @Bean
    public MongoCollections mongoCollections(MongoClient mongoClient, MongoProperties mongoProperties) {
        DB db = mongoClient.getDB(mongoProperties.getMongoClientDatabase());

        Mapper mapper =
            new JacksonMapper.Builder()
                .registerModule(new Jdk8Module())
                .registerModule(new GuavaModule())
                .setVisibilityChecker(VisibilityChecker.Std.defaultInstance())
                .build();

        return new MongoCollections(new Jongo(db, mapper));
    }

}
