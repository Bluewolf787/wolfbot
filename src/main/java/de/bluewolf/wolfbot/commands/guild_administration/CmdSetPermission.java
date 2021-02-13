package de.bluewolf.wolfbot.commands.guild_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.core.CommandHandler;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.guild_administration
 * @created 29/Dez/2020 - 14:55
 */
public class CmdSetPermission implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "setpermission");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        if (args.length == 1 && args[0].equalsIgnoreCase("info"))
        {
            StringBuilder permissionInfoMessage = new StringBuilder();
            for (String i : Permissions.permissionInformation.keySet())
            {
                permissionInfoMessage.append(" - ").append(i).append("\n");
            }

            event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setColor(new Color(0xdf1196))
                    .setTitle("Available Command Permissions")
                    .setDescription(permissionInfoMessage.toString())
                    .build()
            ).queue();

            executed(true, event);
        }
        // Check if command and permission is entered
        else if (args.length > 2)
        {
            String command = args[0].toLowerCase();

            // Check if the entered command exists
            if (CommandHandler.commands.containsKey(command))
            {
                StringBuilder permission = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                {
                    permission.append(args[i]).append(" ");
                }

                // Check if permission exists
                if (Permissions.permissionInformation.containsKey(permission.toString().trim()))
                {

                    // TODO Change permission in Permissions table

                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Set new permission for command")
                            .setDescription(Permissions.permissionInformation.get(permission.toString().trim()).toString())
                            .build()
                    ).queue();
                }
                else // Permission doesn't exists
                {
                    CustomMsg.CMD_ERROR(event, "setpermission", "Can't find the permission ``" + permission.toString().trim() + "``.");
                    executed(false, event);
                }
            }
            else // Comment doesn't exists
            {
                CustomMsg.CMD_ERROR(event, "setpermission", "The command ``" + args[0] + "`` doesn't exists");
                executed(false, event);
            }
        }
        else // Too few arguments
        {
            executed(false, event);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "setpermission");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "setpermission <command> <permsission>`` to set the needed permission for a command\n" +
                "(To show available permissions use ``" + BotSettings.PREFIX + "setpermission info``)";
    }
}
