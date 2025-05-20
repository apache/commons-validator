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
package org.apache.commons.validator;

/**
 * Value object that contains different fields to test type conversion validation.
 */
public class TypeBean {

    private String sByte;
    private String sShort;
    private String sInteger;
    private String sLong;
    private String sFloat;
    private String sDouble;
    private String sDate;
    private String sCreditCard;

    public String getByte() {
        return sByte;
    }

    public String getCreditCard() {
        return sCreditCard;
    }

    public String getDate() {
        return sDate;
    }

    public String getDouble() {
        return sDouble;
    }

    public String getFloat() {
        return sFloat;
    }

    public String getInteger() {
        return sInteger;
    }

    public String getLong() {
        return sLong;
    }

    public String getShort() {
        return sShort;
    }

    public void setByte(final String sByte) {
        this.sByte = sByte;
    }

    public void setCreditCard(final String sCreditCard) {
        this.sCreditCard = sCreditCard;
    }

    public void setDate(final String sDate) {
        this.sDate = sDate;
    }

    public void setDouble(final String sDouble) {
        this.sDouble = sDouble;
    }

    public void setFloat(final String sFloat) {
        this.sFloat = sFloat;
    }

    public void setInteger(final String sInteger) {
        this.sInteger = sInteger;
    }

    public void setLong(final String sLong) {
        this.sLong = sLong;
    }

    public void setShort(final String sShort) {
        this.sShort = sShort;
    }

}
