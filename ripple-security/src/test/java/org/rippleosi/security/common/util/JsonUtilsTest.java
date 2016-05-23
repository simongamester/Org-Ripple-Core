/*
 * Copyright 2016 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.security.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonUtilsTest {

    @Test
    public void shouldNotBeAbleToCreateAnInstanceOfAUtilityClass() throws Exception {
        Class<JsonUtils> clazz = JsonUtils.class;

        assertTrue("class must be final", Modifier.isFinal(clazz.getModifiers()));
        assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);

        final Constructor<?> constructor = clazz.getDeclaredConstructor();

        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            fail("constructor is not private");
        }

        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);

        for (final Method method : clazz.getMethods()) {
            if (method.getDeclaringClass().equals(clazz) && !Modifier.isStatic(method.getModifiers())) {
                fail("there exists a non-static method:" + method);
            }
        }
    }

    @Test
    public void shouldReturnNullJsonNodeFromNullInput() {
        final JsonNode converted = JsonUtils.extractJsonFromString(null);
        assertNull("JSON node should be null with null input.", converted);
    }

    @Test
    public void shouldReturnBaseNodeFromValidJsonInput() {
        final String raw = "{ \"testKey\": \"testValue\" }";
        final JsonNode converted = JsonUtils.extractJsonFromString(raw);

        assertNotNull("JSON node should be null with valid input.", converted);
        assertEquals("Value should equal \"testValue\".", "testValue", converted.get("testKey").asText());
    }
}
