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
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.CodeValidator;

/**
 * Modulus 11 <b>EC number</b> Check Digit calculation/validation.
 *
 * <p>
 * The European Community number (EC number) is a unique seven-digit identifier 
 * that is assigned to chemical substances. 
 * For example, the EC number of arsenic is 231-148-6: 
 * </p>
 *
 * Check digit calculation is based on <i>modulus 11</i> with digits being weighted
 * based on their position (from left to right).
 * <p>
 * 
 * <p>
 * For further information see
 *  <a href="https://en.wikipedia.org/wiki/European_Community_number">Wikipedia - EC number</a>.
 * </p>
 *
 * @since 1.9
 */
public final class ECNumberCheckDigit extends ModulusCheckDigit {

	private static final long serialVersionUID = 7265356024784308367L;
	
	/** Singleton Check Digit instance */
    public static final CheckDigit EC_CHECK_DIGIT = new ECNumberCheckDigit();

    /**
     * EC number consists of 3 groups of numbers separated dashes (-).
     * Example: dexamethasone is 200-003-9
     */
    private static final String GROUP = "(\\d{3})";
    private static final String DASH = "(?:\\-)";
    static final String EC_REGEX = "^(?:" + GROUP + DASH + GROUP + DASH + "(\\d))$";
    
    private static final int EC_LEN = 7;
    static final CodeValidator REGEX_VALIDATOR = new CodeValidator(EC_REGEX, EC_LEN, null);
    
    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    public ECNumberCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For EC number digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        return (leftPos>=EC_LEN) ? 0 : charValue * leftPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (code == null || code.isEmpty()) {
            throw new CheckDigitException("Code is missing");
        }
        final int modulusResult = calculateModulus(code, false);
        return toCheckDigit(modulusResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        Object cde = REGEX_VALIDATOR.validate(code);
        if(cde instanceof String) {
        	try {
        		final int modulusResult = calculateModulus((String)cde, true);
//        		System.out.println("modulusResult="+modulusResult + " for code "+code);
        		return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
        	} catch (final CheckDigitException ex) {
        		return false;
        	}
        } else {
        	return false;
        }
    }

}
