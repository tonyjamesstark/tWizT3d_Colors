package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class colorTest {

    @BeforeEach
    void resetState() {
        color.init();
    }

    @Test
    void isColorAcceptsExactSingleHexDigits() {
        for (char c : "0123456789abcdef".toCharArray()) {
            assertTrue(color.isColor(String.valueOf(c)), c + " should be a color");
        }
    }

    @Test
    void isColorRejectsSubstringsAndEmptyAndMultiChar() {
        assertFalse(color.isColor(""));
        assertFalse(color.isColor("ab"));
        assertFalse(color.isColor("abc"));
        assertFalse(color.isColor("g"));
    }

    @Test
    void isColorAcceptsRegisteredCustomCode() {
        color.addColor("j", "ff6600");
        assertTrue(color.isColor("j"));
    }

    @Test
    void getColorStringZeroPadsEachChannel() {
        color.addColor("j", "0a0b0c");
        assertEquals("#0a0b0c", color.getColorString("j"));
    }

    @Test
    void getColorStringHandlesFullByteChannels() {
        color.addColor("j", "ff00ff");
        assertEquals("#ff00ff", color.getColorString("j"));
    }
}
