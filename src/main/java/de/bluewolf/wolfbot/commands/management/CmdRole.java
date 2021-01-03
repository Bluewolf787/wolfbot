package de.bluewolf.wolfbot.commands.management;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Permissions;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.administration
 * @created 19/Dez/2020 - 23:06
 */
public class CmdRole implements Command
{

    private void addRoleToMember(MessageReceivedEvent event, String userTag, String role) throws SQLException
    {
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        // Get role from GeneralRoles table
        ResultSet roleFromRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' OR Alias1 = '" + role + "' OR Alias2 = '" + role + "' AND Type = 'general';");

        // Check if the member exists
        if (guild.getMembers().contains(guild.getMemberByTag(userTag)))
        {
            Member member = guild.getMemberByTag(userTag);

            // Check if the GeneralRoles table contains the role
            if (roleFromRoles.next())
            {
                // Check if the Bot has MANAGE_ROLES permissions
                if (guild.getSelfMember().getPermissions().contains(Permission.MANAGE_ROLES))
                {
                    // Check if the role exists on the guild
                    if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
                    {
                        Role fRole = guild.getRolesByName(role, true).get(0);

                        // Check if the member already has the role
                        assert member != null;
                        if (member.getRoles().contains(fRole))
                        {
                            CustomMsg.CMD_ERROR(event, "role", "The member '" + userTag + "' has the role '" + role + "' already");
                        }
                        else
                        {
                            // If the role exists and the user do not have the role already: add to member
                            guild.addRoleToMember(Objects.requireNonNull(member), fRole).queue();
                            event.getTextChannel().sendMessage(
                                    new EmbedBuilder()
                                            .setColor(Color.CYAN)
                                            .setAuthor("Role added to " + member.getUser().getName(), null, member.getUser().getAvatarUrl())
                                            .setDescription(
                                                    "Added the role " + fRole.getAsMention() + " to " + member.getAsMention() + "!"
                                            )
                                            .build()
                            ).queue();
                        }
                        executed(true, event);
                    }
                    // If the role does not exists: send message
                    else
                    {
                        CustomMsg.CMD_ERROR(event, "role", "Cloud not find the role '" + role + "' on this guild");
                        executed(false, event);
                    }
                }
                else
                {
                    CustomMsg.CMD_ERROR(event, "game", "Bot does not have permissions to manage roles");
                    executed(false, event);
                }
            }
            // Role not found in DB
            else
            {
                CustomMsg.CMD_ERROR(event, "role", "Cloud not find the role '" + role + "' in the database");
                executed(false, event);
            }
        }
        // Member not found
        else
        {
            CustomMsg.CMD_ERROR(event, "role", "Cloud not find the member with the tag '" + userTag + "' on this guild");
            executed(false, event);
        }
    }

    private void removeRoleFromMember(MessageReceivedEvent event, String userTag, String role) throws SQLException
    {
        Guild guild = event.getGuild();
        String guildId = guild.getId();

        // Get role from GeneralRoles table
        ResultSet roleFromRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + role + "' OR Alias1 = '" + role + "' OR Alias2 = '" + role + "' AND Type = 'general';");

        // Check if the member exists
        if (guild.getMembers().contains(guild.getMemberByTag(userTag)))
        {
            Member member = guild.getMemberByTag(userTag);

            // Check if the GeneralRoles table contains the role
            if (roleFromRoles.next())
            {
                // Check if the Bot has MANAGE_ROLES permissions
                if (guild.getSelfMember().getPermissions().contains(Permission.MANAGE_ROLES))
                {
                    // Check if the role exists on the guild
                    if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
                    {
                        Role fRole = guild.getRolesByName(role, true).get(0);

                        // Check if the member already has the role
                        assert member != null;
                        if (member.getRoles().contains(fRole))
                        {
                            // If the role exists and the user has the role already: remove to member
                            guild.removeRoleFromMember(Objects.requireNonNull(member), fRole).queue();
                            event.getTextChannel().sendMessage(
                                    new EmbedBuilder()
                                            .setColor(Color.CYAN)
                                            .setAuthor("Role removed from " + member.getUser().getName(), null, member.getUser().getAvatarUrl())
                                            .setDescription(
                                                    "Removed the role " + fRole.getAsMention() + " from " + member.getAsMention() + "!"
                                            )
                                            .build()
                            ).queue();
                            executed(true, event);
                        }
                        // Member doesn't has the role
                        else
                        {
                            CustomMsg.CMD_ERROR(event, "role", "The member '" + userTag + "' does not have the role '" + role + "'");
                            executed(false, event);
                        }
                    }
                    // If the role does not exists: send message
                    else
                    {
                        CustomMsg.CMD_ERROR(event, "role", "Cloud not find the role '" + role + "' on this guild");
                        executed(false, event);
                    }
                }
                // Bot has no permissions to MANAGE_ROLES
                else
                {
                    CustomMsg.CMD_ERROR(event, "game", "Bot does not have permissions to manage roles");
                    executed(false, event);
                }
            }
            // Role not found in DB
            else
            {
                CustomMsg.CMD_ERROR(event, "role", "Cloud not find the role '" + role + "' in the database");
                executed(false, event);
            }
        }
        // Member not found
        else
        {
            CustomMsg.CMD_ERROR(event, "role", "Cloud not find the member with the tag '" + userTag + "' on this guild");
            executed(false, event);
        }
    }

    private String[] getMemberAndRole(MessageReceivedEvent event, String[] args)
    {
        String[] output = new String[2];
        StringBuilder rawInput = new StringBuilder();

        if (args.length > 2)
        {
            // Get member and role
            for (int i = 1; i <= args.length - 1; i++) {
                rawInput.append(args[i]).append(" ");
            }
            String input = rawInput.toString().trim();

            String userTag;
            try {
                // Get member
                userTag = input.substring(input.indexOf("member:") + 7);
                output[0] = userTag.substring(0, userTag.indexOf("role:")).trim();

                // Get role
                output[1] = input.substring(input.indexOf("role:") + 5).trim();
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
        return Permissions.check(event, "role");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        if (args.length == 1 && args[0].equalsIgnoreCase("info"))
        {
            new CmdManageRoles().listGeneralRoles(event, event.getGuild());
        }
        else if (args.length < 2)
        {
            CustomMsg.CMD_ERROR(event, "game", "At least one or three positional argument expected");
            executed(false, event);
            return;
        }

        String[] input = getMemberAndRole(event, args);
        String userTag = input[0];
        String role = input[1];

        switch (args[0].toLowerCase())
        {
            case "add":
                addRoleToMember(event, userTag, role);
                break;

            case "remove":
                removeRoleFromMember(event, userTag, role);
                break;
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "role");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "role add/remove member:<Username#Tag> role:<role>`` to add or remove a role to/from a member\n\n"
                + "or use ``" + BotSettings.PREFIX + "role info`` to show all roles";
    }

}
