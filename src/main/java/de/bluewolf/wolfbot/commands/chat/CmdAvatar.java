package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.chat
 * @created 30/Dez/2020 - 11:02
 */
public class CmdAvatar implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "avatar");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        Guild guild = event.getGuild();

        // Check if a user is entered
        if (args.length == 1)
        {
            // Check if the member is on the guild
            if (guild.getMembers().contains(guild.getMemberByTag(args[0])))
            {
                Member member = guild.getMemberByTag(args[0]);
                assert member != null;

                // Get the avatar URL
                String avatarURL = member.getUser().getAvatarUrl();
                // Send message
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                        .setColor(Color.MAGENTA)
                        .setTitle("Avatar URL of " + member.getUser().getName())
                        .setDescription("Avatar URL: " + avatarURL)
                        .setThumbnail(avatarURL)
                        .build()
                ).queue();

                executed(true, event);
            }
            else // User isn't on the guild or is offline
            {
                CustomMsg.CMD_ERROR(event, "role", "The user '" + args[0] + "' isn't member on this guild or is offline");
                executed(false, event);
            }
        }
        else // No user entered
        {
            CustomMsg.CMD_ERROR(event, "clear", "No user entered");
            executed(false, event);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "avatar");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "avatar <usertag>`` to get the avatar URL of a user";
    }
}
