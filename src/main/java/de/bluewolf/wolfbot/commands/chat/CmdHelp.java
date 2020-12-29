package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

public class CmdHelp implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "help";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.MESSAGE_WRITE;

        return BotSettings.PERMISSIONS(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        if (args.length == 0)
        {
            CustomMsg.sendPrivateEmbedMessage(event.getAuthor(), CustomMsg.COMMANDS);
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " I have sent you a direct message with all information.").queue();
            executed(true, event);
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("s")
                && Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.MESSAGE_MANAGE))
        {
            CustomMsg.sendPrivateEmbedMessage(event.getAuthor(), CustomMsg.STAFF_COMMANDS);
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " I have sent you a direct message with all information.").queue();
            executed(true, event);
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