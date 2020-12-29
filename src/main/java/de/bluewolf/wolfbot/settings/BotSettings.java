package de.bluewolf.wolfbot.settings;

import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.settings
 * @created 15/Dez/2020 - 17:48
 */
public class BotSettings
{

    // -- GENERAL SETTINGS -- //
    public static final String PREFIX = "-";
    public static final String VERSION = "SNAPSHOT-2021.1";

    public static String CUSTOM_MASSAGE = "狼機器人";

    public static OnlineStatus STATUS = OnlineStatus.ONLINE;
    public static Activity ACTIVITY = Activity.playing(VERSION + " | -help | " + CUSTOM_MASSAGE);


    // -- STATUS AND PERMISSION SETTINGS -- //
    public static boolean DOWNTIME = false;
    public static OnlineStatus DOWNTIME_STATUS = OnlineStatus.DO_NOT_DISTURB;
    public static Activity RESTART_ACTIVITY = Activity.playing("RESTARTING");
    public static Activity STOP_ACTIVITY = Activity.playing("STOPPING");

    // All Bot commands with permissions
    public static HashMap<String, Integer> commandsWithPermissions = new HashMap<String, Integer>();

    // -- PERMISSION/STATUS CHECK -- //
    public static boolean PERMISSIONS(MessageReceivedEvent event, Permission permission, String cmd)
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
