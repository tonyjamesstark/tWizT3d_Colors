package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class gradientItemTest {

    @Test
    void numesSizeIsStepsPlusOne() {
        ArrayList<Integer> out = gradientItem.numes(255, 0, 4);
        assertEquals(5, out.size());
    }

    @Test
    void numesLastElementIsExactlyA() {
        ArrayList<Integer> out = gradientItem.numes(255, 0, 4);
        assertEquals(255, out.get(out.size() - 1));
    }

    @Test
    void numesFirstElementIsExactlyB() {
        ArrayList<Integer> out = gradientItem.numes(255, 0, 4);
        assertEquals(0, out.get(0));
    }

    @Test
    void numesWithEqualEndpointsRepeatsA() {
        ArrayList<Integer> out = gradientItem.numes(10, 10, 3);
        assertEquals(4, out.size());
        for (int v : out) {
            assertEquals(10, v);
        }
    }

    @Test
    void graMeSizeIsGarsizePlusOne() {
        ArrayList<String> out = gradientItem.GraMe("ff0000", "0000ff", 4);
        assertEquals(5, out.size());
    }

    private static final String[] PINK_YELLOW = {"#ff00ff", "#ffff00"};

    @Test
    void gradStringKeepsEmojiIntact() {
        String out = gradientItem.gradString(PINK_YELLOW, "hi \uD83D\uDE00 yo");
        assertEquals("hi \uD83D\uDE00 yo", net.md_5.bungee.api.ChatColor.stripColor(out));
    }

    @Test
    void gradStringColoursPastABareAmpersand() {
        String out = gradientItem.gradString(PINK_YELLOW, "fish & chips");
        assertEquals("fish & chips", net.md_5.bungee.api.ChatColor.stripColor(out));
        assertEquals(12, out.split("\u00a7x").length - 1);
    }

    @Test
    void gradStringStopsAtAColourCode() {
        String out = gradientItem.gradString(PINK_YELLOW, "ab&cde");
        assertEquals(2, out.split("\u00a7x").length - 1);
        assertEquals("&cde", out.substring(out.indexOf('&')));
    }
}
