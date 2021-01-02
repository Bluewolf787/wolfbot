package de.bluewolf.wolfbot.commands.bot_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class CmdNews implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "news";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.ADMINISTRATOR;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setDescription(
                                "\t**NEW VERSION: " + BotSettings.VERSION + "**\t\n\n WolfBot is getting new features"
                        )
                        //.setFooter("Enter '" + BotSettings.PREFIX + "wolfbot' for more information.")
                        .build()
        ).queue();
        event.getMessage().delete().queue();

        executed(true, event);

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "news");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "news``. For more information use ``" + BotSettings.PREFIX + "help s``.";
    }

}
