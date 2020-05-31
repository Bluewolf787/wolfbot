package de.bluewolf.commands;

import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class CmdHelp implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(0x288AB8))
                        .setDescription(utils.COMMANDS)
                        .build()
        ).queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        utils.CommandExecuted("-help");
    }

    @Override
    public String help()
    {
        return null;
    }
}
