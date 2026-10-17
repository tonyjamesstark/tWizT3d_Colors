package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColorCatalogTest {

    @BeforeEach
    void resetColors() {
        color.init();
        color.addColor("j", "ff6600");
        color.addColor("rnd", "random");
        color.addGrad("gr", "ff00ff ffff00");
    }

    private static Predicate<String> allow(String... perms) {
        Set<String> granted = Set.of(perms);
        return granted::contains;
    }

    @Test
    void vanillaOptionsCoverAllSixteenCodes() {
        List<ColorOption> vanilla = ColorCatalog.vanillaOptions();
        assertEquals(16, vanilla.size());
        for (ColorOption o : vanilla) {
            assertEquals(ColorOption.Kind.VANILLA, o.kind());
            assertEquals("!" + o.id(), o.storedValue());
            assertEquals("tc2.color." + o.id(), o.permission());
        }
    }

    @Test
    void customOptionsDistinguishRandomFromFixed() {
        List<ColorOption> custom = ColorCatalog.customOptions();
        ColorOption j = custom.stream().filter(o -> o.id().equals("j")).findFirst().orElseThrow();
        ColorOption rnd =
                custom.stream().filter(o -> o.id().equals("rnd")).findFirst().orElseThrow();
        assertEquals(ColorOption.Kind.CUSTOM, j.kind());
        assertEquals("!j", j.storedValue());
        assertEquals("tc2.color.j", j.permission());
        assertEquals(ColorOption.Kind.RANDOM, rnd.kind());
    }

    @Test
    void gradientOptionsUseGradPermissionPrefix() {
        List<ColorOption> grads = ColorCatalog.gradientOptions();
        ColorOption gr = grads.stream().filter(o -> o.id().equals("gr")).findFirst().orElseThrow();
        assertEquals(ColorOption.Kind.GRADIENT, gr.kind());
        assertEquals("!gr", gr.storedValue());
        assertEquals("tcg2.color.gr", gr.permission());
    }

    @Test
    void plainPermissionGrantsNonWooOption() {
        ColorOption option = ColorCatalog.vanillaOption("f");
        assertTrue(ColorCatalog.hasPermission(option, allow("tc2.color.f")));
    }

    @Test
    void wooIdRequiresExtraPermission() {
        ColorOption a = ColorCatalog.vanillaOption("a");
        assertFalse(ColorCatalog.hasPermission(a, allow("tc2.color.a")));
        assertTrue(ColorCatalog.hasPermission(a, allow("tc2.color.a", "tco.woo")));
    }

    @Test
    void wooRuleDoesNotApplyToNonWooIds() {
        ColorOption f = ColorCatalog.vanillaOption("f");
        assertTrue(ColorCatalog.hasPermission(f, allow("tc2.color.f")));
    }

    @Test
    void visibleAndLockedPartitionAllOptions() {
        Predicate<String> perm = allow("tc2.color.f", "tc2.color.j");
        List<ColorOption> visible = ColorCatalog.visible(perm);
        List<ColorOption> locked = ColorCatalog.locked(perm);
        assertEquals(ColorCatalog.allOptions().size(), visible.size() + locked.size());
        assertTrue(visible.stream().allMatch(o -> ColorCatalog.hasPermission(o, perm)));
        assertTrue(locked.stream().noneMatch(o -> ColorCatalog.hasPermission(o, perm)));
    }

    @Test
    void findLooksUpAcrossAllKinds() {
        assertEquals(ColorOption.Kind.VANILLA, ColorCatalog.find("f").orElseThrow().kind());
        assertEquals(ColorOption.Kind.CUSTOM, ColorCatalog.find("j").orElseThrow().kind());
        assertEquals(ColorOption.Kind.GRADIENT, ColorCatalog.find("gr").orElseThrow().kind());
        assertTrue(ColorCatalog.find("doesnotexist").isEmpty());
    }

    @Test
    void findVanillaOrCustomExcludesGradients() {
        assertTrue(ColorCatalog.findVanillaOrCustom("gr").isEmpty());
        assertTrue(ColorCatalog.findVanillaOrCustom("j").isPresent());
    }

    @Test
    void findGradientOnlyMatchesGradients() {
        assertTrue(ColorCatalog.findGradient("j").isEmpty());
        assertTrue(ColorCatalog.findGradient("gr").isPresent());
    }

    @Test
    void paginateSplitsIntoFullPages() {
        List<Integer> items = List.of(1, 2, 3, 4, 5);
        List<List<Integer>> pages = ColorCatalog.paginate(items, 2);
        assertEquals(3, pages.size());
        assertEquals(List.of(1, 2), pages.get(0));
        assertEquals(List.of(3, 4), pages.get(1));
        assertEquals(List.of(5), pages.get(2));
    }

    @Test
    void paginateExactMultipleHasNoTrailingEmptyPage() {
        List<Integer> items = List.of(1, 2, 3, 4);
        List<List<Integer>> pages = ColorCatalog.paginate(items, 2);
        assertEquals(2, pages.size());
    }

    @Test
    void paginateEmptyListYieldsOneEmptyPage() {
        List<List<Integer>> pages = ColorCatalog.paginate(List.of(), 10);
        assertEquals(1, pages.size());
        assertTrue(pages.get(0).isEmpty());
    }
}
