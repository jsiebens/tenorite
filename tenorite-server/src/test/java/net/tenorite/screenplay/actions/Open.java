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
package net.tenorite.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.tenorite.TenoriteServerTest;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.context.embedded.LocalServerPort;

import static net.serenitybdd.screenplay.Tasks.instrumented;

@TenoriteServerTest
public class Open implements Interaction {

    @LocalServerPort
    private int port;

    private String path;

    @Step("{0} opens #path")
    public <T extends Actor> void performAs(T theUser) {
        WebDriver driver = BrowseTheWeb.as(theUser).getDriver();
        driver.get("http://localhost:" + port + path);
    }

    public static Open browserOn(String path) {
        Open open = instrumented(Open.class);
        open.path = path;
        return open;
    }

}
