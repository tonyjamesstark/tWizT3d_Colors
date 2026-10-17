package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FormatterTest {

    @Test
    void isHexAcceptsExactHexCode() {
        assertTrue(Formatter.isHex("&#ff00ff"));
    }

    @Test
    void isHexRejectsTrailingGarbage() {
        assertFalse(Formatter.isHex("&#ff00ffzz"));
    }

    @Test
    void isHexRejectsNonHexMessage() {
        assertFalse(Formatter.isHex("not a hex"));
    }

    @Test
    void hasHexFindsEmbeddedHexCode() {
        assertTrue(Formatter.hasHex("hello &#ff00ff world"));
    }

    @Test
    void hasHexFalseWithoutHexCode() {
        assertFalse(Formatter.hasHex("hello world"));
    }
}
