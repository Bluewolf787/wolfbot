package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.core.CommandHandler;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

public class CmdHelp implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "help");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (args.length == 0)
        {
            //CustomMsg.sendPrivateEmbedMessage(event.getAuthor(), CustomMsg.COMMANDS);
            StringBuilder commandsHelpMessage = new StringBuilder();
            for (String i : CommandHandler.commands.keySet())
            {
                commandsHelpMessage.append(" - ``").append(BotSettings.PREFIX).append(i).append("``\n");
            }
            MessageEmbed commandsHelpMessageEmbed = new EmbedBuilder()
                    .setColor(new Color(0xdf1196))
                    .setTitle("WolfBot commands help page")
                    .setDescription(commandsHelpMessage.toString() + "\n\nThis page will be updated soon!")
                    .build();

            CustomMsg.sendPrivateEmbedMessage(event.getAuthor(), commandsHelpMessageEmbed);
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " I have sent you a direct message with all information.").queue();
            executed(true, event);
        }
        else if (args.length == 1 && CommandHandler.commands.containsKey(args[0]))
        {
            CustomMsg.HELP_MSG(event, CommandHandler.commands.get(args[0]).help());
        }
        else
        {
            CustomMsg.CMD_ERROR(event,"help", "Zero or one positional argument expected but more found");
            executed(false, event);
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "help");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "help`` to show all commands.";
    }
}
