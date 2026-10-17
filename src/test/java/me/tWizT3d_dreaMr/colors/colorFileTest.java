package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class colorFileTest {

    @BeforeEach
    void resetState() {
        color.init();
    }

    @Test
    void isColorCodeAcceptsExactSingleCharsFromItsOwnAlphabet() {
        for (char c : "0123456789abcdeflominkr".toCharArray()) {
            assertTrue(colorFile.isColorCode(String.valueOf(c)), c + " should be a color code");
        }
    }

    @Test
    void isColorCodeIsCaseInsensitiveForSingleChars() {
        assertTrue(colorFile.isColorCode("A"));
        assertTrue(colorFile.isColorCode("L"));
    }

    @Test
    void isColorCodeRejectsSubstringsAndEmptyAndUnknownChars() {
        assertFalse(colorFile.isColorCode(""));
        assertFalse(colorFile.isColorCode("lo"));
        assertFalse(colorFile.isColorCode("g"));
    }

    @Test
    void isColorCodeFallsBackToRegisteredColors() {
        color.addColor("j", "ff6600");
        assertTrue(colorFile.isColorCode("j"));
    }
}
