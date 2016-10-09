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

import net.tenorite.core.Tempo;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Johan Siebens
 */
public final class MongoCollections {

    private final Jongo jongo;

    private Map<String, MongoCollection> collections = new ConcurrentHashMap<>();

    public MongoCollections(Jongo jongo) {
        this.jongo = jongo;
    }

    public MongoCollection getCollection(Tempo tempo, String name) {
        return collections.computeIfAbsent(tempo + ":" + name, jongo::getCollection);
    }

    public void forEach(Consumer<MongoCollection> consumer) {
        collections.values().forEach(consumer);
    }

}
