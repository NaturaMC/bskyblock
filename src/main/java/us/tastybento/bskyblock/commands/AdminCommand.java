package us.tastybento.bskyblock.commands;

import java.util.List;

import us.tastybento.bskyblock.api.commands.CompositeCommand;
import us.tastybento.bskyblock.api.localization.TextVariables;
import us.tastybento.bskyblock.api.user.User;
import us.tastybento.bskyblock.commands.admin.AdminClearResetsAllCommand;
import us.tastybento.bskyblock.commands.admin.AdminClearResetsCommand;
import us.tastybento.bskyblock.commands.admin.AdminGetRankCommand;
import us.tastybento.bskyblock.commands.admin.AdminInfoCommand;
import us.tastybento.bskyblock.commands.admin.AdminRegisterCommand;
import us.tastybento.bskyblock.commands.admin.AdminReloadCommand;
import us.tastybento.bskyblock.commands.admin.AdminSchemCommand;
import us.tastybento.bskyblock.commands.admin.AdminSetRankCommand;
import us.tastybento.bskyblock.commands.admin.AdminTeleportCommand;
import us.tastybento.bskyblock.commands.admin.AdminUnregisterCommand;
import us.tastybento.bskyblock.commands.admin.AdminVersionCommand;
import us.tastybento.bskyblock.commands.admin.range.AdminRangeCommand;
import us.tastybento.bskyblock.commands.admin.team.AdminTeamAddCommand;
import us.tastybento.bskyblock.commands.admin.team.AdminTeamDisbandCommand;
import us.tastybento.bskyblock.commands.admin.team.AdminTeamKickCommand;
import us.tastybento.bskyblock.commands.admin.team.AdminTeamMakeLeaderCommand;

public class AdminCommand extends CompositeCommand {

    public AdminCommand() {
        super("bsbadmin", "bsb");
    }

    @Override
    public void setup() {
        setPermissionPrefix("bskyblock");
        setPermission("admin.*");
        setOnlyPlayer(false);
        setParameters("commands.admin.help.parameters");
        setDescription("commands.admin.help.description");
        setWorld(getPlugin().getIWM().getBSBIslandWorld());
        new AdminVersionCommand(this);
        new AdminReloadCommand(this);
        new AdminTeleportCommand(this, "tp");
        new AdminTeleportCommand(this, "tpnether");
        new AdminTeleportCommand(this, "tpend");
        new AdminGetRankCommand(this);
        new AdminSetRankCommand(this);
        new AdminInfoCommand(this);
        // Team commands
        new AdminTeamAddCommand(this);
        new AdminTeamKickCommand(this);
        new AdminTeamDisbandCommand(this);
        new AdminTeamMakeLeaderCommand(this);
        // Schems
        new AdminSchemCommand(this);
        // Register/unregister islands
        new AdminRegisterCommand(this);
        new AdminUnregisterCommand(this);
        // Range
        new AdminRangeCommand(this);
        // Resets
        new AdminClearResetsCommand(this);
        new AdminClearResetsAllCommand(this);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (!args.isEmpty()) {
            user.sendMessage("general.errors.unknown-command", TextVariables.LABEL, getTopLabel());
            return false;
        }
        // By default run the attached help command, if it exists (it should)
        return showHelp(this, user);
    }

}
