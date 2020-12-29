package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.commands.management.CmdManageRoles;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CmdGame implements Command
{

    private void mangeRoles(MessageReceivedEvent event, String role)
    {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        assert member != null;

        try {
            // Check if the role exists on the server
            if (guild.getRoles().contains(guild.getRolesByName(role, true).get(0)))
            {
                // Check if member didn't have the role
                if (!member.getRoles().contains(guild.getRolesByName(role, true).get(0)))
                {
                    // Add role to member
                    guild.addRoleToMember(member, guild.getRolesByName(role, true).get(0)).queue();

                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.WHITE)
                                    .setAuthor("Game role added to " + member.getUser().getName(), null, member.getUser().getAvatarUrl())
                                    .setDescription(
                                            "Added the game role " + guild.getRolesByName(role, true).get(0).getAsMention()
                                            + " to " + member.getAsMention() + "!"
                                    )
                                    .build()
                    ).queue();
                }
                // else remove role from member
                else
                {
                    // Remove role from member
                    guild.removeRoleFromMember(member, guild.getRolesByName(role, true).get(0)).queue();

                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.WHITE)
                                    .setAuthor("Game role removed from " + member.getUser().getName(), null, member.getUser().getAvatarUrl())
                                    .setDescription(
                                            "Removed the game role " + guild.getRolesByName(role, true).get(0).getAsMention()
                                            + " from " + member.getAsMention() + "!"
                                    )
                                    .build()
                    ).queue();
                }

                executed(true, event);
            }
        } catch (IndexOutOfBoundsException e)
        {
            CustomMsg.CMD_ERROR(event, "game", "Game role: '" + role + "' can not be found on this guild");
            executed(false, event);
            e.printStackTrace();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "game";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.MESSAGE_WRITE;

        return BotSettings.PERMISSIONS(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws SQLException
    {

        if (args.length < 1)
        {
            CustomMsg.CMD_ERROR(event, "game", "One positional argument expected");
            executed(false, event);
        }

        StringBuilder rawRole = new StringBuilder();

        if (args.length == 1 && args[0].equalsIgnoreCase("info"))
        {
            new CmdManageRoles().listGameRoles(event, event.getGuild());
        }
        else
        {
            for (String arg : args) {
                rawRole.append(arg).append(" ");
            }

            String guildId = event.getGuild().getId();

            // Get roles from DB
            ResultSet roleFromRoles = DatabaseHelper.query("SELECT Role FROM Roles WHERE GuildId = '" + guildId + "' AND Role = '" + rawRole + "' OR Alias1 = '" + rawRole + "' OR Alias2 = '" + rawRole + "' AND Type = 'game';");

            // Check if the bot has the permissions to manage roles
            if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_ROLES))
            {
                // Check if the role exists in database
                if (roleFromRoles.next())
                {
                    mangeRoles(event, roleFromRoles.getString("role"));
                }
                // Role does not exists
                else
                {
                    CustomMsg.CMD_ERROR(event, "game", "Can not find the role " + rawRole + ".");
                    executed(false, event);
                }
            }
            else
            {
                CustomMsg.CMD_ERROR(event, "game", "Bot does not have permissions to manage roles");
                executed(false, event);
            }
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "game");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "game <option>``. For more information use ``" + BotSettings.PREFIX + "help``.";
    }

}
