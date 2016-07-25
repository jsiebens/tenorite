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
package net.tenorite.core;

/**
 * @author Johan Siebens
 */
public enum Block {

    LINE(1),
    SQUARE(2),
    LEFTL(3),
    RIGHTL(4),
    LEFTZ(5),
    RIGHTZ(6),
    HALFCROSS(7);

    private int number;

    Block(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
