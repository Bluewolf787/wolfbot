package de.bluewolf.wolfbot.settings;

import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

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
    public static final String VERSION = "2021.1.2-SNAPSHOT"; // Current version of the bot

    public static String CUSTOM_MASSAGE = "狼機器人"; // Message, which will be shown in the status

    public static OnlineStatus STATUS = OnlineStatus.ONLINE; // Online status
    public static Activity ACTIVITY = Activity.playing(VERSION + " | " + PREFIX + "help | " + CUSTOM_MASSAGE); // Playing status

    // All Bot commands with permissions
    public static HashMap<String, Integer> commandsWithPermissions = new HashMap<String, Integer>();

    // -- STATUS AND PERMISSION SETTINGS -- //
    public static boolean DOWNTIME = false;
    public static OnlineStatus DOWNTIME_STATUS = OnlineStatus.DO_NOT_DISTURB;
    public static Activity RESTART_ACTIVITY = Activity.playing("RESTARTING");
    public static Activity STOP_ACTIVITY = Activity.playing("STOPPING");

}
