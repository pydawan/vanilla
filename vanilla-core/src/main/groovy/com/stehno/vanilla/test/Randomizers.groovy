/*
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.vanilla.test

/**
 * A collection of useful randomizers for use with the PropertyRandomizer.
 */
class Randomizers {

    private static final String CHARS = 'abcdefghijklmnopqrstuvwxyz0123456789'

    static Closure<String> forString(IntRange sizeRange = (10..20)) {
        return { Random rng ->
            randomString rng, sizeRange
        }
    }

    private static String randomString(Random rng, IntRange sizeRange) {
        int size = sizeRange[rng.nextInt(sizeRange.size())]
        def chars = []
        size.times {
            chars << CHARS[rng.nextInt(CHARS.size())]
        }
        chars.join('')
    }

    static Closure<String[]> forStringArray(Integer size = null, IntRange sizeRange = (10..20)) {
        return { Random rng ->
            randomCollection(rng, size) { randomString(rng, sizeRange) }.toArray()
        }
    }

    static Closure<Long[]> forLongArray(Integer size = null) {
        return { Random rng ->
            randomCollection(rng, size) { rng.nextLong() }.toArray()
        }
    }

    static List randomCollection(Random rng, Integer size = null, Closure generator) {
        int length = size ?: (rng.nextInt(4) + 1)
        def items = []
        length.times {
            items << generator()
        }
        items
    }

    static Closure<Integer> forInteger(Integer bounds = null) {
        return { Random rng ->
            bounds ? rng.nextInt(bounds) : rng.nextInt()
        }
    }

    static Closure<BigDecimal> forBigDecimal(Integer scale = null) {
        return { Random rng ->
            scale ? new BigDecimal(rng.nextLong(), scale) : new BigDecimal(rng.nextLong())
        }
    }

    static Closure<Long> forLong() {
        return { Random rng ->
            rng.nextLong()
        }
    }

    static Closure<Boolean> forBoolean() {
        return { Random rng ->
            rng.nextBoolean()
        }
    }

    static Closure<Byte> forByte() {
        return { Random rng ->
            byte[] bytes = new byte[1]
            rng.nextBytes(bytes)
            bytes[0]
        }
    }

    static Closure<Byte[]> forByteArray(Integer size = null) {
        return { Random rng ->
            def bytes = new byte[size ?: rng.nextInt(25) + 1]
            rng.nextBytes(bytes)
            bytes
        }
    }

    static Closure<Short> forShort() {
        return { Random rng ->
            rng.nextInt() as short
        }
    }

    static Closure<Float> forFloat() {
        return { Random rng ->
            rng.nextFloat()
        }
    }

    static Closure<Double> forDouble() {
        return { Random rng ->
            rng.nextDouble()
        }
    }

    static Closure<Date> forDate() {
        return { Random rng ->
            new Date(rng.nextLong())
        }
    }

    static Closure<Character> forChar() {
        return { Random rng ->
            CHARS[rng.nextInt(CHARS.size())] as char
        }
    }
}
