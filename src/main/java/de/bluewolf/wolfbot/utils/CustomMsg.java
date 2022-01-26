package de.bluewolf.wolfbot.utils;

import de.bluewolf.wolfbot.settings.BotSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
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

    private static final ConsoleColors colors = new ConsoleColors();

    // PREFIXES
    public static final String INFO_PREFIX =  colors.BLACK + "[" + colors.YELLOW_BOLD + "INFO" + colors.BLACK + "] " + colors.RESET;
    public static final String NO_PERM_PREFIX = colors.BLACK + "[" + colors.RED_BOLD + "NO PERM" + colors.BLACK + "] " + colors.RESET;
    public static final String ERROR_PREFIX = colors.BLACK + "[" + colors.RED_BOLD + "ERROR" + colors.BLACK + "] " + colors.RESET;

    public static final String TIMESTAMP = colors.GREEN + java.time.LocalDateTime.now() + colors.RESET;

    /**
     * Format a String with guild name and guild ID as GuildName (GuildId)
     * @param guildName The name of the guild
     * @param guildId The ID of the guild
     * @return Formatted String with guild name and guild ID
     */
    public static String GUILD_NAME(String guildName, String guildId)
    {
        return colors.CYAN + guildName + colors.RESET + " (" + colors.CYAN + guildId + ")" + colors.RESET;
    }

    /**
     * Send a message marked as info in the terminal
     * @param infoMessage Message which will be send
     */
    public static void INFO(String infoMessage)
    {
        System.out.println(
                INFO_PREFIX + infoMessage + colors.BOLD + " - " + colors.RESET
                + TIMESTAMP
        );
    }

    /**
     * Send a info message for a specific command in a Discord channel and the terminal
     * @param event MessageReceivedEvent
     * @param command The Command which were performed
     * @param infoMessage The message which will be send
     */
    public static void CMD_INFO(MessageReceivedEvent event, String command, String infoMessage)
    {
        event.getTextChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setDescription(infoMessage)
                        .build()
        ).queue();

        System.out.println(
                INFO_PREFIX + "Command " + colors.BLACK + "'" + colors.RED + BotSettings.PREFIX + command + colors.BLACK + "' " + colors.RESET
                + "was performed with info: " + colors.YELLOW + infoMessage + colors.RESET
                + colors.CYAN + " (" + event.getGuild().getName() + ") " + colors.RESET + "at " + TIMESTAMP
        );
    }

    /**
     * Sends a message in a Discord channel and the terminal
     * when a command were performed without permissions
     *  @param event MessageReceivedEvent
     * @param command The command which were performed
     */
    public static void NO_PERM(MessageReceivedEvent event, String command)
    {
        event.getTextChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(
                                "Sorry, but you have no permission to perform this command!"
                        )
                        .build()
        ).queue();

        System.out.println(
                NO_PERM_PREFIX + "Command " + colors.BLACK + "'" + colors.RED + BotSettings.PREFIX + command + colors.BLACK + "' " + colors.RESET
                        + "was performed with " + colors.RED + "no permissions " + colors.RESET
                        + colors.CYAN + "(" + event.getGuild().getName() + ") " + colors.RESET
                        + "at " + TIMESTAMP
        );
    }

    /**
     * Sends a message in the terminal when a command where successful performed
     * @param event MessageReceivedEvent
     * @param command The command which were performed
     */
    public static void COMMAND_EXECUTED(MessageReceivedEvent event, String command)
    {
        System.out.println(
                INFO_PREFIX + "Command " + colors.BLACK + "'" + colors.RED + BotSettings.PREFIX + command + colors.BLACK + "' " + colors.RESET
                        + "was executed " + colors.CYAN + "(" + event.getGuild().getName() + ") " + colors.RESET
                        + "at " + TIMESTAMP
        );
    }

    /**
     * Send a error message for a specific command in the Discord channel and the terminal
     * @param event MessageReceivedEvent
     * @param command The command which occurred the error
     * @param errorMessage The error message
     */
    public static void CMD_ERROR(MessageReceivedEvent event, String command, String errorMessage)
    {
        event.getTextChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(errorMessage)
                        .build()
        ).queue();

        System.out.println(
                ERROR_PREFIX + "Command " + colors.BLACK + "'" + colors.RED + BotSettings.PREFIX + command + colors.BLACK + "' " + colors.RESET
                        + "was performed while error: '" + colors.RED + errorMessage + colors.RESET + "' occurred! "
                        + colors.CYAN + "(" + event.getGuild().getName() + ") " + colors.RESET
                        + "Performed at " + TIMESTAMP
        );
    }

    /**
     * Send a error message in the terminal
     * @param error The error message
     */
    public static void ERROR(String error)
    {
        System.out.println(
                ERROR_PREFIX + error + colors.BOLD + " - " + colors.RESET
                + TIMESTAMP
        );
    }

    /**
     * Send a message in a Discord channel when
     * a specific command was performed in the wrong channel
     * @param usedChannel The channel where the command was performed
     * @param providedChannel The channel which is provided to perform the command
     * @param command The command which was performed
     */
    public static void WRONG_CHANNEL(TextChannel usedChannel, TextChannel providedChannel, String command)
    {
        usedChannel.sendMessageEmbeds(
                new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Tried to perform a command in the wrong channel")
                .setDescription(
                        "The command ``" + BotSettings.PREFIX + command +
                        "`` can't be performed in this channel (" + usedChannel.getAsMention() + ").\n" +
                        "You can use this command in " + providedChannel.getAsMention()
                )
                        .build()
        ).queue();
    }

    /**
     * Sends a message in a Discord channel with information about a specific command
     * @param event MessageReceivedEvent
     * @param helpMessage The message which will be send
     */
    public static void HELP_MSG(MessageReceivedEvent event, String helpMessage)
    {
        event.getTextChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(new Color(0xdf1196))
                        .setTitle("Command Help")
                        .setDescription(helpMessage)
                        .build()
        ).queue();
    }

    /**
     * Sends a message in a Discord channel and the terminal
     * when a command that doesn't exists are performed
     * @param event
     * @param cmd
     */
    public static void UNKNOWN_CMD(MessageReceivedEvent event, String cmd)
    {
        event.getTextChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription("Can not find the command ``" + cmd + "``")
                        .build()
        ).queue();

        System.out.println(
                ERROR_PREFIX + "Unknown command: '" + colors.RED + cmd + "' " + colors.RESET
                        + colors.CYAN + "(" + event.getGuild().getName() + ") " + colors.RESET
                        + "performed at " + TIMESTAMP
        );
    }

    // No track in queue error message (for music command)
    public static MessageEmbed NO_TRACK = new EmbedBuilder()
            .setColor(Color.RED)
            .setDescription(
                    "There is no track in queue!"
            )
            .build();

    /**
     * Sends a normal direct message to a user
     * @param user The user who will received the message
     * @param directMessage The message which will be send
     */
    public static void sendPrivateMessage(User user, String directMessage)
    {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(directMessage))
                .queue();
    }

    /**
     * Sends a direct message as MessageEmbed to a user
     * @param user The user who will received the message
     * @param directMessage The content of MessageEmbed which will be send
     */
    public static void sendPrivateEmbedMessage(User user, MessageEmbed directMessage)
    {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(directMessage))
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
