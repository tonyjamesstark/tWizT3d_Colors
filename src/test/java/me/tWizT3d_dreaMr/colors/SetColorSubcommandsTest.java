package me.tWizT3d_dreaMr.colors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class SetColorSubcommandsTest {

    @Test
    void isHelpAcceptsHelpAndQuestionMark() {
        assertTrue(SetColorSubcommands.isHelp("help"));
        assertTrue(SetColorSubcommands.isHelp("HELP"));
        assertTrue(SetColorSubcommands.isHelp("?"));
    }

    @Test
    void isHelpRejectsOtherArgs() {
        assertFalse(SetColorSubcommands.isHelp("set"));
        assertFalse(SetColorSubcommands.isHelp(""));
    }

    @Test
    void findIsCaseInsensitive() {
        assertTrue(SetColorSubcommands.find("SET").isPresent());
        assertEquals(SetColorSubcommands.Name.SET, SetColorSubcommands.find("SET").get().name());
    }

    @Test
    void findRejectsUnknownSubcommand() {
        assertTrue(SetColorSubcommands.find("nope").isEmpty());
    }

    @Test
    void setSetgRemoveExpectTheirArgCounts() {
        assertEquals(2, SetColorSubcommands.find("set").get().argCount());
        assertEquals(2, SetColorSubcommands.find("setg").get().argCount());
        assertEquals(1, SetColorSubcommands.find("remove").get().argCount());
        assertEquals(1, SetColorSubcommands.find("chat").get().argCount());
    }

    @Test
    void onlyChatIsPermissionGated() {
        assertEquals(null, SetColorSubcommands.find("set").get().permission());
        assertEquals(null, SetColorSubcommands.find("setg").get().permission());
        assertEquals(null, SetColorSubcommands.find("remove").get().permission());
        assertEquals("tc2.can", SetColorSubcommands.find("chat").get().permission());
    }

    @Test
    void visibleToHidesChatWithoutItsPermission() {
        List<SetColorSubcommands.Spec> visible = SetColorSubcommands.visibleTo(perm -> false);
        assertFalse(visible.stream().anyMatch(s -> s.name() == SetColorSubcommands.Name.CHAT));
        assertTrue(visible.stream().anyMatch(s -> s.name() == SetColorSubcommands.Name.SET));
    }

    @Test
    void visibleToShowsChatWithItsPermission() {
        List<SetColorSubcommands.Spec> visible = SetColorSubcommands.visibleTo(perm -> true);
        assertTrue(visible.stream().anyMatch(s -> s.name() == SetColorSubcommands.Name.CHAT));
        assertEquals(SetColorSubcommands.all().size(), visible.size());
    }
}
