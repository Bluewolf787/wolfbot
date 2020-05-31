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
    public static final String VERSION = "1.5.2";

    public static String CUSTOM_MASSAGE = "狼機器人";

    public static OnlineStatus STATUS = OnlineStatus.ONLINE;
    public static Activity ACTIVITY = Activity.playing(VERSION + " | -help | " + CUSTOM_MASSAGE);


    // -- MASSAGES -- //
    // NO PERM
    public static void NO_PERM(MessageReceivedEvent event)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(
                                "Sorry, but you have no permission to perform this command!"
                        )
                        .build()
        ).queue();
    }

    // COMMAND EXECUTED
    public static void CommandExecuted(String cmd)
    {
        System.out.println(
                ConsoleColors.BLACK + "[" + ConsoleColors.YELLOW + "INFO" + ConsoleColors.BLACK + "] " + ConsoleColors.RESET
                        + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + cmd + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                        + "was executed!"
        );
    }

    // ERROR
    public static void ERROR_MSG(MessageReceivedEvent event, String content)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription(content)
                        .build()
        ).queue();
    }

    // NO TRACK IN QUEUE (for music command)
    public static MessageEmbed NO_TRACK = new EmbedBuilder()
            .setColor(Color.RED)
            .setDescription(
                    "There is no track in queue!"
            )
            .build();

    // --|| COMMANDS ||-- //
    public static final String COMMANDS =
            "              **WOLFBOT COMMANDS**              \n"
            + "\n***General commands***"
            + "\n`-game <number>` **->** Get awesome game roles! For more information use `-game info`\n"
            + "\n***Music commands***"
            + "\n`-m play <link/name>` **->** choose your favorite song! \n"
            + "\n`-m stop` **->** Stop the player and clear the playlist.\n"
            + "\n`-m skip` **->** Skip the current song.\n"
            + "\n`-m shuffle` **->** Toggle shuffle mode.\n"
            + "\n`-m info/track` **->** Display the currently playing song.\n"
            + "\n`-m queue` **->** Display the current queue.\n"
            + "\n`-m pause` **->** Pause the player.\n"
            + "\n`-m unpause` **->** Unpause the player.\n"
            + "\n***Vote commands***\n"
            + "`-vote create <title|<answer options>1|2|...>` **->** Create a poll.\n"
            + "\n`-vote v <number>` **->** Use to vote for a answer option.\n"
            + "\n`-vote stats` **->** Show the stats of the currently running poll.\n"
            + "\n`-vote close` **->** Close the current poll. (STUFF ONLY!)\n"
            + "\n";

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

}
