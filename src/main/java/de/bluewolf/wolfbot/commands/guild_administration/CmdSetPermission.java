package de.bluewolf.wolfbot.commands.guild_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.Permissions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.guild_administration
 * @created 29/Dez/2020 - 14:55
 */
public class CmdSetPermission implements Command
{
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        return Permissions.check(event, "setpermission");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {

    }

    @Override
    public String help()
    {
        return null;
    }
}
