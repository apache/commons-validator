/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/FieldTest.java,v 1.4 2004/11/12 16:02:52 niallp Exp $
 * $Revision: 1.4 $
 * $Date: 2004/11/12 16:02:52 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
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

package org.apache.commons.validator;

import junit.framework.TestCase;

/**
 * Test <code>Field</code> objects.
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
    public void setUp() {
        field = new Field();
    }

    /**
     * Test clean up
     */
    public void tearDown() {
        field = null;
    }

    /**
     * test Field with no arguments
     */
    public void testEmptyArgs() {

        assertEquals("Empty Args(1) ", 0, field.getArgs("required").length);
        assertEquals("Empty Args(2) ", 0, field.getArgs("required").length);

    }

    /**
     * test Field with only 'default' arguments
     */
    public void testDefaultArgs() {

        field.addArg(defaultArg("position-0"));
        field.addArg(defaultArg("position-1"));
        field.addArg(defaultArg("position-2"));

        assertEquals("testDefaultArgs(1) ", 3, field.getArgs("required").length);
        assertEquals("testDefaultArgs(2) ", "position-0", field.getArg("required", 0).getKey());
        assertEquals("testDefaultArgs(3) ", "position-1", field.getArg("required", 1).getKey());
        assertEquals("testDefaultArgs(4) ", "position-2", field.getArg("required", 2).getKey());

    }

    /**
     * test Field with a 'default' argument overriden using 'position' property
     */
    public void testOverrideUsingPositionA() {

        field.addArg(defaultArg("position-0"));
        field.addArg(defaultArg("position-1"));
        field.addArg(defaultArg("position-2"));
        field.addArg(overrideArg("override-position-1", "required", 1));

        // use 'required' as name
        assertEquals("testOverrideUsingPositionA(1) ", 3, field.getArgs("required").length);
        assertEquals("testOverrideUsingPositionA(2) ", "override-position-1", field.getArg("required", 1).getKey());

        // use 'mask' as name
        assertEquals("testOverrideUsingPositionA(3) ", 3, field.getArgs("mask").length);
        assertEquals("testOverrideUsingPositionA(4) ", "position-1", field.getArg("mask", 1).getKey());

        // Get Default
        assertEquals("testOverrideUsingPositionA(5) ", "position-1", field.getArg(1).getKey());

    }

    /**
     * test Field with a 'default' argument overriden using 'position' property
     */
    public void testOverrideUsingPositionB() {

        field.addArg(overrideArg("override-position-1", "required", 1));
        field.addArg(defaultArg("position-0"));
        field.addArg(defaultArg("position-1"));
        field.addArg(defaultArg("position-2"));

        // use 'required' as name
        assertEquals("testOverrideUsingPositionB(1) ", 3, field.getArgs("required").length);
        assertEquals("testOverrideUsingPositionB(2) ", "override-position-1", field.getArg("required", 1).getKey());

        // use 'mask' as name
        assertEquals("testOverrideUsingPositionB(3) ", 3, field.getArgs("mask").length);
        assertEquals("testOverrideUsingPositionB(4) ", "position-1", field.getArg("mask", 1).getKey());

        // Get Default
        assertEquals("testOverrideUsingPositionB(5) ", "position-1", field.getArg(1).getKey());

    }

    /**
     * test Field with a 'default' argument overriden by adding immediately
     * after the default argument is added.
     */
    public void testOverridePositionImplied() {

        field.addArg(defaultArg("position-0"));
        field.addArg(defaultArg("position-1"));
        field.addArg(overrideArg("override-position-1", "required"));
        field.addArg(defaultArg("position-2"));

        // use 'required' as name
        assertEquals("testOverridePositionImplied(1) ", 3, field.getArgs("required").length);
        assertEquals("testOverridePositionImplied(2) ", "override-position-1", field.getArg("required", 1).getKey());

        // use 'mask' as name
        assertEquals("testOverridePositionImplied(3) ", 3, field.getArgs("mask").length);
        assertEquals("testOverridePositionImplied(4) ", "position-1", field.getArg("mask", 1).getKey());

        // Get Default
        assertEquals("testOverridePositionImplied(5) ", "position-1", field.getArg(1).getKey());

    }

    /**
     * Convenience Method - create 'default' argument (i.e. no name)
     */
    private Arg defaultArg(String key) {
        Arg arg = new Arg();
        arg.setKey(key);
        return arg; 
    }

    /**
     * Convenience Method - create 'overriden' argument (name specified, no position)
     */
    private Arg overrideArg(String key, String name) {
        Arg arg = new Arg();
        arg.setKey(key);
        arg.setName(name);
        return arg; 
    }

    /**
     * Convenience Method - create 'overriden' argument (name & position specified)
     */
    private Arg overrideArg(String key, String name, int position) {
        Arg arg = overrideArg(key, name);
        arg.setPosition(position);
        return arg; 
    }

}
