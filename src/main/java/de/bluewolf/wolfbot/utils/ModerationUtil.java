package de.bluewolf.wolfbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ModerationUtil
{

    public static void ban(MessageReceivedEvent event, Guild guild, String targetId, User sender, String reason)
    {

        assert sender != null;

        User target = event.getJDA().getUserById(targetId);
        assert target != null;
        Member member = null;
        try {
            member = event.getGuild().getMemberById(targetId);
        } catch (IllegalArgumentException exception) {
            CustomMsg.CMD_ERROR(event, "ban", "An error occurred");
            return;
        }

        if (member == null)
        {
            CustomMsg.CMD_ERROR(event, "ban", "No user entered");
            return;
        }

        // Check if user is on the guild
        if (!event.getGuild().getMembers().contains(member))
        {
            CustomMsg.CMD_ERROR(event, "ban", "User is not a member on the guild");
            return;
        }
        // Check if user isn't themselves
        if (member.getId().equals(sender.getId()))
        {
            CustomMsg.CMD_ERROR(event, "ban", "You can not be banned by yourself");
            return;
        }
        // Check if user isn't owner
        if (member.isOwner())
        {
            CustomMsg.CMD_ERROR(event, "ban", "The owner of the guild can not be banned");
            return;
        }
        // Check if user haven't a higher role
        if (!guild.getSelfMember().canInteract(member))
        {
            CustomMsg.CMD_ERROR(event, "ban", "User can not be banned because of higher role");
            return;
        }

        CustomMsg.sendPrivateMessage(target, "You got banned from " + guild.getName() + " for " + reason + ".");

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setAuthor("Banned " + target.getName() + "!", null, target.getAvatarUrl())
                        .setDescription(
                                "User " + member.getEffectiveName() + " was banned for " + reason + "."
                        )
                        .setFooter("Ban performed by " + sender.getName(), sender.getAvatarUrl())
                        .build()
        ).queue();

        event.getMessage().delete().queue();

        guild.ban(targetId, 7, reason).queue();

    }

    private static boolean isCorrectUser(Guild.Ban ban, String arg)
    {
        User bannedUser = ban.getUser();

        return bannedUser.getName().equalsIgnoreCase(arg) || bannedUser.getId().equals(arg);
    }

    // Unban user
    public static void unban(MessageReceivedEvent event, Guild guild, String targetId, User sender)
    {

        assert sender != null;

        TextChannel channel = event.getTextChannel();
        //User targetUser = event.getJDA().getUserById(targetId);
        //assert targetUser != null;

        // Check if the targeted user is banned
        event.getGuild().retrieveBanList().queue((bans) -> {

            List<User> bannedUsers = bans.stream().filter((ban) ->
                    isCorrectUser(ban, targetId)).map(Guild.Ban::getUser).collect(Collectors.toList());

            if (bannedUsers.isEmpty())
            {
                CustomMsg.CMD_ERROR(event, "unban", "User is not banned on the guild");
                return;
            }

            User target = bannedUsers.get(0);

            CustomMsg.sendPrivateMessage(target,
                    "You got unbanned from " + guild.getName() + ".\n"
                            + "If you want you can rejoin the server: " + channel.createInvite()
            );

            guild.unban(target).queue();

            event.getTextChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setAuthor("Unbanned " + target.getName() + "!", null, target.getAvatarUrl())
                            .setDescription(
                                    "User " + target.getName() + " was unbanned."
                            )
                            .setFooter("Unban performed by " + sender.getName(), sender.getAvatarUrl())
                            .build()
            ).queue();
            event.getMessage().delete().queue();

        });

    }

    // Kick user
    public static void kick(MessageReceivedEvent event, Guild guild, String targetId, User sender, String reason)
    {

        assert sender != null;

        TextChannel channel = event.getTextChannel();
        //User targetUser = event.getJDA().getUserById(targetId);
        //assert targetUser != null;

        User target = event.getJDA().getUserById(targetId);
        assert target != null;
        Member member = null;
        try {
            member = event.getGuild().getMember(target);
        } catch (IllegalArgumentException exception) {
            CustomMsg.CMD_ERROR(event, "kick", "An error occurred");
        }

        if (member == null)
        {
            CustomMsg.CMD_ERROR(event, "kick", "No user entered");
            return;
        }

        // Check if user is on the guild
        if (!event.getGuild().getMembers().contains(member))
        {
            CustomMsg.CMD_ERROR(event, "kick", "User is not a member on the guild");
            return;
        }
        // Check if user isn't themselves
        if (member.getId().equals(sender.getId()))
        {
            CustomMsg.CMD_ERROR(event, "kick", "You can not be kicked by yourself");
            return;
        }
        // Check if user isn't owner
        if (member.isOwner())
        {
            CustomMsg.CMD_ERROR(event, "kick", "The owner of the guild can not be kicked");
            return;
        }
        // Check if user haven't a higher role
        if (!guild.getSelfMember().canInteract(member))
        {
            CustomMsg.CMD_ERROR(event, "kick", "User can not be kicked because of higher role");
            return;
        }

        CustomMsg.sendPrivateMessage(target, "You got kicked from " + guild.getName() + " for " + reason +
                ".\nIf you want you can rejoin the server but next time you break the rules, you will be banned " + channel.createInvite()
        );

        guild.kick(targetId, reason).queue();

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setAuthor("Kicked " + target.getName() + "!", null, target.getAvatarUrl())
                        .setDescription(
                                "User " + member.getEffectiveName() + " was kicked for " + reason + "."
                        )
                        .setFooter("Kick performed by " + sender.getName(), sender.getAvatarUrl())
                        .build()
        ).queue();
        event.getMessage().delete().queue();

    }

}
