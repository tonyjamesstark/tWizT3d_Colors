package me.tWizT3d_dreaMr.colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class gradientItem {
    public static void gothrough(String[] args, Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            p.sendMessage(LangHandler.get("Grad", "NoItem", null, null));
            return;
        }
        ItemMeta im = item.getItemMeta();
        if (!im.hasLore()) {
            p.sendMessage(LangHandler.get("Grad", "NoLore", null, null));
            return;
        }
        List<String> lorlis = im.getLore();
        String[] lore = lorlis.toArray(new String[0]);

        CommandArgs.GradResult result = CommandArgs.validateGrad(args, lorlis.size());
        if (!result.ok()) {
            p.sendMessage(gradErrorMessage(result.error(), lorlis.size()));
            return;
        }
        CommandArgs.GradParsed parsed = result.parsed();
        ArrayList<String> hexes = new ArrayList<>(parsed.hexes());
        lore =
                hexes.size() == 2
                        ? comgra2(hexes, parsed.start(), parsed.end(), lore)
                        : comgra3(hexes, parsed.start(), parsed.end(), lore);
        if (lore == null) {
            p.sendMessage(LangHandler.get("Grad", "h1h2", null, null));
            return;
        }
        List<String> endLore = new ArrayList<>();
        Collections.addAll(endLore, lore);
        im.setLore(endLore);
        item.setItemMeta(im);
        p.sendMessage(LangHandler.get("Grad", "Success", null, null));
    }

    private static String gradErrorMessage(CommandArgs.GradError error, int loreSize) {
        return switch (error.type()) {
            case ARG_COUNT ->
                    LangHandler.get(
                            "Grad",
                            "Usage",
                            "example",
                            "<hex1> <hex2> [hex3] <start line> <end line>  e.g. /grad #ff0000"
                                    + " #0000ff 1 3");
            case BAD_HEX -> LangHandler.get("Grad", "BadHex", "arg", error.arg());
            case BAD_NUMBER -> LangHandler.get("Grad", "BadNumber", "arg", error.arg());
            case LINE_OUT_OF_RANGE ->
                    LangHandler.get("Grad", "LineOutOfRange", "arg", error.arg())
                            .replace(">size", String.valueOf(loreSize));
            case RANGE_TOO_SMALL -> LangHandler.get("Grad", "RangeTooSmall", null, null);
        };
    }

    private static final Pattern CODE = Pattern.compile("&[0-9a-fA-Fk-oK-OrRxX#]");

    public static String gradString(String[] strings, String st) {
        String ep = "";
        Matcher code = CODE.matcher(st);
        if (code.find()) {
            ep = st.substring(code.start());
            st = st.substring(0, code.start());
        }
        if (st.isEmpty()) return ep;

        String[] ls = st.codePoints().mapToObj(Character::toString).toArray(String[]::new);
        String[] end = comgra(strings, 1, ls.length, ls);
        StringBuilder fin = new StringBuilder();
        for (String s : end) fin.append(s);
        return fin + ep;
    }

    public static String[] comgra(String[] strings, int start, int end, String[] lore) {
        int loch = Math.max(start, end);
        int locl = Math.min(start, end);
        int dif = loch - locl;
        int garsize = dif;

        ArrayList<String> temp;
        temp = GraMe(strings[0], strings[1], garsize);

        if (temp == null) {
            Bukkit.getLogger().log(Level.WARNING, "H1 or H2 null"); // in lang
            return null;
        }
        ArrayList<String> grabass = new ArrayList<>(temp);

        if (locl != 0)
            for (String hx : grabass) {
                lore[locl - 1] = hx + ChatColor.stripColor(lore[locl - 1]);
                locl++;
            }

        return lore;
    }

    public static String[] comgra2(ArrayList<String> hexes, int start, int end, String[] lore) {
        int loch = Math.max(start, end);
        int locl = Math.min(start, end);
        int dif = loch - locl;
        int garsize = dif;

        ArrayList<String> temp;
        temp = GraMe(hexes.get(0), hexes.get(1), garsize);

        if (temp == null) {
            Bukkit.getLogger().log(Level.WARNING, "H1 or H2 null"); // in lang
            return null;
        }
        ArrayList<String> grabass = new ArrayList<>(temp);

        for (String hx : grabass) {

            lore[locl - 1] = "" + hx + ChatColor.stripColor(lore[locl - 1]);
            locl++;
        }
        return lore;
    }

    public static String[] comgra3(ArrayList<String> hexes, int start, int end, String[] lore) {

        int loch = Math.max(start, end) - 1;
        int locl = Math.min(start, end);
        int dif = loch - locl;
        int garsize = dif / 2;
        int gardif = dif % 2;

        ArrayList<String> grabass = new ArrayList<>();
        for (int n = 0; n < 2; n++) {
            ArrayList<String> temp;
            if (gardif > 0) {
                temp = GraMe(hexes.get(n), hexes.get(n + 1), garsize + 1);
                gardif--;
            } else {
                temp = GraMe(hexes.get(n), hexes.get(n + 1), garsize);
            }
            // if(if gardif not=0 set middle ones or last longer)
            if (temp == null) {
                Bukkit.getLogger().log(Level.WARNING, "H1 or H2 null"); // in lang
                return null;
            }
            grabass.addAll(temp);
        }

        for (String hx : grabass) {
            lore[locl - 1] = "" + hx + ChatColor.stripColor(lore[locl - 1]);
            locl++;
        }
        return lore;
    }

    public static ArrayList<String> GraMe(String h2, String h1, int garsize) {
        ArrayList<String> graout = new ArrayList<>();

        h1 = h1.replace("#", "");
        h1 = h1.replace("&", "");
        h2 = h2.replace("#", "");
        h2 = h2.replace("&", "");
        if (h1.length() != 6) {
            Bukkit.getLogger().log(Level.WARNING, "h1 not right length: " + h1);
            return null;
        }
        if (h2.length() != 6) {
            Bukkit.getLogger().log(Level.WARNING, "h2 not right length: " + h2);
            return null;
        }
        int h1r = Integer.parseInt(h1.substring(0, 2), 16);
        int h1g = Integer.parseInt(h1.substring(2, 4), 16);
        int h1b = Integer.parseInt(h1.substring(4), 16);

        int h2r = Integer.parseInt(h2.substring(0, 2), 16);
        int h2g = Integer.parseInt(h2.substring(2, 4), 16);
        int h2b = Integer.parseInt(h2.substring(4), 16);

        ArrayList<Integer> r = numes(h1r, h2r, garsize);
        ArrayList<Integer> g = numes(h1g, h2g, garsize);
        ArrayList<Integer> b = numes(h1b, h2b, garsize);
        if (r.size() != g.size() && b.size() != g.size()) {
            r = numes(h1r, h2r, garsize);
            g = numes(h1g, h2g, garsize);
            b = numes(h1b, h2b, garsize);
        }
        for (int i = 0; i < r.size(); i++) {
            String hr = Integer.toHexString(r.get(i));
            String hg = Integer.toHexString(g.get(i));
            String hb = Integer.toHexString(b.get(i));

            if (hr.length() == 1) hr = "0" + hr;
            else if (hr.length() == 3) hr = "ff";
            if (hg.length() == 1) hg = "0" + hg;
            else if (hg.length() == 3) hg = "ff";
            if (hb.length() == 1) hb = "0" + hb;
            else if (hb.length() == 3) hb = "ff";

            String hex = ChatColor.of("#" + hr + hg + hb) + "";

            graout.add(hex);
        }

        return graout;
    }

    public static ArrayList<Integer> numes(Integer a, Integer b, int gs) {
        ArrayList<Integer> out = new ArrayList<>();
        int dif = a - b;
        if (dif == 0) {
            for (int i = 0; i < (gs + 1); i++) {
                out.add(a);
            }
        } else {
            double incr = 1.0 * dif / gs;
            for (int i = 0; i < (gs); i++) {
                out.add((int) Math.floor(b + (incr * i)));
            }
            out.add(a);
        }
        return out;
    }
}
