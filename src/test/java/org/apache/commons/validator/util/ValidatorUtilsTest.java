/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.validator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.Var;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ValidatorUtils}.
 */
class ValidatorUtilsTest {

    @Test
    void testCopyFastHashMap() {
        final FastHashMap original = new FastHashMap();
        original.put("key1", "value1");
        original.put("key2", "value2");
        original.put("key3", "value3");
        original.setFast(true);
        final FastHashMap copy = ValidatorUtils.copyFastHashMap(original);
        assertEquals(original, copy);
    }

    @Test
    void testCopyMap() {
        final Msg msg = new Msg();
        msg.setName("msg");
        final Arg arg = new Arg();
        arg.setName("arg");
        final Var var = new Var("var", "value", null);
        final String plain = "plain";
        final Map<String, Object> original = new HashMap<>();
        original.put("msg", msg);
        original.put("arg", arg);
        original.put("var", var);
        original.put("plain", plain);
        final Map<String, Object> copy = ValidatorUtils.copyMap(original);
        assertEquals(original.size(), copy.size());
        // Msg, Arg and Var values are deep-copied (cloned into new instances).
        assertTrue(copy.get("msg") instanceof Msg);
        assertNotSame(msg, copy.get("msg"));
        assertTrue(copy.get("arg") instanceof Arg);
        assertNotSame(arg, copy.get("arg"));
        assertTrue(copy.get("var") instanceof Var);
        assertNotSame(var, copy.get("var"));
        // Any other value type is shallow-copied (same reference).
        assertSame(plain, copy.get("plain"));
    }

    @Test
    void testReplace() {
        // Key present: replaced.
        assertEquals("Hello there", ValidatorUtils.replace("Hello world", "world", "there"));
        // Key absent: value is unchanged.
        assertEquals("Hello world", ValidatorUtils.replace("Hello world", "xyz", "there"));
        // A null value, key or replacement returns the original value unchanged.
        assertNull(ValidatorUtils.replace(null, "world", "there"));
        assertEquals("Hello world", ValidatorUtils.replace("Hello world", null, "there"));
        assertEquals("Hello world", ValidatorUtils.replace("Hello world", "world", null));
    }
}
