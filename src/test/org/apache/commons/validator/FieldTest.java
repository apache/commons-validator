/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/FieldTest.java,v 1.3 2004/02/21 17:10:30 rleland Exp $
 * $Revision: 1.3 $
 * $Date: 2004/02/21 17:10:30 $
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

	public void testGetArgs() {
		// Arg with a validator name
		Arg a = new Arg();
		a.setKey("my.resource.key");
		a.setName("required");

		// Arg without name so it will be stored under default name
		Arg a2 = new Arg();
		a2.setKey("another.resource.key");
		a2.setPosition(1);

		Field f = new Field();
        // test empty args first
        Arg[] emptyArgs = f.getArgs("required");
        assertEquals(0, emptyArgs.length);
        
        // add args for other tests
		f.addArg(a);
		f.addArg(a2);

		// test arg lookup for "required" validator
		Arg[] args = f.getArgs("required");
		assertEquals(2, args.length);
		assertTrue(args[0] == a);

		// test arg lookup for non-existent "required2" validator
		// should find default arg for position 1
		Arg[] args2 = f.getArgs("required2");
		assertEquals(2, args2.length);
		assertNull(args2[0]); // we didn't define a 0 position arg
		assertTrue(args2[1] == a2);

	}

}
