/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator;

import junit.framework.TestCase;

/**
 * Test <code>Field</code> objects.
 *
 * @version $Revision$
 */
public class FieldTest extends TestCase {


    protected Field field;

    /**
     * FieldTest constructor.
     */
    public FieldTest() {
        super();
    }

    /**
     * FieldTest constructor.
     * @param name
     */
    public FieldTest(String name) {
        super(name);
    }

    /**
     * Test setup
     */
    @Override
    public void setUp() {
        field = new Field();
    }

    /**
     * Test clean up
     */
    @Override
    public void tearDown() {
        field = null;
    }

    /**
     * test Field with no arguments
     */
    public void testEmptyArgs() {

        assertEquals("Empty Args(1) ", 0, field.getArgs("required").length);

    }
    /**
     * test Field with only 'default' arguments, no positions specified.
     */
    public void testDefaultPositionImplied() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-1"));
        field.addArg(createArg("default-position-2"));

        assertEquals("testDefaultPositionImplied(1) ", 3, field.getArgs("required").length);
        assertEquals("testDefaultPositionImplied(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertEquals("testDefaultPositionImplied(3) ", "default-position-1", field.getArg("required", 1).getKey());
        assertEquals("testDefaultPositionImplied(4) ", "default-position-2", field.getArg("required", 2).getKey());

    }

    /**
     * test Field with only 'default' arguments, positions specified.
     */
    public void testDefaultUsingPositions() {

        field.addArg(createArg("default-position-1", 1));
        field.addArg(createArg("default-position-0", 0));
        field.addArg(createArg("default-position-2", 2));

        assertEquals("testDefaultUsingPositions(1) ", 3, field.getArgs("required").length);
        assertEquals("testDefaultUsingPositions(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertEquals("testDefaultUsingPositions(3) ", "default-position-1", field.getArg("required", 1).getKey());
        assertEquals("testDefaultUsingPositions(4) ", "default-position-2", field.getArg("required", 2).getKey());

    }

    /**
     * test Field with only 'default' arguments, position specified for one argument
     */
    public void testDefaultOnePosition() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-2", 2));
        field.addArg(createArg("default-position-3"));

        assertEquals("testDefaultOnePosition(1) ", 4, field.getArgs("required").length);
        assertEquals("testDefaultOnePosition(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertNull("testDefaultOnePosition(3) ", field.getArg("required", 1));
        assertEquals("testDefaultOnePosition(4) ", "default-position-2", field.getArg("required", 2).getKey());
        assertEquals("testDefaultOnePosition(5) ", "default-position-3", field.getArg("required", 3).getKey());

    }

    /**
     * test Field with only 'default' arguments, some position specified.
     */
    public void testDefaultSomePositions() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-2", 2));
        field.addArg(createArg("default-position-3"));
        field.addArg(createArg("default-position-1", 1));

        assertEquals("testDefaultSomePositions(1) ", 4, field.getArgs("required").length);
        assertEquals("testDefaultSomePositions(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertEquals("testDefaultSomePositions(3) ", "default-position-1", field.getArg("required", 1).getKey());
        assertEquals("testDefaultSomePositions(4) ", "default-position-2", field.getArg("required", 2).getKey());
        assertEquals("testDefaultSomePositions(5) ", "default-position-3", field.getArg("required", 3).getKey());

    }

    /**
     * test Field with a 'default' argument overriden using 'position' property
     */
    public void testOverrideUsingPositionA() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-1"));
        field.addArg(createArg("default-position-2"));
        field.addArg(createArg("required-position-1", "required", 1));

        // use 'required' as name
        assertEquals("testOverrideUsingPositionA(1) ", 3, field.getArgs("required").length);
        assertEquals("testOverrideUsingPositionA(2) ", "required-position-1", field.getArg("required", 1).getKey());

        // use 'mask' as name
        assertEquals("testOverrideUsingPositionA(3) ", 3, field.getArgs("mask").length);
        assertEquals("testOverrideUsingPositionA(4) ", "default-position-1", field.getArg("mask", 1).getKey());

        // Get Default
        assertEquals("testOverrideUsingPositionA(5) ", "default-position-1", field.getArg(1).getKey());

    }

