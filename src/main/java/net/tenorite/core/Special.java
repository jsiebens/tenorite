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

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Johan Siebens
 */
public enum Special {

    ADDLINE(1, 'a', true),
    CLEARLINE(2, 'c', false),
    NUKEFIELD(3, 'n', false),
    RANDOMCLEAR(4, 'r', true),
    SWITCHFIELD(5, 's', true),
    CLEARSPECIAL(6, 'b', true),
    GRAVITY(7, 'g', false),
    QUAKEFIELD(8, 'q', true),
    BLOCKBOMB(9, 'o', true);

    private final int number;

    private final char letter;

    private final boolean offensive;

    Special(int number, char letter, boolean offensive) {
        this.number = number;
        this.letter = letter;
        this.offensive = offensive;
    }

    public int getNumber() {
        return number;
    }

    public char getLetter() {
        return letter;
    }

    public boolean isOffensive() {
        return offensive;
    }

    public boolean isDefensive() {
        return !offensive;
    }

    public static Optional<Special> valueOf(char letter) {
        return Arrays.stream(values()).filter(s -> s.letter == letter).findFirst();
    }

}
