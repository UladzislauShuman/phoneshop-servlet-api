package com.es.phoneshop.model.product.utils;

import com.es.phoneshop.utils.PhoneValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "+375291111111",
            "375252222222",
            "80443333333",
            "+375334444444",
            "80291234567",
            "375291234567",
            "80251234567",
            "+375441234567",
            "375331234567",
    })
    void isValidNumber_validNumbersParameterized(String number) {
        assertTrue(PhoneValidator.isValidNumber(number));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+37529111111",
            "3752522222222",
            "8044333333",
            "+37533444444",
            "8029123456",
            "37529123456",
            "802512345",
            "+375441234",
            "37533123",
            "12345678901",
            "+375301234567"
    })
    void isValidNumber_invalidNumbersParameterized(String number){
        assertFalse(PhoneValidator.isValidNumber(number));
    }

    @Test
    void isValidNumber_validBelarusianNumberWithDashesAndSpaces() {
        assertTrue(PhoneValidator.isValidNumber("+375 (29) 123-45-67"));
        assertTrue(PhoneValidator.isValidNumber("375 29 123 45 67"));
        assertTrue(PhoneValidator.isValidNumber("80 29-123-45-67"));
    }

    @Test
    void isValidNumber_nullNumber() {
        assertThrows(NullPointerException.class, () -> PhoneValidator.isValidNumber(null));
    }

    @Test
    void isValidNumber_emptyNumber() {
        assertFalse(PhoneValidator.isValidNumber(""));
    }
}
