/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/example/org/apache/commons/validator/example/ValidateBean.java,v 1.4 2004/02/21 17:10:29 rleland Exp $
 * $Revision: 1.4 $
 * $Date: 2004/02/21 17:10:29 $
 *
 * ====================================================================
 * Copyright 2000-2004 The Apache Software Foundation
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

package org.apache.commons.validator.example;

/**                                                       
 * A simple bean to use with the Validator Example.
 */
public class ValidateBean extends Object {

    String lastName, firstName, street1, street2, city, state, postalCode, age;

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setStreet1(String street1) {
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
    
    public void setAge(String age) {
        this.age = age;
    }

    public String getLastName() {
        return this.lastName;
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public String getStreet1() {
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
    
    public String getAge() {
        return this.age;
    }

    public String toString() {
        return "{lastname="
            + this.lastName
            + ", firstname="
            + this.firstName
            + ", street1="
            + this.street1
            + ",\n street2="
            + this.street2
            + ", "
            + "city="
            + this.city
            + ", state="
            + this.state
            + ",\n postalcode="
            + this.postalCode
            + ", age="
            + this.age
            + "}";
    }

}
