/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/FieldTest.java,v 1.1 2003/09/28 20:39:57 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2003/09/28 20:39:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.validator;

import junit.framework.TestCase;

/**
 * Test <code>Field</code> objects.
 * 
 * @author David Graham
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
