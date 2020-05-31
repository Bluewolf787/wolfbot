package de.bluewolf.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import de.bluewolf.settings.utils;

import java.awt.*;
import java.util.Arrays;

public class CmdGameroles implements Command
{

    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {
        return false;
    }

    // Send message without response handling
    public void sendMessage(User user, String content)
    {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    // TODO: Save roles in file

    public String[] gameRoles = new String[]
            {
                    "fortnite", "minecraft","gta5", "farming simulator 15", "farming simulator 19", "battlefield", "hearthstone", "rocket league", "destiny",
                    "the crew", "forza horizan", "valorant", "cod", "warzone", "unrailed!", "the witcher", "ets", "overwatch", "csgo", "wow", "osu!", "apex legends", "for honor"
            };

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {

        if (args.length < 1)
        {
            utils.ERROR_MSG(event, "Enter `-game <game>`! For more information use `-game info`.");
            return;
        }

        boolean role = Arrays.asList(gameRoles).contains(args[0].toLowerCase());

        Guild guild = event.getGuild();
        Member member = event.getMember();
        assert member != null;

        // TODO: Check if the Bot has the permission MANGE_ROLES

        // Check if the role exists
        if (role)
        {
            //System.out.println("yes");

            try {
                // Check if the role exists
                if (guild.getRoles().contains(guild.getRolesByName(args[0], true).get(0)))
                {

                    // Check if member didn't have the role
                    if (!member.getRoles().contains(guild.getRolesByName(args[0], true).get(0)))
                    {

                        // Add role to member
                        guild.addRoleToMember(member, guild.getRolesByName(args[0], true).get(0)).queue();

                        event.getTextChannel().sendMessage(
                                new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setDescription(
                                                "**SUCCESS!**\n\n" + member.getAsMention() + " has now the role " +
                                                        guild.getRolesByName(args[0], true).get(0).getAsMention() + "."
                                        )
                                        .build()
                        ).queue();

                    }
                    // else remove role from member
                    else
                    {

                        guild.removeRoleFromMember(member, guild.getRolesByName(args[0], true).get(0)).queue();

                        event.getTextChannel().sendMessage(
                                new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setDescription(
                                                member.getAsMention() + " has not longer the role " +
                                                        guild.getRolesByName(args[0], true).get(0).getAsMention() + "."
                                        )
                                        .build()
                        ).queue();

                    }

                }
                else
                    utils.ERROR_MSG(event, "The role `" + args[0] + "` don't exists on this! Please contact a server stuff.");
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
        else if (args[0].equalsIgnoreCase("info"))
        {

            String msg = "**Gameroles**\n";

            for (int i = 0; i < gameRoles.length; i++)
            {
                System.out.println(i + ": " + gameRoles[i]);
                msg += gameRoles[i] + "\n";
            }

            event.getTextChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(new Color(0x288AB8))
                            .setDescription(msg)
                            .build()
            ).queue();
        }
        // If role not exists
        else
        {
            System.out.println("Role: '" + args[0] + "' not found");

            // Send direct message to bluewolf
            User user  = member.getUser().getJDA().getUserById("267392033059110913");
            assert user != null;
            sendMessage(user, event.getAuthor().getName() + " from " + guild.getName() + " wants " + args[0] + " as gamerole!");

        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        utils.CommandExecuted("-game");
    }

    @Override
    public String help()
    {
        return null;
    }

}
