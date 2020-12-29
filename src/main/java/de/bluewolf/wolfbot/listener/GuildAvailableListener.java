package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.listener
 * @created 27/Dez/2020 - 00:00
 */
public class GuildAvailableListener extends ListenerAdapter
{


    public void onGuildAvailable(GuildAvailableEvent event)
    {
        String guildId = event.getGuild().getId();

        ResultSet guildAvailable = DatabaseHelper.query("SELECT Available FROM BotStats WHERE GuildId = '" + guildId + "';");

        try
        {
            if (guildAvailable.next())
            {
                if (guildAvailable.getInt("Available") == 0)
                {
                    DatabaseHelper.update("UPDATE BotStats SET Available = 1 WHERE GuildId = '" + guildId + "';");
                    CustomMsg.INFO(event.getGuild().getName() + " is back available");
                }
            }
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }

    public void onGuildUnavailable(GuildUnavailableEvent event)
    {
        String guildId = event.getGuild().getId();

        ResultSet guildAvailable = DatabaseHelper.query("SELECT Available FROM BotStats WHERE GuildId = '" + guildId + "';");

        try
        {
            if (guildAvailable.next())
            {
                if (guildAvailable.getInt("Available") == 1)
                {
                    DatabaseHelper.update("UPDATE BotStats SET Available = 0 WHERE GuildId = '" + guildId + "';");
                    CustomMsg.INFO(event.getGuild().getName() + " is now unavailable");
                }
            }
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }

}
