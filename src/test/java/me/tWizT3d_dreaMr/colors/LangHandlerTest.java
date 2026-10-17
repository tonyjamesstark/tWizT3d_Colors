package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LangHandlerTest {

    @Test
    void replaceKeyInsertsPlayerInputLiterally() {
        assertEquals(
                "Unknown subcommand 'a$1\\b'",
                LangHandler.replaceKey("Unknown subcommand '>arg'", "a$1\\b", "arg"));
    }

    @Test
    void replaceKeyReplacesEveryOccurrence() {
        assertEquals("x and x", LangHandler.replaceKey(">id and >id", "x", "id"));
    }
}
