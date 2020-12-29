package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.core.CommandHandler;
import de.bluewolf.wolfbot.core.CommandParser;
import de.bluewolf.wolfbot.settings.BotSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class CommandListener extends ListenerAdapter
{

    public void onMessageReceived(MessageReceivedEvent event)
    {

        if(event.getMessage().getContentRaw().startsWith(BotSettings.PREFIX)
                && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getAuthor().isBot()
                && !event.getChannelType().equals(ChannelType.PRIVATE))
        {
            try {
                CommandHandler.handleCommand(CommandParser.parser(event.getMessage().getContentRaw(), event));
            } catch (IOException | ParseException | SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
