package de.bluewolf.commands;

import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

public class CmdNews implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {

        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR))
        {
            return false;
        } else
        {
            utils.NO_PERM(event);
            return true;
        }

    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {


        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setDescription(
                                "          **NEW VERSION: " + utils.VERSION + "**          \n\n"
                                        + "***Änderungen:***"
                                        + "\n- Der Commandprefix wurde von `~` zu `-` geändert."
                                        + "\n- Der Command `-role` wurde durch `-game` ersetzt."
                                        + "\n- Gameroles wurden überarbeitet. Weitere Informationen mit `-game info`."
                        )
                        .build()
        ).queue();
        event.getMessage().delete().queue();

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        utils.CommandExecuted("-news");
    }

    @Override
    public String help()
    {
        return null;
    }

}
