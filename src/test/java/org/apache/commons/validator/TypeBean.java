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

/**                                                       
 * Value object that contains different fields to test type conversion 
 * validation. 
 *
 * @version $Revision$
 */
public class TypeBean {

    private String sByte = null;
    private String sShort = null;
    private String sInteger = null;
    private String sLong = null;
    private String sFloat = null;
    private String sDouble = null;
    private String sDate = null;
    private String sCreditCard = null;

    public String getByte() {
        return sByte;
    }
    
    public void setByte(String sByte) {
        this.sByte = sByte;
    }
    
    public String getShort() {
        return sShort;
    }
    
    public void setShort(String sShort) {
        this.sShort = sShort;
    }

    public String getInteger() {
        return sInteger;
    }
    
    public void setInteger(String sInteger) {
        this.sInteger = sInteger;
    }

    public String getLong() {
        return sLong;
    }
    
    public void setLong(String sLong) {
        this.sLong = sLong;
    }

    public String getFloat() {
        return sFloat;
    }
    
    public void setFloat(String sFloat) {
        this.sFloat = sFloat;
    }

    public String getDouble() {
        return sDouble;
    }
    
    public void setDouble(String sDouble) {
        this.sDouble = sDouble;
    }

    public String getDate() {
        return sDate;
    }
    
    public void setDate(String sDate) {
        this.sDate = sDate;
    }

    public String getCreditCard() {
        return sCreditCard;
    }
    
    public void setCreditCard(String sCreditCard) {
        this.sCreditCard = sCreditCard;
    }

}
