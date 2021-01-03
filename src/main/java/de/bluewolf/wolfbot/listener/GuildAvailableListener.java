package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.entities.Guild;
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
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        ResultSet guildAvailable = DatabaseHelper.query("SELECT Available FROM Guilds WHERE GuildId = '" + guildId + "';");

        // Update available status in Guilds table
        try
        {
            if (guildAvailable.next())
            {
                if (guildAvailable.getInt("Available") == 0)
                {
                    DatabaseHelper.update("UPDATE Guilds SET Available = 1 WHERE GuildId = '" + guildId + "';");
                    CustomMsg.INFO(event.getGuild().getName() + " is back available");
                }
            }
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        // Create Staff role on guild and add to DB, when not exists
        try {
            BotSettings.createStaffRole(guild, guildId);
        } catch (SQLException sqlException) {
            CustomMsg.ERROR("Failed to create Staff role on the guild " + CustomMsg.GUILD_NAME(guild.getName(), guildId));
        }
    }

    public void onGuildUnavailable(GuildUnavailableEvent event)
    {
        String guildId = event.getGuild().getId();

        ResultSet guildAvailable = DatabaseHelper.query("SELECT Available FROM Guilds WHERE GuildId = '" + guildId + "';");

        // Update available status in Guilds table
        try
        {
            if (guildAvailable.next())
            {
                if (guildAvailable.getInt("Available") == 1)
                {
                    DatabaseHelper.update("UPDATE Guilds SET Available = 0 WHERE GuildId = '" + guildId + "';");
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
