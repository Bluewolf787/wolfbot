package de.bluewolf.wolfbot.commands.bot_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.settings.Secret;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import de.bluewolf.wolfbot.utils.Timer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.administration
 * @created 26/Dez/2020 - 22:56
 */
public class CmdBot implements Command
{

    private String[] buildGuildsMessage(ResultSet resultSet, boolean listAllGuilds, boolean guildAvailable) throws SQLException
    {
        String[] guildList = new String[2];

        List<String> guildIds = new ArrayList<>();
        List<String> guildNames = new ArrayList<>();
        List<Integer> guildMembers = new ArrayList<>();
        List<String> guildRegions = new ArrayList<>();
        int guildsCount = 0;
        while (resultSet.next())
        {
            guildIds.add(resultSet.getString("GuildId"));
            guildNames.add(resultSet.getString("GuildName"));

            if (listAllGuilds)
            {
                guildMembers.add(resultSet.getInt("Member"));
                guildRegions.add(resultSet.getString("Region"));
            }
            guildsCount++;
        }

        StringBuilder guildsMessage = new StringBuilder();
        for (int i = 0; i < guildsCount; i++)
        {
            if (i > 2 && !listAllGuilds)
            {
                int rest = guildsCount - i;
                guildsMessage.append("And ").append(rest).append(" more. Show all guilds with ``").append(BotSettings.PREFIX).append("bot guilds``\n");
                break;
            }

            if (listAllGuilds && guildAvailable)
            {
                guildsMessage.append("```bash\n\"- ").append(CustomMsg.GUILD_NAME(guildNames.get(i), guildIds.get(i)))
                        .append("\"\nMember: ").append(guildMembers.get(i)).append("\nRegion: ").append(guildRegions.get(0)).append("\n```");
            }
            else if (listAllGuilds)
            {
                guildsMessage.append("```diff\n- ").append(CustomMsg.GUILD_NAME(guildNames.get(i), guildIds.get(i)))
                        .append("\nMember: ").append(guildMembers.get(i)).append("\nRegion: ").append(guildRegions.get(0)).append("\n```");
            }

            if (!listAllGuilds && guildAvailable)
            {
                guildsMessage.append("```bash\n\"- ").append(CustomMsg.GUILD_NAME(guildNames.get(i), guildIds.get(i))).append("\"\n```");
            }
            else if (!listAllGuilds)
            {
                guildsMessage.append("```diff\n- ").append(CustomMsg.GUILD_NAME(guildNames.get(i), guildIds.get(i))).append("\n```");
            }
        }

        guildList[0] = String.valueOf(guildsCount);
        guildList[1] = guildsMessage.toString();
        return guildList;
    }


    private MessageEmbed uptimeMessage()
    {
        String running = "This Bot is running for *" + Timer.getElapsedTime().trim() + "*";

        return new EmbedBuilder()
            .setColor(new Color(0xdf1196))
            .setTitle("WolfBot Uptime")
            .setDescription(running)
            .build();
    }

    private MessageEmbed infoMessage(MessageReceivedEvent event) throws SQLException
    {
        // Get all available guilds
        ResultSet getAvailableGuilds = DatabaseHelper.query("SELECT GuildId, GuildName, Available FROM Guilds WHERE Available = 1;");
        String[] availableGuildsList = buildGuildsMessage(getAvailableGuilds, false, true);
        String availableGuildsCount = availableGuildsList[0];
        String availableGuildsMessage = availableGuildsList[1];

        // Get all unavailable guilds
        ResultSet getUnavailableGuilds = DatabaseHelper.query("SELECT GuildId, GuildName, Available FROM Guilds WHERE Available = 0;");
        String[] unavailableGuildsList = buildGuildsMessage(getUnavailableGuilds, false, false);
        String unavailableGuildsCount = unavailableGuildsList[0];
        String unavailableGuildsMessage = unavailableGuildsList[1];

        String running = "This Bot is running for *" + Timer.getElapsedTime().trim() + "*";
        String totalGuilds = "---------\n```ini\n[Total: " + event.getJDA().getGuilds().size() + "]\n```---------";
        String availableGuilds = "```bash\n\"Available: " + availableGuildsCount + "\"\n```" + availableGuildsMessage;
        String unavailableGuilds = "```css\n[Unavailable: " + unavailableGuildsCount + "]\n```" + unavailableGuildsMessage;

        // Build complete message

        return new EmbedBuilder()
                .setColor(new Color(0xdf1196))
                .setTitle("WolfBot Information")
                .setDescription(
                        "**Uptime:**\n" + running +
                                "\n\n**Guilds:**\n" + totalGuilds + "\n" + availableGuilds + "\n" + unavailableGuilds
                )
                .build();
    }

    private MessageEmbed guildsMessage(MessageReceivedEvent event) throws SQLException
   {
        // Get all available guilds
        ResultSet getAvailableGuilds = DatabaseHelper.query("SELECT GuildId, GuildName, Member, Region, Available FROM Guilds WHERE Available = 1;");
        String[] availableGuildsList = buildGuildsMessage(getAvailableGuilds, true, true);
        String availableGuildsCount = availableGuildsList[0];
        String availableGuildsMessage = availableGuildsList[1];

        // Get all unavailable guilds
        ResultSet getUnavailableGuilds = DatabaseHelper.query("SELECT GuildId, GuildName, Member, Region, Available FROM Guilds WHERE Available = 0;");
        String[] unavailableGuildsList = buildGuildsMessage(getUnavailableGuilds, true, false   );
        String unavailableGuildsCount = unavailableGuildsList[0];
        String unavailableGuildsMessage = unavailableGuildsList[1];

        String totalGuilds = "---------\n```ini\n[Total: " + event.getJDA().getGuilds().size() + "]\n```---------";
        String availableGuilds = "```bash\n\"Available: " + availableGuildsCount + "\"\n```" + availableGuildsMessage;
        String unavailableGuilds = "```css\n[Unavailable: " + unavailableGuildsCount + "]\n```" + unavailableGuildsMessage;

        return new EmbedBuilder()
                .setColor(new Color(0xdf1196))
                .setTitle("All guilds this Bot is running on")
                .setDescription(totalGuilds + "\n" + availableGuilds + "\n" + unavailableGuilds)
                .build();
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String author = event.getAuthor().getId();

        if (author.equals(Secret.ID))
            return false;
        else
        {
            CustomMsg.NO_PERM(event, "bot");
            return true;
        }
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException
    {
        TextChannel channel = event.getTextChannel();

        if (args.length < 2)
        {
            switch (args[0])
            {
                case "uptime":
                    channel.sendMessageEmbeds(uptimeMessage()).queue();
                    executed(true, event);
                    break;

                case "info":
                    channel.sendMessageEmbeds(infoMessage(event)).queue();
                    executed(true, event);
                    break;

                case "guilds":
                    channel.sendMessageEmbeds(guildsMessage(event)).queue();
                    executed(true, event);
                    break;

            }
        }
        else
            executed(false, event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "bot");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "bot info`` to show all information about the bot\n\n" +
                "Use ``" + BotSettings.PREFIX + "bot uptime`` to show the time the bot is running";
    }

}