    /**
     * test Field with a 'default' argument overriden using 'position' property
     */
    public void testOverrideUsingPositionB() {

        field.addArg(createArg("required-position-3", "required", 3));
        field.addArg(createArg("required-position-1", "required", 1));
        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-1"));
        field.addArg(createArg("default-position-2"));

        // use 'required' as name
        assertEquals("testOverrideUsingPositionB(1) ", 4, field.getArgs("required").length);
        assertEquals("testOverrideUsingPositionB(2) ", "default-position-0",  field.getArg("required", 0).getKey());
        assertEquals("testOverrideUsingPositionB(3) ", "required-position-1", field.getArg("required", 1).getKey());
        assertEquals("testOverrideUsingPositionB(4) ", "default-position-2",  field.getArg("required", 2).getKey());
        assertEquals("testOverrideUsingPositionB(5) ", "required-position-3", field.getArg("required", 3).getKey());

        // use 'mask' as name
        assertEquals("testOverrideUsingPositionB(6) ", 4, field.getArgs("mask").length);
        assertEquals("testOverrideUsingPositionB(6) ", "default-position-0", field.getArg("mask", 0).getKey());
        assertEquals("testOverrideUsingPositionB(7) ", "default-position-1", field.getArg("mask", 1).getKey());
        assertEquals("testOverrideUsingPositionB(8) ", "default-position-2", field.getArg("mask", 2).getKey());
        assertNull("testOverrideUsingPositionB(9) ", field.getArg("mask", 3));

    }

    /**
     * test Field with a 'default' argument overriden without positions specified.
     */
    public void testOverridePositionImplied() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("required-position-1", "required"));
        field.addArg(createArg("required-position-2", "required"));
        field.addArg(createArg("mask-position-1", "mask"));

        // use 'required' as name
        assertEquals("testOverridePositionImplied(1) ", 3, field.getArgs("required").length);
        assertEquals("testOverridePositionImplied(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertEquals("testOverridePositionImplied(3) ", "required-position-1", field.getArg("required", 1).getKey());
        assertEquals("testOverridePositionImplied(4) ", "required-position-2", field.getArg("required", 2).getKey());

        // use 'mask' as name
        assertEquals("testOverridePositionImplied(5) ", 3, field.getArgs("mask").length);
        assertEquals("testOverridePositionImplied(6) ", "default-position-0", field.getArg("mask", 0).getKey());
        assertEquals("testOverridePositionImplied(7) ", "mask-position-1", field.getArg("mask", 1).getKey());
        assertNull("testOverridePositionImplied(8) ", field.getArg("mask", 2));

        // Get Defaults
        assertEquals("testOverridePositionImplied(9) ",  "default-position-0", field.getArg(0).getKey());
        assertNull("testOverridePositionImplied(10) ", field.getArg(1));
        assertNull("testOverridePositionImplied(11) ", field.getArg(2));

    }

    /**
     * test Field with a 'default' argument overriden with some positions specified
     */
    public void testOverrideSomePosition() {

        field.addArg(createArg("default-position-0"));
        field.addArg(createArg("default-position-1"));
        field.addArg(createArg("default-position-2"));
        field.addArg(createArg("required-position-1", "required", 1));
        field.addArg(createArg("required-position-2", "required"));
        field.addArg(createArg("mask-position-3", "mask"));

        // use 'required' as name
        assertEquals("testOverrideSomePosition(1) ", 4, field.getArgs("required").length);
        assertEquals("testOverrideSomePosition(2) ", "default-position-0", field.getArg("required", 0).getKey());
        assertEquals("testOverrideSomePosition(3) ", "required-position-1", field.getArg("required", 1).getKey());
        assertEquals("testOverrideSomePosition(4) ", "required-position-2", field.getArg("required", 2).getKey());
        assertNull("testOverrideSomePosition(5) ", field.getArg("required", 3));

        // use 'mask' as name
        assertEquals("testOverrideSomePosition(6) ", 4, field.getArgs("mask").length);
        assertEquals("testOverrideSomePosition(7) ", "default-position-0", field.getArg("mask", 0).getKey());
        assertEquals("testOverrideSomePosition(8) ", "default-position-1", field.getArg("mask", 1).getKey());
        assertEquals("testOverrideSomePosition(9) ", "default-position-2", field.getArg("mask", 2).getKey());
        assertEquals("testOverrideSomePosition(10) ", "mask-position-3", field.getArg("mask", 3).getKey());

        // Get Defaults
        assertEquals("testOverrideSomePosition(11) ",  "default-position-0", field.getArg(0).getKey());
        assertEquals("testOverrideSomePosition(12) ", "default-position-1", field.getArg(1).getKey());
        assertEquals("testOverrideSomePosition(13) ", "default-position-2", field.getArg(2).getKey());
        assertNull("testOverrideSomePosition(14) ", field.getArg(3));

    }

    /**
     * Convenience Method - create argument (no name or position specified)
     */
    private Arg createArg(String key) {
        Arg arg = new Arg();
        arg.setKey(key);
        return arg; 
    }

    /**
     * Convenience Method - create argument (no name, position specified)
     */
    private Arg createArg(String key, int position) {
        Arg arg = createArg(key);
        arg.setPosition(position);
        return arg; 
    }

    /**
     * Convenience Method - create argument (name specified, no position)
     */
    private Arg createArg(String key, String name) {
        Arg arg = createArg(key);
        arg.setName(name);
        return arg; 
    }

    /**
     * Convenience Method - create argument (name & position specified)
     */
    private Arg createArg(String key, String name, int position) {
        Arg arg = createArg(key, name);
        arg.setPosition(position);
        return arg; 
    }

}
