package de.bluewolf.wolfbot.commands.moderation;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CmdClear implements Command
{

    private int getInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "clear");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        int num;

        if (args.length < 1)
        {
            CustomMsg.CMD_ERROR(event, "clear", "No amount of messages to delete entered");
            executed(false, event);
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

                    Message msgDelete = event.getTextChannel().sendMessageEmbeds(
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

                executed(true, event);

            }
            else
            {
                CustomMsg.CMD_ERROR(event, "clear", "Amount of messages out of range (between 2 and 100)");
                executed(false, event);
            }
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "clear");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "clear <Amount Of Messages>``. For more information use ``" + BotSettings.PREFIX + "help s``.";
    }
}
