/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/example/org/apache/commons/validator/example/ValidateBean.java,v 1.1 2002/10/11 20:17:50 turner Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/11 20:17:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


package org.apache.commons.validator.example;


/**                                                       
 * <p>A simple bean to use with the Validator Example.</p> 
 *
 * @author James Turner
 * @version $Revision: 1.1 $ $Date: 2002/10/11 20:17:50 $
*/                                                       

public class ValidateBean extends Object {

    String lastName, firstName, street1, street2, city, state, postalCode, age;


    public void setLastName(String lastName) {
	this.lastName = lastName;
    }
    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }
    public void setStreet1 (String street1) {
	this.street1 = street1;
    }
    public void setStreet2(String street2) {
	this.street2 = street2;
    }
    public void setCity(String city) {
	this.city = city;
    }
    public void setState(String state) {
	this.state = state;
    }
    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }
    public void setAge (String age) {
	this.age = age;
    }

    public String getLastName() {
	return this.lastName;
    }
    public String getFirstName() {
	return this.firstName;
    }
    public String getStreet1 () {
	return this.street1;
    }
    public String getStreet2() {
	return this.street2;
    }
    public String getCity() {
	return this.city;
    }
    public String getState() {
	return this.state;
    }
    public String getPostalCode() {
	return this.postalCode;
    }
    public String getAge () {
	return this.age;
    }

    public String toString() {
	return "{lastname=" + this.lastName + ", firstname=" + this.firstName + 
	       ", street1=" + this.street1 + ",\n street2=" + this.street2 + ", " +
               "city=" + this.city + ", state=" + this.state + 
	    ",\n postalcode=" + this.postalCode + ", age=" + this.age + "}";
    } 

}
