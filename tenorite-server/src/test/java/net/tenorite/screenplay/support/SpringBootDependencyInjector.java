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
package net.tenorite.screenplay.support;

import net.serenitybdd.core.di.DependencyInjector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.TestContextManager;

/**
 * @author Johan Siebens
 */
public class SpringBootDependencyInjector implements DependencyInjector {

    @Override
    public void injectDependenciesInto(Object target) {
        if (annotatedWithSpringContext(target)) {
            TestContextManager contextManager = getTestContextManager(target.getClass());
            try {
                contextManager.prepareTestInstance(target);
            }
            catch (Exception e) {
                throw new IllegalStateException("Could not instantiate test instance", e);
            }
        }
    }

    @Override
    public void reset() {

    }

    private boolean annotatedWithSpringContext(Object target) {
        return (AnnotationUtils.findAnnotation(target.getClass(), BootstrapWith.class) != null);
    }

    private TestContextManager getTestContextManager(Class<?> clazz) {
        return new TestContextManager(clazz);
    }

}
