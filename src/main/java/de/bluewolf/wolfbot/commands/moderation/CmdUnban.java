package de.bluewolf.wolfbot.commands.moderation;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import de.bluewolf.wolfbot.utils.ModerationUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class CmdUnban implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException {
        String command = "unban";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.BAN_MEMBERS;

        return BotSettings.PERMISSIONS(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        // If there are at least to arguments
        if (args.length >= 1)
        {
            executed(true, event);
            ModerationUtil.unban(event, event.getGuild(), args[0], event.getAuthor());
        }
        else
        {
            CustomMsg.CMD_ERROR(event, "unban", "No UserId entered");
            executed(false, event);
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "unban");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "unban <UserId>``. For more information use ``" + BotSettings.PREFIX + "help s``.";
    }

}
