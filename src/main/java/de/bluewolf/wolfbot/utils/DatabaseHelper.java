package de.bluewolf.wolfbot.utils;

import de.bluewolf.wolfbot.settings.Secret;
import net.dv8tion.jda.api.Permission;

import java.sql.*;
import java.util.HashMap;

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

    // Check the connection to the database
    public static boolean isConnected() { return connection != null; }

    // Create connection to database
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
                System.exit(0);
            }
        }
    }

    // Close connection to database
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

    // Create BotStats table
    public static void createBotStatsTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `BotStats` "
                                + "(`GuildId` varchar(20) NOT NULL PRIMARY KEY UNIQUE, "
                                + "`GuildName` varchar(50), "
                                + "`Member` int, "
                                + "`Region` varchar(15), "
                                + "`Available` tinyint(1)  NOT NULL, "
                                + "`Password` varchar(10) NOT NULL);"
                );
                CustomMsg.INFO("Created the table 'BotStats' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'BotStats' (SQL)");
            }
        }
    }

    // Create Guild bases game roles table
    public static void createRolesTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `Roles` "
                                + "(`GuildId` varchar(20) NOT NULL, "
                                + "`Role` varchar(20) NOT NULL UNIQUE, "
                                + "`Alias1` varchar(20) UNIQUE, "
                                + "`Alias2` varchar(10) UNIQUE,"
                                + "`Type` varchar(7) NOT NULL, "
                                + "PRIMARY KEY(`GuildId`, `Role`));"
                );
                CustomMsg.INFO("Created the table 'Roles' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Roles' (SQL)");
            }
        }
    }

    // Create Guild bases game roles table
    public static void createPermissionsTable()
    {
        if (isConnected())
        {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS `Permissions` "
                                + "(`GuildId` varchar(20) NOT NULL, "
                                + "`Cmd` varchar(20) NOT NULL, "
                                + "`Permission` int NOT NULL, "
                                + "PRIMARY KEY(`GuildId`, `Cmd`));"
                );
                CustomMsg.INFO("Created the table 'Permissions' (SQL)");
            } catch (SQLException sqlException) {
                CustomMsg.ERROR("Failed to create the table 'Permissions' (SQL)");
            }
        }
    }

    // TODO Create channel table

    // TODO Create Moderation tabel

    // TODO Create leaderboard table

    // Delete table
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

    // Update database
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

    // Insert guild in Permissions table with default permissions
    public static void insertGuildIntoPermissionsTable(String guildId, HashMap<String, Integer> commands)
    {
        for (String i : commands.keySet())
        {
            update("INSERT INTO Permissions (GuildId, Cmd, Permission) VALUES ('" + guildId + "', '" + i + "', " + commands.get(i) + ");");
        }
    }

    // Remove guild from Permissions table
    public static void deleteGuildFromPermissionsTable(String guildId, HashMap<String, Integer> commands)
    {
        for (String i : commands.keySet())
        {
            update("DELETE FROM Permissions WHERE GuildId = '" + guildId + "' AND Cmd = '" + i + "';");
        }
    }

}
