package de.bluewolf.wolfbot.settings;

import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.settings
 * @created 15/Dez/2020 - 17:48
 */
public class BotSettings
{

    // -- GENERAL SETTINGS -- //
    public static final String PREFIX = "!"; // Command prefix
    public static final String VERSION = "SNAPSHOT-2021.1"; // Current version of the bot

    public static String CUSTOM_MASSAGE = "狼機器人"; // Message, which will be shown in the status

    public static OnlineStatus STATUS = OnlineStatus.ONLINE; // Online status
    public static Activity ACTIVITY = Activity.playing(VERSION + " | -help | " + CUSTOM_MASSAGE); // Playing status

    // All Bot commands with permissions
    public static HashMap<String, Integer> commandsWithPermissions = new HashMap<String, Integer>();

    // -- CREATE STAFF ROLE -- //
    public static void createStaffRole(Guild guild, String guildId) throws SQLException
    {
        ResultSet getRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = 'Staff';");

        if (!getRoles.next())
        {
            DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Type) VALUES ('" + guildId + "', 'Staff', 'general');");
            guild.createRole()
                    .setColor(Color.YELLOW)
                    .setHoisted(true)
                    .setMentionable(true)
                    .setName("Staff")
                    .setPermissions(
                            Permission.MESSAGE_MANAGE, Permission.MANAGE_ROLES, Permission.BAN_MEMBERS,
                            Permission.KICK_MEMBERS, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION,
                            Permission.MESSAGE_ATTACH_FILES, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                            Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS
                    ).queue();

        }
    }

    // -- STATUS AND PERMISSION SETTINGS -- //
    public static boolean DOWNTIME = false;
    public static OnlineStatus DOWNTIME_STATUS = OnlineStatus.DO_NOT_DISTURB;
    public static Activity RESTART_ACTIVITY = Activity.playing("RESTARTING");
    public static Activity STOP_ACTIVITY = Activity.playing("STOPPING");


    // -- PERMISSION/STATUS CHECK -- //
    public static boolean checkPermissions(MessageReceivedEvent event, Permission permission, String cmd)
    {
        Member member = event.getMember();
        String author = event.getAuthor().getId();

        if (!DOWNTIME)
        {
            if (member != null && member.getPermissions().contains(permission))
                return false;
            // No permissions
            else
            {
                CustomMsg.NO_PERM(event, cmd);
                return true;
            }
        }
        else if (author.equals(Secret.ID))
            return false;
        // DOWNTIME (Stopping or Restarting)
        else
        {
            CustomMsg.CMD_ERROR(event, cmd, "Tried to perform command while downtime");
            return true;
        }
    }

}
