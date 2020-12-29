package de.bluewolf.wolfbot.utils;

import de.bluewolf.wolfbot.settings.BotSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.settings
 * @created 15/Dez/2020 - 17:52
 */
public class CustomMsg
{

    // PREFIXES
    public static final String INFO_PREFIX =  ConsoleColors.BLACK + "[" + ConsoleColors.YELLOW_BOLD + "INFO" + ConsoleColors.BLACK + "] " + ConsoleColors.RESET;
    public static final String NO_PERM_PREFIX = ConsoleColors.BLACK + "[" + ConsoleColors.RED_BOLD + "NO PERM" + ConsoleColors.BLACK + "] " + ConsoleColors.RESET;
    public static final String ERROR_PREFIX = ConsoleColors.BLACK + "[" + ConsoleColors.RED_BOLD + "ERROR" + ConsoleColors.BLACK + "] " + ConsoleColors.RESET;

    public static final String TIMESTAMP = ConsoleColors.GREEN + java.time.LocalDateTime.now() + ConsoleColors.RESET;

    // Info
    public static void INFO(String info)
    {
        System.out.println(
                INFO_PREFIX + info + ConsoleColors.BOLD + " - " + ConsoleColors.RESET
                + TIMESTAMP
        );
    }

    public static void CMD_INFO(MessageReceivedEvent event, String cmd, String info)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setDescription(info)
                        .build()
        ).queue();

        System.out.println(
                INFO_PREFIX + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + BotSettings.PREFIX + cmd + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                + "was performed with info: " + ConsoleColors.YELLOW + info + ConsoleColors.RESET
                + ConsoleColors.CYAN + " (" + event.getGuild().getName() + ") " + ConsoleColors.RESET + "at " + TIMESTAMP
        );
    }

    // NO PERM
    public static void NO_PERM(MessageReceivedEvent event, String cmd)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(
                                "Sorry, but you have no permission to perform this command!"
                        )
                        .build()
        ).queue();

        System.out.println(
                NO_PERM_PREFIX + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + BotSettings.PREFIX + cmd + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                        + "was performed with " + ConsoleColors.RED + "no permissions " + ConsoleColors.RESET
                        + ConsoleColors.CYAN + "(" + event.getGuild().getName() + ") " + ConsoleColors.RESET
                        + "at " + TIMESTAMP
        );
    }

    // COMMAND EXECUTED
    public static void COMMAND_EXECUTED(MessageReceivedEvent event, String cmd)
    {
        System.out.println(
                INFO_PREFIX + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + BotSettings.PREFIX + cmd + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                        + "was executed " + ConsoleColors.CYAN + "(" + event.getGuild().getName() + ") " + ConsoleColors.RESET
                        + "at " + TIMESTAMP
        );
    }

    // ERROR
    public static void CMD_ERROR(MessageReceivedEvent event, String cmd, String error)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(error)
                        .build()
        ).queue();

        System.out.println(
                ERROR_PREFIX + "Command " + ConsoleColors.BLACK + "'" + ConsoleColors.RED + BotSettings.PREFIX + cmd + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                        + "was performed while error: '" + ConsoleColors.RED + error + ConsoleColors.RESET + "' occurred! "
                        + ConsoleColors.CYAN + "(" + event.getGuild().getName() + ") " + ConsoleColors.RESET
                        + "Performed at " + TIMESTAMP
        );
    }

    public static void ERROR(String error)
    {
        System.out.println(
                ERROR_PREFIX + error + ConsoleColors.BOLD + " - " + ConsoleColors.RESET
                + TIMESTAMP
        );
    }

    // Help
    public static void HELP_MSG(MessageReceivedEvent event, String content)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription(content)
                        .build()
        ).queue();
    }

    // Unknown command
    public static void UNKNOWN_CMD(MessageReceivedEvent event, String cmd)
    {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("Can not find the command ``" + cmd + "``")
                        .build()
        ).queue();

        System.out.println(
                ERROR_PREFIX + "Unknown command: '" + ConsoleColors.RED + cmd + "' " + ConsoleColors.RESET
                        + ConsoleColors.CYAN + "(" + event.getGuild().getName() + ") " + ConsoleColors.RESET
                        + "performed at " + TIMESTAMP
        );
    }

    // No track in queue (for music command)
    public static MessageEmbed NO_TRACK = new EmbedBuilder()
            .setColor(Color.RED)
            .setDescription(
                    "There is no track in queue!"
            )
            .build();

    // Send direct msg
    public static void sendPrivateMessage(User user, String content)
    {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    public static void sendPrivateEmbedMessage(User user, MessageEmbed message)
    {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(message))
                .queue();
    }

    // --|| COMMANDS ||-- //
    public static MessageEmbed COMMANDS = new EmbedBuilder()
            .setColor(new Color(0x288AB8))
            .setTitle("**WOLFBOT COMMANDS**")
            .setDescription(
                    "\n***General commands***"
                            + "\n`" + BotSettings.PREFIX + "game <number>` **->** Get awesome game roles! For more information use `-game info`\n"
                            + "\n***Music commands***"
                            + "\n`" + BotSettings.PREFIX + "m play <link/name>` **->** choose your favorite song! \n"
                            + "\n`" + BotSettings.PREFIX + "m stop` **->** Stop the player and clear the playlist.\n"
                            + "\n`" + BotSettings.PREFIX + "m skip` **->** Skip the current song.\n"
                            + "\n`" + BotSettings.PREFIX + "m shuffle` **->** Toggle shuffle mode.\n"
                            + "\n`" + BotSettings.PREFIX + "m info/track` **->** Display the currently playing song.\n"
                            + "\n`" + BotSettings.PREFIX + "m queue` **->** Display the current queue.\n"
                            + "\n`" + BotSettings.PREFIX + "m pause` **->** Pause the player.\n"
                            + "\n`" + BotSettings.PREFIX + "m unpause` **->** Unpause the player.\n"
                            + "\n***Vote commands***\n"
                            + "`" + BotSettings.PREFIX + "vote create <title|<answer options>1|2|...>` **->** Create a poll.\n"
                            + "\n`" + BotSettings.PREFIX + "vote v <number>` **->** Use to vote for a answer option.\n"
                            + "\n`" + BotSettings.PREFIX + "vote stats` **->** Show the stats of the currently running poll.\n"
                            + "\n`" + BotSettings.PREFIX + "vote close` **->** Close the current poll. (only staff and poll creator)\n"
                            + "\n"
            )
            .setFooter("For stuff member use `" + BotSettings.PREFIX + "help s`")
            .build();

    public static MessageEmbed STAFF_COMMANDS = new EmbedBuilder()
            .setColor(new Color(0xF69D31))
            .setTitle("**WOLFBOT STAFF COMMANDS**")
            .setDescription(
                    "\n***Administrator commands***"
                            + "\n`" + BotSettings.PREFIX + "news` **->** Send a message with the changelog for WOLFBOT\n"
                            + "\n`" + BotSettings.PREFIX + "settings <running/working/downtime/stop>` **->** Change the status of WOLFBOT (only for the developer)\n"
                            + "\n***Moderation commands***"
                            + "\n`" + BotSettings.PREFIX + "clear <Amount Of Messages>` **->** Clear the last messages of your amount (2-100). \n"
                            + "\n`" + BotSettings.PREFIX + "kick <UserId>` **->** Kick a member from the guild. \n"
                            + "\n`" + BotSettings.PREFIX + "ban <UserId> <reason>` **->** Ban a member from the guild.\n"
                            + "\n`" + BotSettings.PREFIX + "unban <UserId>` **->** Unban a user from the guild.\n"
                            + "\n"
            )
            .build();

}
