package de.bluewolf.wolfbot.commands;

import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CmdPing implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "ping";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.ADMINISTRATOR;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        event.getChannel().sendMessage(
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
