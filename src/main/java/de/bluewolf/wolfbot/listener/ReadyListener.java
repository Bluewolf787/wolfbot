package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.core.CommandHandler;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadyListener extends ListenerAdapter
{

    private static final ConsoleColors colors = new ConsoleColors();

    public void onReady(ReadyEvent event)
    {
        StringBuilder out = new StringBuilder();

        out.append("\n============================================================\n");

        out.append(CustomMsg.INFO_PREFIX).append("Logged in as ").append(colors.YELLOW_BOLD).append(event.getJDA().getSelfUser().getAsTag()).append(colors.RESET)
                .append(" at ").append(colors.GREEN).append(java.time.LocalDateTime.now()).append(colors.RESET)
                .append(". This Bot is running on following servers: \n\n");

        // List all guilds the Bot is running on
        for (Guild guild : event.getJDA().getGuilds())
        {
            String guildId = guild.getId();
            String guildName = guild.getName();
            String guildRegion = guild.getRegionRaw();
            int guildMember = guild.getMemberCount();

            out.append("\t").append(CustomMsg.GUILD_NAME(guildName, guildId)).append("\n\t---------------------------- \n");

            // Check if the guild already exists in the table Guilds
            ResultSet guildFromGuilds = DatabaseHelper.query("SELECT GuildId FROM Guilds WHERE GuildId = '" + guildId + "';");
            try {
                if (guildFromGuilds.next())
                {
                    // If exists: update
                    // Check if guild is available
                    if (event.getJDA().isUnavailable(guild.getIdLong()))
                    {
                        // Update unavailable guild in Guilds table
                        DatabaseHelper.update(
                                "UPDATE Guilds SET Available = 0 WHERE GuildId = '" + guildId + "';"
                        );
                    }
                    else
                    {
                        // Update available guild in Guilds table
                        DatabaseHelper.update(
                                "UPDATE Guilds SET GuildName = '" + guildName + "', Member = " + guildMember + "," +
                                        " Region = '" + guildRegion + "', Available = 1 WHERE GuildId = '" + guildId + "';"
                        );
                    }

                    // -- Add new commands with the default permissions to the Permissions table //

                    // Get all commands from Permissions table
                    ResultSet getGuildCommandPermission = DatabaseHelper.query(
                            "SELECT Command FROM Permissions WHERE GuildId = '" + guildId + "';"
                    );

                    // Put all commands from Permissions table in the ArrayList commands
                    List<String> commands = new ArrayList<>();
                    while (getGuildCommandPermission.next())
                    {
                        commands.add(getGuildCommandPermission.getString("Command"));
                    }

                    // Copy all commands with the permissions in the new HashMap newCommandsWithPermissions
                    HashMap<String, Integer> newCommandsWithPermissions = new HashMap<>(BotSettings.commandsWithPermissions);
                    // Remove all entries from the new HashMap, which are in the commands ArrayList
                    for (String command : commands)
                    {
                        newCommandsWithPermissions.remove(command);
                    }
                    // Check if the new HashMap newCommandsWithPermissions is Empty
                    if (!newCommandsWithPermissions.isEmpty())
                    {
                        // If not add the new commands with permissions to the Permissions table
                        DatabaseHelper.insertGuildIntoPermissionsTable(guildId, newCommandsWithPermissions);
                    }

                    // -- Add new commands to the CommandChannels table //

                    // Get all commands from the CommandChannels table
                    ResultSet getCommandsFromCommandChannels = DatabaseHelper.query(
                            "SELECT Command FROM CommandChannels WHERE GuildId = '" + guildId + "';"
                    );

                    // Put all commands from CommandChannels table in the ArrayList commands
                    List<String> commandsFromCommandChannels = new ArrayList<>();
                    while (getCommandsFromCommandChannels.next())
                    {
                        commandsFromCommandChannels.add(getCommandsFromCommandChannels.getString("Command"));
                    }

                    // Copy all commands in a new HashMap newCommands
                    HashMap<String, Command> newCommands = new HashMap<>(CommandHandler.commands);
                    // Remove all entries from the new HashMap newCommands, which are in the commands ArrayList
                    for (String command : commandsFromCommandChannels)
                    {
                        newCommands.remove(command);
                    }

                    // Check if the new HashMap newCommands is Empty
                    if (!newCommands.isEmpty())
                    {
                        // If not add the new commands to the CommandChannels table
                        DatabaseHelper.insertGuildIntoCommandChannelsTable(guildId, newCommands);
                    }

                }
                else // Guild doesn't exists in Guilds table
                {
                    // If does not exists: insert
                    // Check if guild is available
                    if (event.getJDA().isUnavailable(guild.getIdLong())) // Guild is unavailable
                    {
                        // Insert unavailable guild in Guilds table
                        DatabaseHelper.update(
                                "INSERT INTO Guilds (GuildId, Available, Password) VALUES" +
                                        " ('" + guildId + "', 1, '" + PasswordGenerator.generatePassword() + "');"
                        );
                    }
                    else // Guild is available
                    {
                        // Insert available guild in Guilds table
                        DatabaseHelper.update(
                                "INSERT INTO Guilds (GuildId, GuildName, Member, Region, Available, Password) VALUES" +
                                        " ('" + guildId + "', '" + guildName + "', "+ guildMember + ", '" + guildRegion + "', 1, '" + PasswordGenerator.generatePassword() + "');"
                        );
                    }

                    // Insert guild into Permissions table with default permissions
                    DatabaseHelper.insertGuildIntoPermissionsTable(guildId, BotSettings.commandsWithPermissions);
                    // Insert guild into CommandChannels table
                    DatabaseHelper.insertGuildIntoCommandChannelsTable(guildId, CommandHandler.commands);
                }
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("SQL exception while trying to update the database. Occurred while updating the guild: " + colors.CYAN + guildId + colors.RESET);
                sqlException.printStackTrace();
            }
        }

        // Print the total number of guilds the Bot is running on
        out.append(colors.BOLD).append("\tTotal: ").append(event.getGuildTotalCount()).append(colors.RESET);

        out.append("\n============================================================\n");

        // List available and unavailable guilds
        out.append(CustomMsg.INFO_PREFIX).append("Available guilds: ").append(colors.GREEN_BOLD).append(event.getGuildAvailableCount()).append(colors.RESET)
                .append(" | ").append(colors.RED_BOLD).append(event.getGuildUnavailableCount()).append(colors.RESET);

        out.append("\n============================================================\n");

        System.out.println(out);

    }

}
