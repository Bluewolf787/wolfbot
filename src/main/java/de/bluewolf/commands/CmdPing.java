package de.bluewolf.commands;

import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Objects;

public class CmdPing implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {
        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR))
        {
            return false;
        }
        else
        {
            utils.NO_PERM(event);
            return true;
        }
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        event.getChannel().sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription("Pong!").build()
        ).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        utils.CommandExecuted("-ping");
    }

    @Override
    public String help()
    {
        return null;
    }

}
