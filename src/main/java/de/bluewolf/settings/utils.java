package de.bluewolf.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class utils
{

    // -- GENERAL SETTINGS -- //
    public static final String PREFIX = "-";
    public static final String VERSION = "1.0";

    public static String CUSTOM_MASSAGE = "REPLACE YOUR CUSTOM MASSAGE HERE";

    public static OnlineStatus STATUS = OnlineStatus.ONLINE;
    public static Activity ACTIVITY = Activity.playing(VERSION + " | -help | " + CUSTOM_MASSAGE);


    public static MessageEmbed NO_PERM = new EmbedBuilder()
            .setColor(Color.RED)
            .setDescription(
                    "Sorry, but you don't have permissions to perform this command!"
            )
            .build();

    public static EmbedBuilder ERROR = new EmbedBuilder()
            .setColor(Color.RED);

    // --|| CONSOLE COLORS ||-- //
    public static class ConsoleColors
    {
        // Reset
        public static final String RESET = "\033[0m";  // Text Reset

        // Regular Colors
        public static final String BLACK = "\033[0;30m";   // BLACK
        public static final String RED = "\033[0;31m";     // RED
        public static final String GREEN = "\033[0;32m";   // GREEN
        public static final String YELLOW = "\033[0;33m";  // YELLOW
        public static final String BLUE = "\033[0;34m";    // BLUE
        public static final String PURPLE = "\033[0;35m";  // PURPLE
        public static final String CYAN = "\033[0;36m";    // CYAN
        public static final String WHITE = "\033[0;37m";   // WHITE
    }

    public static void CommandExecuted(String cmd)
    {
        System.out.println(
                ConsoleColors.BLACK + "[" + ConsoleColors.YELLOW + "INFO" + ConsoleColors.BLACK + ConsoleColors.RESET
                        + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + cmd + ConsoleColors.BLACK + "'" + ConsoleColors.RESET
                        + "was executed!"
        );
    }

}
