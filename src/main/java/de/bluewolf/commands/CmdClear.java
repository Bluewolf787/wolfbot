package de.bluewolf.commands;

import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CmdClear implements Command
{

    EmbedBuilder error = new EmbedBuilder().setColor(Color.RED);

    private int getInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {

        if(Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR))
        {
            return false;
        } else {
            utils.NO_PERM(event);
            return true;
        }

    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        int num;

        if (args.length < 1)
        {
            utils.ERROR_MSG(event, "Please enter a amount of massages you want to delete!");
        }
        else
        {

            num = getInt(args[0]);

            if (num > 1 && num <= 100)
            {

                try {

                    MessageHistory history = new MessageHistory(event.getTextChannel());
                    List<Message> msgs;

                    event.getMessage().delete().queue();

                    msgs = history.retrievePast(num).complete();
                    event.getTextChannel().deleteMessages(msgs).queue();

                    Message msgDelete = event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setDescription(
                                            "Delete " + args[0] + " messages!"
                                    ).build()
                    ).complete();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            msgDelete.delete().queue();
                        }
                    }, 3000);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            {
                utils.ERROR_MSG(event, "Please enter a amount messages between 2 and 100!");
            }
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        utils.CommandExecuted("-clear");
    }

    @Override
    public String help() {
        return null;
    }
}
