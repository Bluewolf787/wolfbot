package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.ConsoleColors;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import de.bluewolf.wolfbot.utils.PasswordGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.listener
 * @created 18/Dez/2020 - 22:27
 */
public class BotGuildListener extends ListenerAdapter
{

    private static final ConsoleColors colors = new ConsoleColors();

    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        String guildName = guild.getName();
        String guildId  = guild.getId();

        CustomMsg.INFO(
                "Joined new guild " + colors.CYAN + guildName + colors.RESET
                + " (" + colors.CYAN + guildId + colors.RESET + ")"
        );

        // Add guild to BotStats table in DB
        DatabaseHelper.update(
                "INSERT INTO botstats (GuildId, GuildName, Member, Region, Available, Password) VALUES" +
                " ('" + guildId + "', '" + guildName + "', "+ guild.getMemberCount() + ", '" + guild.getRegionRaw() + "', 1, '" + PasswordGenerator.generatePassword() + "');"
        );

        // Add guild to permissions table with default permissions
        DatabaseHelper.insertGuildIntoPermissionsTable(guild.getId(), BotSettings.commandsWithPermissions);

        // Create Staff role on guild and add to DB
        try {
            BotSettings.createStaffRole(guild, guildId);
        } catch (SQLException sqlException) {
            CustomMsg.ERROR("Failed to create Staff role on the guild " + CustomMsg.GUILD_NAME(guildName, guildId));
        }
    }

    public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event)
    {

        String guildId = event.getGuildId();

        CustomMsg.INFO(
                "Joined new guild " + colors.CYAN + "Unavailable" + colors.RESET
                        + " (" + colors.CYAN + guildId + colors.RESET + ")"
        );

        // Create guild based tables in DB
        DatabaseHelper.update("INSERT INTO botstats (GuildId, Available, Password) VALUES ('" + guildId + "', 0, '" + PasswordGenerator.generatePassword() + "');");

        // Add guild to permissions table with default permissions
        DatabaseHelper.insertGuildIntoPermissionsTable(guildId, BotSettings.commandsWithPermissions);
    }

    public void onGuildLeave(GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        String gName = guild.getName();
        String guildId  = guild.getId();

        CustomMsg.INFO(
                "Left guild " + colors.CYAN + gName + colors.RESET
                        + " (" + colors.CYAN + guildId + colors.RESET + ")"
        );

        // Remove guild from BotStats table
        DatabaseHelper.update("DELETE FROM botstats WHERE GuildId = '" + guildId + "';");

        // Remove guild from Permissions table
        DatabaseHelper.deleteGuildFromPermissionsTable(guildId, BotSettings.commandsWithPermissions);
    }

    public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event)
    {
        String guildId  = event.getGuildId();

        CustomMsg.INFO(
                "Left guild " + colors.CYAN + "Unavailable" + colors.RESET
                        + " (" + colors.CYAN + guildId + colors.RESET + ")"
        );

        // Remove guild from BotStats table
        DatabaseHelper.update("DELETE FROM botstats WHERE GuildId = '" + guildId + "';");

        // Remove guild from Permissions table
        DatabaseHelper.deleteGuildFromPermissionsTable(guildId, BotSettings.commandsWithPermissions);
    }

}
