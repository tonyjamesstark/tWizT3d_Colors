package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandArgsTest {

    @Test
    void validateColormeAcceptsGoodArgs() {
        assertNull(CommandArgs.validateColorme(new String[] {"sunset", "ff6600"}));
    }

    @Test
    void validateColormeRejectsWrongArgCount() {
        CommandArgs.ColormeError e = CommandArgs.validateColorme(new String[] {"sunset"});
        assertEquals(CommandArgs.ColormeErrorType.ARG_COUNT, e.type());
    }

    @Test
    void validateColormeRejectsEmptyCode() {
        CommandArgs.ColormeError e = CommandArgs.validateColorme(new String[] {"", "ff6600"});
        assertEquals(CommandArgs.ColormeErrorType.EMPTY_CODE, e.type());
    }

    @Test
    void validateColormeRejectsVanillaColorCode() {
        CommandArgs.ColormeError e = CommandArgs.validateColorme(new String[] {"a", "ff6600"});
        assertEquals(CommandArgs.ColormeErrorType.VANILLA_CODE, e.type());
    }

    @Test
    void validateColormeRejectsVanillaFormatCode() {
        CommandArgs.ColormeError e = CommandArgs.validateColorme(new String[] {"l", "ff6600"});
        assertEquals(CommandArgs.ColormeErrorType.VANILLA_CODE, e.type());
    }

    @Test
    void validateColormeRejectsBadHex() {
        CommandArgs.ColormeError e =
                CommandArgs.validateColorme(new String[] {"sunset", "#ff6600"});
        assertEquals(CommandArgs.ColormeErrorType.BAD_HEX, e.type());
        assertEquals("#ff6600", e.arg());
    }

    @Test
    void isVanillaCodeIsCaseInsensitive() {
        assertTrue(CommandArgs.isVanillaCode("A"));
        assertTrue(CommandArgs.isVanillaCode("R"));
    }

    @Test
    void validateGradAcceptsTwoHex() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "1", "3"}, 5);
        assertTrue(r.ok());
        assertEquals(2, r.parsed().hexes().size());
        assertEquals(1, r.parsed().start());
        assertEquals(3, r.parsed().end());
    }

    @Test
    void validateGradAcceptsThreeHex() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(
                        new String[] {"#ff0000", "#00ff00", "#0000ff", "1", "4"}, 5);
        assertTrue(r.ok());
        assertEquals(3, r.parsed().hexes().size());
    }

    @Test
    void validateGradRejectsWrongArgCount() {
        CommandArgs.GradResult r = CommandArgs.validateGrad(new String[] {"#ff0000"}, 5);
        assertEquals(CommandArgs.GradErrorType.ARG_COUNT, r.error().type());
    }

    @Test
    void validateGradRejectsHexWithoutHash() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"ff0000", "#0000ff", "1", "3"}, 5);
        assertEquals(CommandArgs.GradErrorType.BAD_HEX, r.error().type());
        assertEquals("ff0000", r.error().arg());
    }

    @Test
    void validateGradRejectsTrailingGarbageHex() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000zz", "#0000ff", "1", "3"}, 5);
        assertEquals(CommandArgs.GradErrorType.BAD_HEX, r.error().type());
    }

    @Test
    void validateGradRejectsNonNumberLine() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "one", "3"}, 5);
        assertEquals(CommandArgs.GradErrorType.BAD_NUMBER, r.error().type());
        assertEquals("one", r.error().arg());
    }

    @Test
    void validateGradRejectsZeroLine() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "0", "3"}, 5);
        assertEquals(CommandArgs.GradErrorType.LINE_OUT_OF_RANGE, r.error().type());
    }

    @Test
    void validateGradRejectsNegativeLine() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "-1", "3"}, 5);
        assertEquals(CommandArgs.GradErrorType.LINE_OUT_OF_RANGE, r.error().type());
    }

    @Test
    void validateGradRejectsLineAboveLoreSize() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "1", "6"}, 5);
        assertEquals(CommandArgs.GradErrorType.LINE_OUT_OF_RANGE, r.error().type());
        assertEquals("6", r.error().arg());
    }

    @Test
    void validateGradAllowsStartAfterEnd() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "3", "1"}, 5);
        assertTrue(r.ok());
    }

    @Test
    void validateGradRejectsThreeHexOnASingleLine() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(
                        new String[] {"#ff0000", "#00ff00", "#0000ff", "2", "2"}, 5);
        assertEquals(CommandArgs.GradErrorType.RANGE_TOO_SMALL, r.error().type());
    }

    @Test
    void validateGradAllowsTwoHexOnASingleLine() {
        CommandArgs.GradResult r =
                CommandArgs.validateGrad(new String[] {"#ff0000", "#0000ff", "2", "2"}, 5);
        assertTrue(r.ok());
    }
}
