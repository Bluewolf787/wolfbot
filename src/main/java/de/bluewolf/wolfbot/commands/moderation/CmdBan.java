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

public class CmdBan implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "ban";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.BAN_MEMBERS;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        StringBuilder reason = new StringBuilder();

        // If there are at least to arguments
        if (args.length >= 2)
        {
            for (int i = 1; i <= args.length - 1; i++) {
                reason.append(args[i]).append(" ");
            }

            executed(true, event);
            ModerationUtil.ban(event, event.getGuild(), args[0], event.getAuthor(), reason.toString());
        }
        else
        {
            CustomMsg.CMD_ERROR(event, "ban","No UserId and reason entered");
            executed(false, event);
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "ban");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "ban <UserId> <reason>``. For more information use ``" + BotSettings.PREFIX + "help s``.";
    }

}
