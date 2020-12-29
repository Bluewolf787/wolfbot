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

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.listener
 * @created 18/Dez/2020 - 22:27
 */
public class BotGuildListener extends ListenerAdapter
{

    public void onGuildJoin(GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        String gName = guild.getName();
        String gId  = guild.getId();

        CustomMsg.INFO(
                "Joined new guild " + ConsoleColors.CYAN + gName + ConsoleColors.RESET
                + " (" + ConsoleColors.CYAN + gId + ConsoleColors.RESET + ")"
        );

        // Add guild to BotStats table in DB
        DatabaseHelper.update(
                "INSERT INTO botstats (GuildId, GuildName, Member, Region, Available, Password) VALUES" +
                " ('" + gId + "', '" + gName + "', "+ guild.getMemberCount() + ", '" + guild.getRegionRaw() + "', 1, '" + PasswordGenerator.generatePassword() + "');"
        );

        // Add guild to permissions table with default permissions
        DatabaseHelper.insertGuildIntoPermissionsTable(guild.getId(), BotSettings.commandsWithPermissions);
    }

    public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event)
    {

        String guildId = event.getGuildId();

        CustomMsg.INFO(
                "Joined new guild " + ConsoleColors.CYAN + "Unavailable" + ConsoleColors.RESET
                        + " (" + ConsoleColors.CYAN + guildId + ConsoleColors.RESET + ")"
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
                "Left guild " + ConsoleColors.CYAN + gName + ConsoleColors.RESET
                        + " (" + ConsoleColors.CYAN + guildId + ConsoleColors.RESET + ")"
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
                "Left guild " + ConsoleColors.CYAN + "Unavailable" + ConsoleColors.RESET
                        + " (" + ConsoleColors.CYAN + guildId + ConsoleColors.RESET + ")"
        );

        // Remove guild from BotStats table
        DatabaseHelper.update("DELETE FROM botstats WHERE GuildId = '" + guildId + "';");

        // Remove guild from Permissions table
        DatabaseHelper.deleteGuildFromPermissionsTable(guildId, BotSettings.commandsWithPermissions);
    }

}
