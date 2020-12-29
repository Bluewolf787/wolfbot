package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.ConsoleColors;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import de.bluewolf.wolfbot.utils.PasswordGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadyListener extends ListenerAdapter
{

    public void onReady(ReadyEvent event)
    {
        StringBuilder out = new StringBuilder();

        out.append("\n============================================================\n");

        out.append(CustomMsg.INFO_PREFIX + "Logged in as ").append(ConsoleColors.YELLOW_BOLD).append(event.getJDA().getSelfUser().getAsTag()).append(ConsoleColors.RESET)
                .append(" at ").append(ConsoleColors.GREEN).append(java.time.LocalDateTime.now()).append(ConsoleColors.RESET)
                .append(". This Bot is running on following servers: \n\n");

        // List all guilds the Bot is running on
        for (Guild guild : event.getJDA().getGuilds())
        {
            String guildId = guild.getId();
            String guildName = guild.getName();
            String guildRegion = guild.getRegionRaw();
            int guildMember = guild.getMemberCount();

            out.append("\t" + ConsoleColors.CYAN).append(guildName).append(ConsoleColors.RESET)
                    .append(" (").append(ConsoleColors.CYAN).append(guildId).append(ConsoleColors.RESET)
                    .append(") \n\t---------------------------- \n");

            // Check if the guild already exists in the table BotStats
            ResultSet guildFromBotStats = DatabaseHelper.query("SELECT GuildId FROM botstats WHERE GuildId = '" + guildId + "';");
            try {
                if (guildFromBotStats.next())
                {
                    // If exists: update
                    // Check if guild is available
                    if (event.getJDA().isUnavailable(guild.getIdLong()))
                    {
                        // Update unavailable guild in BotStats table
                        DatabaseHelper.update(
                                "UPDATE botstats SET Available = 0 WHERE GuildId = '" + guildId + "';"
                        );
                    }
                    else
                    {
                        // Update available guild in BotStats table
                        DatabaseHelper.update(
                                "UPDATE botstats SET GuildName = '" + guildName + "', Member = " + guildMember + "," +
                                        " Region = '" + guildRegion + "', Available = 1 WHERE GuildId = '" + guildId + "';"
                        );
                    }
                }
                else
                {
                    // If does not exists: insert
                    // Check if guild is available
                    if (event.getJDA().isUnavailable(guild.getIdLong()))
                    {
                        // Insert unavailable guild in BotStats table
                        DatabaseHelper.update(
                                "INSERT INTO botstats (GuildId, Available, Password) VALUES" +
                                        " ('" + guildId + "', 1, '" + PasswordGenerator.generatePassword() + "');"
                        );
                    }
                    else
                    {
                        // Insert available guild in BotStats table
                        DatabaseHelper.update(
                                "INSERT INTO botstats (GuildId, GuildName, Member, Region, Available, Password) VALUES" +
                                        " ('" + guildId + "', '" + guildName + "', "+ guildMember + ", '" + guildRegion + "', 1, '" + PasswordGenerator.generatePassword() + "');"
                        );
                    }

                    // Insert into Permissions table with default permissions
                    DatabaseHelper.insertGuildIntoPermissionsTable(guildId, BotSettings.commandsWithPermissions);
                }
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("SQL exception while trying to update the table BotStats. Guild ID: " + ConsoleColors.CYAN + guildId + ConsoleColors.RESET);
                sqlException.printStackTrace();
            }
        }

        // Print the total number of guilds the Bot is running on
        out.append(ConsoleColors.BOLD + "\tTotal: ").append(event.getGuildTotalCount()).append(ConsoleColors.RESET);

        out.append("\n============================================================\n");

        // List available and unavailable guilds
        out.append(CustomMsg.INFO_PREFIX + "Available guilds: ").append(ConsoleColors.GREEN_BOLD).append(event.getGuildAvailableCount()).append(ConsoleColors.RESET)
                .append(" | ").append(ConsoleColors.RED_BOLD).append(event.getGuildUnavailableCount()).append(ConsoleColors.RESET);

        out.append("\n============================================================\n");

        System.out.println(out);

    }

}
