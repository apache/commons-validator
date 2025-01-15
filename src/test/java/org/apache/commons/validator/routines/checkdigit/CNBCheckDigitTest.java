package org.apache.commons.validator.routines.checkdigit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CNBCheckDigitTest {

    private final CheckDigit cnbCheckDigit = CNBCheckDigit.CNB_CHECK_DIGIT;

    @ParameterizedTest
    @ValueSource(strings = {
            "CZ4907100000000000123457",
            "CZ3507100000190000123457"
    })
    void shouldBeValid(String iban) {
        assertTrue(cnbCheckDigit.isValid(iban));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CZ3507100000170000123457",
            "CZ4907100000190000123459",

    })
    void shouldNotBeValid(String iban) {
        assertFalse(cnbCheckDigit.isValid(iban));
    }

}