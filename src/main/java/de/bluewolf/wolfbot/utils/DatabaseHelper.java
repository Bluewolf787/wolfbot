package de.bluewolf.wolfbot.utils;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.Secret;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.settings
 * @created 02/Sep/2020 - 22:42
 */

public class DatabaseHelper
{

    private static Connection connection;
    private static Statement statement;

    /**
     * Check the connection to the database
     * @return
     */
    public static boolean isConnected() { return connection != null; }

    /**
     * Create connection to database
     */
    public static void connect()
    {
        if (!isConnected())
        {
            try {
                //Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + Secret.DB_HOST + ":" + Secret.DB_PORT + "/" + Secret.DB_DATABASE + "?autoReconnect=true",
                        Secret.DB_USER, Secret.DB_PASSWORD);
                CustomMsg.INFO("Connected to database " + Secret.DB_DATABASE + " (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to connect to database " + Secret.DB_DATABASE + " (SQL)");
                sqlException.printStackTrace();
                new Timer().schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        System.exit(0);
                    }
                }, 5000);
            }
        }
    }

    /**
     * Close connection to database
     */
    public static void disconnect()
    {
        if (isConnected())
        {
            try {
                connection.close();
                CustomMsg.INFO("Closed connection to database " + Secret.DB_DATABASE + " (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to close connection to database " + Secret.DB_DATABASE + " (SQL)");
            }
        }
    }

    /**
     * Create Guilds table
     */
    public static void createGuildsTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `Guilds` "
                                + "(`GuildId` varchar(20) NOT NULL PRIMARY KEY UNIQUE, "
                                + "`GuildName` varchar(50), "
                                + "`Member` int, "
                                + "`Available` tinyint(1)  NOT NULL, "
                                + "`Password` varchar(10) NOT NULL);"
                );
                CustomMsg.INFO("Created the table 'Guilds' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Guilds' (SQL)");
            }
        }
    }

    /**
     * Create Roles table
     */
    public static void createRolesTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `Roles` "
                                + "(`GuildId` varchar(20) NOT NULL, "
                                + "`Role` varchar(20) NOT NULL, "
                                + "`Alias1` varchar(20), "
                                + "`Alias2` varchar(10),"
                                + "`Type` varchar(7) NOT NULL, "
                                + "PRIMARY KEY(`GuildId`, `Role`));"
                );
                CustomMsg.INFO("Created the table 'Roles' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Roles' (SQL)");
            }
        }
    }

    /**
     * Create Permissions table
     */
    public static void createPermissionsTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `Permissions` "
                                + "(`GuildId` varchar(20) NOT NULL, "
                                + "`Command` varchar(20) NOT NULL, "
                                + "`Permission` int NOT NULL, "
                                + "PRIMARY KEY(`GuildId`, `Command`));"
                );
                CustomMsg.INFO("Created the table 'Permissions' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Permissions' (SQL)");
            }
        }
    }

    /**
     * Create CommandChannels table
     */
    public static void createCommandChannelsTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `CommandChannels` "
                                + "(`GuildId` varchar(20) NOT NULL, "
                                + "`Command` varchar(20) NOT NULL, "
                                + "`ChannelId` varchar(20), "
                                + "PRIMARY KEY(`GuildId`, `Command`));"
                );
                CustomMsg.INFO("Created the table 'Permissions' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Permissions' (SQL)");
            }
        }
    }

    // TODO Create Moderation table

    // TODO Create leaderboard table

    /**
     * Delete table from database
     * @param table Name of the table
     */
    public static void deleteTable(String table)
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate("DROP TABLE IF EXISTS " + table + ";");
                CustomMsg.INFO("Deleted table '" + table + "' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to delete table '" + table + "' (SQL)");
            }
        }
    }

    /**
     * Update table in the database
     * @param query Query statement (INSERT, UPDATE, DELETE)
     */
    public static void update(String query)
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(query);
                CustomMsg.INFO("Executed query: '" + query + "' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to execute query: '" + query + "' (SQL)");
                sqlException.printStackTrace();
            }
        }
    }

    // Select data from database

    /**
     * Select data from table in the database
     * @param query Select statement
     * @return ResultSet with the data
     */
    public static ResultSet query(String query)
    {
        ResultSet result = null;

        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                result = statement.executeQuery(query);
                CustomMsg.INFO("Executed query: '" + query + "' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to execute query: '" + query + "' (SQL)");
            }
        }

        return result;
    }

    /**
     * Insert guild into Permissions table with default permissions
     * @param guildId ID of the guild which will be added
     * @param commands HashMap of the commands with permission offset
     */
    public static void insertGuildIntoPermissionsTable(String guildId, HashMap<String, Integer> commands)
    {
        for (String i : commands.keySet())
        {
            update("INSERT INTO Permissions (GuildId, Command, Permission) VALUES ('" + guildId + "', '" + i + "', " + commands.get(i) + ");");
        }
    }

    /**
     * Remove guild from Permissions table
     * @param guildId ID of the guild which will be removed
     */
    public static void deleteGuildFromPermissionsTable(String guildId)
    {
        update("DELETE FROM Permissions WHERE GuildId = '" + guildId + "';");
    }

    /**
     * Remove guild from Roles table
     * @param guildId ID of the guild which will be removed
     */
    public static void deleteGuildFromRolesTable(String guildId)
    {
        update("DELETE FROM Roles WHERE GuildId = '" + guildId + "';");
    }

    /**
     * Insert guild into CommandChannels table (Only guild ID and commands without channels)
     * @param guildId ID of the guild which will be added
     * @param commands HashMap of the commands
     */
    public static void insertGuildIntoCommandChannelsTable(String guildId, HashMap<String, Command> commands)
    {
        for (String i : commands.keySet())
        {
            update("INSERT INTO CommandChannels (GuildId, Command) VALUES ('" + guildId + "', '" + i + "');");
        }
    }

    /**
     * Remove guild from CommandChannels table
     * @param guildId ID of the guild which will be removed
     */
    public static void deleteGuildFromCommandChannelsTable(String guildId)
    {
        update("DELETE FROM CommandChannels WHERE GuildId = '" + guildId + "';");
    }

}
