/**
 * 
 */
package us.tastybento.bskyblock.commands.admin.team;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import us.tastybento.bskyblock.BSkyBlock;
import us.tastybento.bskyblock.Settings;
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
public class AdminTeamKickCommandTest {
    
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
        when(plugin.getIWM()).thenReturn(iwm);


        // Player has island to begin with 
        im = mock(IslandsManager.class);
        when(im.hasIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(true);
        when(im.hasIsland(Mockito.any(), Mockito.any(User.class))).thenReturn(true);
        when(im.isOwner(Mockito.any(),Mockito.any())).thenReturn(true);
        when(im.getTeamLeader(Mockito.any(),Mockito.any())).thenReturn(uuid);
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
        when(lm.get(Mockito.any(), Mockito.any())).thenReturn("mock translation");
        when(plugin.getLocalesManager()).thenReturn(lm);
    }


    /**
     * Test method for {@link AdminTeamKickCommand#execute(User, String, List)}.
     */
    @Test
    public void testExecuteNoTarget() {
        AdminTeamKickCommand itl = new AdminTeamKickCommand(ac);
        assertFalse(itl.execute(user, itl.getLabel(), new ArrayList<>()));
        // Show help
    }
    
    /**
     * Test method for {@link AdminTeamKickCommand#execute(User, String, List)}.
     */
    @Test
    public void testExecuteUnknownPlayer() {
        AdminTeamKickCommand itl = new AdminTeamKickCommand(ac);
        String[] name = {"tastybento"};
        when(pm.getUUID(Mockito.any())).thenReturn(null);
        assertFalse(itl.execute(user, itl.getLabel(), Arrays.asList(name)));
        Mockito.verify(user).sendMessage(Mockito.eq("general.errors.unknown-player"));
    }
        
    /**
     * Test method for {@link AdminTeamKickCommand#execute(User, String, List)}.
     */
    @Test
    public void testExecutePlayerNotInTeam() {
        AdminTeamKickCommand itl = new AdminTeamKickCommand(ac);
        String[] name = {"tastybento"};
        when(pm.getUUID(Mockito.any())).thenReturn(notUUID);
        when(im.getMembers(Mockito.any(), Mockito.any())).thenReturn(new HashSet<>());
        assertFalse(itl.execute(user, itl.getLabel(), Arrays.asList(name)));
        Mockito.verify(user).sendMessage(Mockito.eq("general.errors.not-in-team"));
    }

    /**
     * Test method for {@link AdminTeamKickCommand#execute(User, String, List)} .
     */
    @Test
    public void testExecuteKickLeader() {
        when(im.inTeam(Mockito.any(), Mockito.any())).thenReturn(true);
        Island is = mock(Island.class);
        when(im.getIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(is);
        String[] name = {"tastybento"};
        when(pm.getUUID(Mockito.any())).thenReturn(notUUID);
        
        when(im.getTeamLeader(Mockito.any(), Mockito.eq(notUUID))).thenReturn(notUUID);
        
        AdminTeamKickCommand itl = new AdminTeamKickCommand(ac);
        assertFalse(itl.execute(user, itl.getLabel(), Arrays.asList(name)));
        Mockito.verify(user).sendMessage(Mockito.eq("commands.admin.team.kick.cannot-kick-leader"));
        Mockito.verify(is).showMembers(Mockito.any(), Mockito.any(), Mockito.any());
    }
    
    /**
     * Test method for {@link us.tastybento.bskyblock.commands.admin.team.AdminTeamKickCommand#execute(User, String, List)}.
     */
    @Test
    public void testExecute() {
        when(im.inTeam(Mockito.any(), Mockito.any())).thenReturn(true);
        Island is = mock(Island.class);
        when(im.getIsland(Mockito.any(), Mockito.any(UUID.class))).thenReturn(is);
        String[] name = {"tastybento"};
        when(pm.getUUID(Mockito.any())).thenReturn(notUUID);
                
        when(im.getTeamLeader(Mockito.any(), Mockito.eq(notUUID))).thenReturn(uuid);
        
        AdminTeamKickCommand itl = new AdminTeamKickCommand(ac);
        assertTrue(itl.execute(user, itl.getLabel(), Arrays.asList(name)));
        Mockito.verify(im).removePlayer(Mockito.any(), Mockito.eq(notUUID));
        Mockito.verify(user).sendMessage(Mockito.eq("general.success"));
    }

}
