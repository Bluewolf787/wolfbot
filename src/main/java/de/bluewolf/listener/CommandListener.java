package de.bluewolf.listener;

import de.bluewolf.core.commandHandler;
import de.bluewolf.core.commandParser;
import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.text.ParseException;

public class CommandListener extends ListenerAdapter
{

    public void onMessageReceived(MessageReceivedEvent event)
    {

        if(event.getMessage().getContentRaw().startsWith(utils.PREFIX)
                && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getAuthor().isBot())
        {
            try {
                commandHandler.handleCommand(commandParser.parser(event.getMessage().getContentRaw(), event));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

    }

}
