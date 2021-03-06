/**
 *
 */
package us.tastybento.bskyblock.commands.admin.range;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import us.tastybento.bskyblock.BSkyBlock;
import us.tastybento.bskyblock.Settings;
import us.tastybento.bskyblock.api.localization.TextVariables;
import us.tastybento.bskyblock.api.user.User;
import us.tastybento.bskyblock.commands.AdminCommand;
import us.tastybento.bskyblock.database.objects.Island;
import us.tastybento.bskyblock.managers.CommandsManager;
import us.tastybento.bskyblock.managers.IslandWorldManager;
import us.tastybento.bskyblock.managers.IslandsManager;
import us.tastybento.bskyblock.managers.LocalesManager;
import us.tastybento.bskyblock.managers.PlayersManager;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BSkyBlock.class, User.class })
public class AdminRangeResetCommandTest {

    private BSkyBlock plugin;
    private AdminCommand ac;
    private UUID uuid;
    private User user;
    private IslandsManager im;
    private PlayersManager pm;
    private UUID notUUID;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Set up plugin
        plugin = mock(BSkyBlock.class);
        Whitebox.setInternalState(BSkyBlock.class, "instance", plugin);

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // Settings
        Settings s = mock(Settings.class);
        when(s.getResetWait()).thenReturn(0L);
        when(s.getResetLimit()).thenReturn(3);
        when(plugin.getSettings()).thenReturn(s);

        // Player
        Player p = mock(Player.class);
        // Sometimes use Mockito.withSettings().verboseLogging()
        user = mock(User.class);
        when(user.isOp()).thenReturn(false);
        uuid = UUID.randomUUID();
        notUUID = UUID.randomUUID();
        while(notUUID.equals(uuid)) {
            notUUID = UUID.randomUUID();
        }
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.getName()).thenReturn("tastybento");
        User.setPlugin(plugin);

        // Parent command has no aliases
        ac = mock(AdminCommand.class);
        when(ac.getSubCommandAliases()).thenReturn(new HashMap<>());

        // Island World Manager
        IslandWorldManager iwm = mock(IslandWorldManager.class);
        World world = mock(World.class);
        when(iwm.getBSBIslandWorld()).thenReturn(world);
        when(iwm.getFriendlyName(Mockito.any())).thenReturn("BSkyBlock");
        when(iwm.getIslandProtectionRange(Mockito.any())).thenReturn(200);
        when(plugin.getIWM()).thenReturn(iwm);


        // Player has island to begin with
        im = mock(IslandsManager.class);
        when(im.hasIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(true);
        when(im.hasIsland(Mockito.any(), Mockito.any(User.class))).thenReturn(true);
        when(im.isOwner(Mockito.any(),Mockito.any())).thenReturn(true);
        when(im.getTeamLeader(Mockito.any(),Mockito.any())).thenReturn(uuid);
        Island island = mock(Island.class);
        when(im.getIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(island);
        when(plugin.getIslands()).thenReturn(im);

        // Has team
        pm = mock(PlayersManager.class);
        when(im.inTeam(Mockito.any(), Mockito.eq(uuid))).thenReturn(true);

        when(plugin.getPlayers()).thenReturn(pm);

        // Server & Scheduler
        BukkitScheduler sch = mock(BukkitScheduler.class);
        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(sch);

        // Locales
        LocalesManager lm = mock(LocalesManager.class);
        Answer<String> answer = new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgumentAt(1, String.class);
            }
        };

        when(lm.get(Mockito.any(), Mockito.any())).thenAnswer(answer );
        when(plugin.getLocalesManager()).thenReturn(lm);

    }

    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.range.AdminRangeResetCommand#execute(us.tastybento.bskyblock.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteConsoleNoArgs() {
        AdminRangeResetCommand arc = new AdminRangeResetCommand(ac);
        CommandSender sender = mock(CommandSender.class);
        User console = User.getInstance(sender);
        arc.execute(console, "", new ArrayList<>());
        // Show help
        Mockito.verify(sender).sendMessage("commands.help.header");
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.range.AdminRangeResetCommand#execute(us.tastybento.bskyblock.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecutePlayerNoArgs() {
        AdminRangeResetCommand arc = new AdminRangeResetCommand(ac);
        arc.execute(user, "", new ArrayList<>());
        // Show help
        Mockito.verify(user).sendMessage("commands.help.header","[label]","BSkyBlock");
    }


    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.range.AdminRangeResetCommand#execute(us.tastybento.bskyblock.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUnknownPlayer() {
        AdminRangeResetCommand arc = new AdminRangeResetCommand(ac);
        List<String> args = new ArrayList<>();
        args.add("tastybento");
        arc.execute(user, "", args);
        Mockito.verify(user).sendMessage("general.errors.unknown-player");
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.range.AdminRangeResetCommand#execute(us.tastybento.bskyblock.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteKnownPlayerNoIsland() {
        when(pm.getUUID(Mockito.anyString())).thenReturn(uuid);
        when(im.hasIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(false);
        AdminRangeResetCommand arc = new AdminRangeResetCommand(ac);
        List<String> args = new ArrayList<>();
        args.add("tastybento");
        arc.execute(user, "", args);
        Mockito.verify(user).sendMessage("general.errors.player-has-no-island");
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.range.AdminRangeResetCommand#execute(us.tastybento.bskyblock.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteKnownPlayer() {
        when(pm.getUUID(Mockito.anyString())).thenReturn(uuid);
        AdminRangeResetCommand arc = new AdminRangeResetCommand(ac);
        List<String> args = new ArrayList<>();
        args.add("tastybento");
        arc.execute(user, "", args);
        Mockito.verify(user).sendMessage("commands.admin.range.reset.success", TextVariables.NUMBER, "200");
    }

}
