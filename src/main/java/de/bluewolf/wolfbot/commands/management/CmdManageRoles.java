package de.bluewolf.wolfbot.commands.management;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.administrator
 * @created 19/Dez/2020 - 16:04
 */
public class CmdManageRoles implements Command
{

    /**
     * Create a new role with standard permissions as general role
     * @param event MessageReceivedEvent
     * @param guild The guild on which a new role will be created
     * @param role The name of the new role
     * @param alias1 A alias for the role
     * @param alias2 A second alias for the role
     * @throws SQLException For SQL queries
     */
    private void createGeneralRole(MessageReceivedEvent event, Guild guild, String role, String alias1, String alias2) throws SQLException
    {
        // Check if the role exists on the guild
        try {
            if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
            {
                // If the role already exists: send message
                executed(true, event);
                CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' already exists on this guild");
            }
        }
        catch (IndexOutOfBoundsException exception)
        {
            // If does not exists add to the guild an table
            guild.createRole()
                    .setName(role)
                    .setColor(Color.CYAN)
                    .setMentionable(true)
                    .setHoisted(true)
                    .queue();

            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("General role created!")
                            .setDescription("The role '" + role + "' was created")
                            .build()
            ).queue();

            addGeneralRole(event, guild, role, alias1, alias2);
        }
    }

    /**
     * Create a new role with standard permissions as game role
     * @param event MessageReceivedEvent
     * @param guild The guild on which a new role will be created
     * @param role The name of the new role
     * @param alias1 A alias for the role
     * @param alias2 A second alias for the role
     * @throws SQLException For SQL queries
     */
    private void createGameRole(MessageReceivedEvent event, Guild guild, String role, String alias1, String alias2) throws SQLException
    {
        // Check if the role exists on the guild
        try {
            if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
            {
                // If the role already exists: send message
                executed(true, event);
                CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' already exists on this guild");
            }
        }
        catch (IndexOutOfBoundsException exception)
        {
            // If does not exists add to the guild an table
            guild.createRole()
                    .setName(role)
                    .setColor(Color.WHITE)
                    .setMentionable(true)
                    .queue();

            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Game role created!")
                            .setDescription("The role '" + role + "' was created")
                            .build()
            ).queue();

            addGameRole(event, guild, role, alias1, alias2);
        }
    }

    /**
     * Delete a role from guild an DB (Roles table)
     * @param event MessageReceivedEvent
     * @param guild The guild on which the role will be deleted
     * @param role The role to be deleted
     * @throws SQLException For SQL queries
     */
    private void deleteGeneralRole(MessageReceivedEvent event, Guild guild, String role) throws SQLException
    {
        // Check if the role exists on the guild
        try {
            if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
            {
                // If the role exists: remove from guild and database
                guild.getRolesByName(role, true).get(0).delete().queue();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("General role deleted!")
                                .setDescription("The role '" + role + "' was delete from the guild")
                                .build()
                ).queue();
                removeGeneralRole(event, guild, role);
            }
        } catch (IndexOutOfBoundsException exception) {
            // If the role does not exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' does not exists on this guild");
        }
    }

    private void deleteGameRole(MessageReceivedEvent event, Guild guild, String role) throws SQLException
    {
        // Check if the role exists on the guild
        try {
            if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
            {
                System.out.println("ROLE : " + role + " | " + guild.getRolesByName(role, true).get(0).toString());
                // If the role exists: remove from guild and database
                guild.getRolesByName(role, true).get(0).delete().queue();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Game role deleted!")
                                .setDescription("The role '" + role + "' was delete from the guild")
                                .build()
                ).queue();
                removeGameRole(event, guild, role);
            }
        } catch (IndexOutOfBoundsException exception) {
            // If the role does not exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' does not exists on this guild");
        }
    }

    /**
     * Add a new general role to the DB (Roles table)
     * @param event JDA MessageReceivedEvent
     * @param guild The guild to which to role will be added
     * @param role Name of the role
     * @param alias1 Alias for the role
     * @param alias2 Second alias for the role
     * @throws SQLException For SQL queries
     */
    private void addGeneralRole(MessageReceivedEvent event, Guild guild, String role, String alias1, String alias2) throws SQLException
    {
        String guildId = guild.getId();

        // Check if the role already exists in table
        ResultSet roleFromGeneralRoles = DatabaseHelper.query("SELECT role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'general';");
        if (roleFromGeneralRoles.next())
        {
            // If exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' already exists in the database");
        }
        else
        {
            // If does not exists: add to table
            // Check if aliases exists
            if (alias1.isEmpty() && alias2.isEmpty())
            {
                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Type) VALUES ('" + guildId + "', '" + role + "', 'general');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("General role added!")
                            .setDescription("The role '" + role + "' was added without a alias")
                            .build()
                ).queue();
            }
            else if (alias1.isEmpty() || alias2.isEmpty())
            {
                String alias;
                if (!alias1.isEmpty())
                    alias = alias1;
                else
                    alias = alias2;

                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Alias1, Type) VALUES ('" + guildId + "', '" + role + "', '" + alias + "', 'general');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("General role added!")
                                .setDescription("The role '" + role + "' was added with the alias '" + alias + "'")
                                .build()
                ).queue();
            }
            else
            {
                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Alias1, Alias2, Type) VALUES ('" + guildId + "', '" + role + "', '" + alias1 + "', '" + alias2 + "', 'general');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("General role added!")
                                .setDescription("The role '" + role + "' was added with the aliases '" + alias1 + "' and '" + alias2 + "'")
                                .build()
                ).queue();
            }

            executed(true, event);
        }
    }

    /**
     * Add a new game role to the DB (Roles table)
     * @param event JDA MessageReceivedEvent
     * @param guild The guild to which to role will be added
     * @param role Name of the role
     * @param alias1 Alias for the role
     * @param alias2 Second alias for the role
     * @throws SQLException For SQL queries
     */
    private void addGameRole(MessageReceivedEvent event, Guild guild, String role, String alias1, String alias2) throws SQLException
    {
        String guildId = guild.getId();

        // Check if the role already exists in table
        ResultSet roleFromGameRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'game';");
        if (roleFromGameRoles.next())
        {
            // If exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' already exists in the database");
        }
        else
        {
            // If does not exists: add to table
            // Check if aliases exists
            if (alias1.isEmpty() && alias2.isEmpty())
            {
                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Type) VALUES ('" + guildId + "', '" + role + "', 'game');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Game role added!")
                                .setDescription("The role '" + role + "' was added without a alias")
                                .build()
                ).queue();
            }
            else if (alias1.isEmpty() || alias2.isEmpty())
            {
                String alias;
                if (!alias1.isEmpty())
                    alias = alias1;
                else
                    alias = alias2;

                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Alias1, Type) VALUES ('" + guildId + "', '" + role + "', '" + alias + "', 'game');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Game role added!")
                                .setDescription("The role '" + role + "' was added with the alias '" + alias + "'")
                                .build()
                ).queue();
            }
            else
            {
                DatabaseHelper.update("INSERT INTO Roles (GuildId, Role, Alias1, Alias2, Type) VALUES ('" + guildId + "', '" + role + "', '" + alias1 + "', '" + alias2 + "', 'game');");
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Game role added!")
                                .setDescription("The role '" + role + "' was added with the aliases '" + alias1 + "' and '" + alias2 + "'")
                                .build()
                ).queue();
            }

            executed(true, event);
        }
    }

    private void removeGeneralRole(MessageReceivedEvent event, Guild guild, String role) throws SQLException
    {
        String guildId = guild.getId();

        // Check if the role exists in DB
        ResultSet roleFromGeneralRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'general';");
        if (roleFromGeneralRoles.next())
        {
            // If the role exists: remove from DB
            DatabaseHelper.update("DELETE FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'general';");
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("General role removed!")
                            .setDescription("The role '" + role + "' was removed from the database")
                            .build()
            ).queue();
            executed(true, event);
        }
        else
        {
            // If the role does not exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' does not exists in the database");
        }
    }
    private void removeGameRole(MessageReceivedEvent event, Guild guild, String role) throws SQLException
    {
        String guildId = guild.getId();

        // Check if the role exists in DB
        ResultSet roleFromGameRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'game';");
        if (roleFromGameRoles.next())
        {
            // If the role exists: remove from DB
            DatabaseHelper.update("DELETE FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' AND Type = 'game';");
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Game role removed!")
                            .setDescription("The role '" + role + "' was removed from the database")
                            .build()
            ).queue();
            executed(true, event);
        }
        else
        {
            // If the role does not exists: send message
            executed(true, event);
            CustomMsg.CMD_INFO(event, "manageroles", "The role '" + role + "' does not exists in the database");
        }
    }

    public void listGeneralRoles(MessageReceivedEvent event, Guild guild) throws SQLException
    {
        String guildId = guild.getId();
        StringBuilder msg = new StringBuilder();

        // Get all roles from GeneralRoles table
        ResultSet roles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Type = 'general';");
        while (roles.next())
        {
            msg.append(" - ").append(roles.getString("role")).append("\n");
        }

        // Send message with all roles
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setTitle("General Roles")
                        .setDescription(msg.toString())
                        .build()
        ).queue();

        executed(true, event);
    }
    public void listGameRoles(MessageReceivedEvent event, Guild guild) throws SQLException
    {
        String guildId = guild.getId();
        StringBuilder msg = new StringBuilder();

        // Get all roles from GeneralRoles table
        ResultSet roles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Type = 'game';");
        while (roles.next())
        {
            msg.append(" - ").append(roles.getString("role")).append("\n");
        }

        // Send message with all roles
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setTitle("Game Roles")
                        .setDescription(msg.toString())
                        .build()
        ).queue();

        executed(true, event);
    }

    private String[] getRoleAndAliases(MessageReceivedEvent event, String[] args)
    {
        String[] output = new String[3];

        StringBuilder rawInput = new StringBuilder();

        if (args.length > 2)
        {
            // Get role and aliases
            for (int i = 2; i <= args.length - 1; i++) {
                //rawRole.append(args[i]).append(" ");
                rawInput.append(args[i]).append(" ");
            }
            String input = rawInput.toString().trim();
            //System.out.println("Input : " + input);

            String role;
            String alias1;

            try {
                String roleSubstring = input.substring(input.indexOf("role:") + 5);
                String aliasOneSubstring = input.substring(input.indexOf("alias1:") + 7);
                String aliasTwoSubstring = input.substring(input.indexOf("alias2:") + 7);

                if (input.contains("alias1:") && input.contains("alias2:"))
                {
                    // Get role
                    role = roleSubstring;
                    output[0] = role.substring(0, role.indexOf("alias1:")).trim();

                    // Get alias1
                    alias1 = aliasOneSubstring;
                    output[1] = alias1.substring(0, alias1.indexOf("alias2:")).trim();

                    // Get alias2
                    output[2] = aliasTwoSubstring.trim();
                }
                else if (input.contains("alias1:"))
                {
                    // Get role
                    role = roleSubstring;
                    output[0] = role.substring(0, role.indexOf("alias1:")).trim();

                    // Get alias1
                    output[1] = aliasOneSubstring.trim();

                    // Set alias2 empty
                    output[2] = "";
                }
                else if (input.contains("alias2:"))
                {
                    // Get role
                    role = roleSubstring;
                    output[0] = role.substring(0, role.indexOf("alias2:")).trim();

                    // Set alias1 empty
                    output[1] = "";

                    // Get alias2
                    output[2] = aliasTwoSubstring.trim();
                }
                else
                {
                    // Get role
                    output[0] = roleSubstring.trim();

                    // Set alias1 and alias2 empty
                    output[1] = "";
                    output[2] = "";
                }
            } catch (StringIndexOutOfBoundsException exception) {
                CustomMsg.CMD_ERROR(event, "manageroles", "String index out of bounds exception");
                executed(false, event);
            }
        }

        return output;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "manageroles";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.MANAGE_ROLES;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        if (args.length < 2)
        {
            CustomMsg.CMD_ERROR(event, "game", "At least two positional argument expected");
            executed(false, event);
            return;
        }

        Guild guild = event.getGuild();

        String[] roleInput = getRoleAndAliases(event, args);
        String role = roleInput[0];
        String alias1 = roleInput[1];
        String alias2 = roleInput[2];

        switch (args[0])
        {
            case "test":
                System.out.println("Role : " + role);
                System.out.println("Alias1 : " + alias1);
                System.out.println("Alias2 : " + alias2);
                break;

            case "list":
                switch (args[1])
                {
                    case "general":
                        listGeneralRoles(event, guild);
                        break;

                    case "game":
                        listGameRoles(event, guild);
                        break;

                    default:
                        executed(false, event);
                        break;
                }
                break;

            case "create":
                // Check if the bot has the permissions to manage roles
                if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_ROLES))
                {
                    switch (args[1])
                    {
                        case "general":
                            createGeneralRole(event, guild, role, alias1, alias2);
                            break;

                        case "game":
                            createGameRole(event, guild, role, alias1, alias2);
                            break;

                        default:
                            executed(false, event);
                            break;
                    }
                }
                else
                {
                    CustomMsg.CMD_ERROR(event, "game", "Bot does not have permissions to manage roles");
                    executed(false, event);
                }
                break;

            case "delete":
                // Check if the bot has the permissions to manage roles
                if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_ROLES))
                {
                    switch (args[1])
                    {
                        case "general":
                            deleteGeneralRole(event, guild, role);
                            break;

                        case "game":
                            deleteGameRole(event, guild, role);
                            break;

                        default:
                            executed(false, event);
                            break;
                    }
                }
                else
                {
                    CustomMsg.CMD_ERROR(event, "game", "Bot does not have permissions to manage roles");
                    executed(false, event);
                }
                break;

            case "add":
                switch (args[1])
                {
                    case "general":
                        addGeneralRole(event, guild, role, alias1, alias2);
                        break;

                    case "game":
                        addGameRole(event, guild, role, alias1, alias2);
                        break;

                    default:
                        executed(false, event);
                        break;
                }
                break;

            case "remove":
                switch (args[1])
                {
                    case "general":
                        removeGeneralRole(event, guild, role);
                        break;

                    case "game":
                        removeGameRole(event, guild, role);
                        break;

                    default:
                        executed(false, event);
                        break;
                }
                break;
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "manageroles");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use:\n\n- ``" + BotSettings.PREFIX + "manageroles create/delete general/game role:<Role name> alias1:<alias> alias2:<alias>`` to create/delete a general or game role to/from this guild (Create: optional with aliases)\n\n"
                + "- ``" + BotSettings.PREFIX + "manageroles add/remove general/game role:<Role name> alias1:<alias> alias2:<alias>`` to add/remove a general or game role to/from the database (Add: optional with aliases)\n\n"
                + "- ``" + BotSettings.PREFIX + "manageroles list general/game`` to show all general or game roles on this guild";
    }

}
