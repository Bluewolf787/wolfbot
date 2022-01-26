package de.bluewolf.wolfbot.settings;

import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.settings
 * @created 03/Jan/2021 - 15:23
 */
public class Permissions
{

    /**
     * Check if the user who tries to execute a command has permission and is in the right channel
     * or if the Bot is on Downtime and can't execute commands at the moment
     * @param event MessageReceivedEvent
     * @param command The command which should be executed
     * @return If the command can be executed by the member
     */
    public static boolean check(MessageReceivedEvent event, String command) throws SQLException
    {
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        Permission permission;
        TextChannel channel = null;
        Member member = event.getMember();
        String author = event.getAuthor().getId();

        // Get permissions for the command from the Permissions table
        ResultSet getPermission = DatabaseHelper.query(
                "SELECT Permission FROM Permissions WHERE GuildId = '" + guildId + "' AND Command = '" + command + "';"
        );
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            // If no entry for the command exists get default permission for the command
            permission = Permission.getFromOffset(BotSettings.commandsWithPermissions.get(command));

        // Check if there is a specific channel for the command on the guild
        ResultSet getChannel = DatabaseHelper.query(
                "SELECT ChannelId FROM CommandChannels WHERE GuildId = '" + guildId + "' AND Command = '" + command + "';"
        );
        if (getChannel.next() && getChannel.getString("ChannelId") != null)
            channel = guild.getTextChannelById(getChannel.getString("ChannelId"));

        // Check if the Bot is on downtime
        if (!BotSettings.DOWNTIME)
        {
            // Bot isn't on downtime
            // Check if the command has a specific channel
            if (channel == null)
            {
                // There is no specific channel for the command
                // Check if the user has the permission to perform the command
                if (member != null && member.getPermissions().contains(permission))
                    return false;
                else // The user doesn't have the permissions
                {
                    CustomMsg.NO_PERM(event, command);
                    return true;
                }
            }
            // If yes check if the user is in the right channel
            else if (event.getChannel().equals(channel))
            {
                // User is in the right channel
                // Check if the user has the permission to perform the command
                if (member != null && member.getPermissions().contains(permission))
                    return false;
                else // The user doesn't have the permissions
                {
                    CustomMsg.NO_PERM(event, command);
                    return true;
                }
            }
            else // The command has a specific channel and the user is in the wrong channel
            {
                CustomMsg.WRONG_CHANNEL(event.getTextChannel(), channel, command);
                return true;
            }
        }
        else if (author.equals(Secret.ID)) // Check if the user is the developer
            return false;
        else // Bot is on DOWNTIME (Stopping or Restarting)
        {
            CustomMsg.CMD_ERROR(event, command, "Tried to perform command while downtime");
            return true;
        }
    }

    public static HashMap<String, Integer> permissionInformation = new HashMap<>();

    public static void initializePermissionInformation()
    {
        permissionInformation.put(Permission.MESSAGE_SEND.getName(), Permission.MESSAGE_SEND.getOffset());
        permissionInformation.put(Permission.VIEW_CHANNEL.getName(), Permission.VIEW_CHANNEL.getOffset());

        CustomMsg.INFO("Initialized Permission information");
    }

}
