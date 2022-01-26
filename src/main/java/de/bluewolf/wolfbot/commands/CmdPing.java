package de.bluewolf.wolfbot.commands;

import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;

public class CmdPing implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "ping");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        event.getChannel().sendMessageEmbeds(
                new EmbedBuilder().setColor(Color.GREEN).setDescription("Pong!").build()
        ).queue();

        executed(true, event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "ping");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "ping``. For more information use ``" + BotSettings.PREFIX + "help``.";
    }

}
