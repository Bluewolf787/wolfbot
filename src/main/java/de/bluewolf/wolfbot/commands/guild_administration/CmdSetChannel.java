package de.bluewolf.wolfbot.commands.guild_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.core.CommandHandler;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.guild_administration
 * @created 30/Dez/2020 - 10:56
 */
public class CmdSetChannel implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "setchannel");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        // Check if a command is entered
        if (args.length == 1)
        {
            String command = args[0].toLowerCase();
            // Check if the command exists
            if (CommandHandler.commands.containsKey(command))
            {
                // Get the channel ID
                String channelId = event.getChannel().getId();

                // Update channel for the command in CommandChannels table
                DatabaseHelper.update(
                        "UPDATE CommandChannels SET ChannelId = '" + channelId + "' " +
                                "WHERE GuildId = '" + guildId + "' AND Command = '" + command + "';"
                );

                event.getChannel().sendMessageEmbeds(
                        new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Channel established")
                        .setDescription(
                                "This channel is now established for the command ``" + BotSettings.PREFIX + command + "``"
                        )
                        .build()
                ).queue();
                executed(true, event);
            }
            else // Command doesn't exists
            {
                CustomMsg.CMD_ERROR(event, "setchannel", "The command '" + args[0] + "' doesn't exists");
                executed(false, event);
            }
        }
        // Check if a command and channel is entered
        else if (args.length == 2)
        {
            String command = args[0];
            String channelId = args[1].replaceAll("<#", "").replace(">", "");
            // Check if the command exists
            if (CommandHandler.commands.containsKey(command))
            {
                // Check if the channel exists
                if (guild.getChannels().contains(guild.getTextChannelById(channelId)))
                {
                    // Update channel for the command in CommandChannels table
                    DatabaseHelper.update(
                            "UPDATE CommandChannels SET ChannelId = '" + channelId + "' " +
                                    "WHERE GuildId = '" + guildId + "' AND Command = '" + command + "';"
                    );

                    event.getChannel().sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTitle("Channel established")
                                    .setDescription(
                                            "The channel " + Objects.requireNonNull(guild.getTextChannelById(channelId)).getName() +
                                                    " is now established for the command ``" + BotSettings.PREFIX + command + "``"
                                    )
                                    .build()
                    ).queue();
                    executed(true, event);
                }
                else // Guild doesn't contains the channel
                {
                    CustomMsg.CMD_ERROR(event, "setchannel", "The channel '" + args[1] + "' doesn't exists");
                    executed(false, event);
                }
            }
            else // Command doesn't exists
            {
                CustomMsg.CMD_ERROR(event, "setchannel", "The command '" + args[0] + "' doesn't exists");
                executed(false, event);
            }
        }
        else // Too many or too few arguments
        {
            executed(false, event);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "setchannel");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "setchannel <command>`` to set the current channel as execution channel for the specified command\n\n"
                + "Use ``" + BotSettings.PREFIX + "setchannel <command> <#channel>`` to set the specified channel as execution channel for the specified command";
    }
}
