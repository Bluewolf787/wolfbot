package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.commands.chat
 * @created 06/Jul/2020 - 13:54
 */

public class CmdVote implements Command, Serializable
{

    private static TextChannel textChannel;

    private static final HashMap<Guild, Message> tempList = new HashMap<>();
    public static HashMap<Guild, Poll> polls = new HashMap<>();

    private static final String[] EMOJI = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};

    public static class Poll implements Serializable
    {

        private final String creator;
        private final String heading;
        private final List<String> answer;
        private final String message;
        private final HashMap<String, Integer> votes;

        public Poll(Member creator, String heading, List<String> answer, Message message)
        {
            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.answer = answer;
            this.message = message.getId();
            this.votes = new HashMap<>();
        }

        private Member getCreator(Guild guild)
        {
            return guild.getMember(Objects.requireNonNull(guild.getJDA().getUserById(creator)));
        }

        public Message getMessage(Guild guild)
        {
            List<Message> messages = new ArrayList<>();

            guild.getTextChannels().forEach(channel -> {
                try {
                    messages.add(channel.retrieveMessageById(message).complete());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            return messages.isEmpty() ? null : messages.get(0);
        }


    }

    private static void message(String content, Color color)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(color).setDescription(content);
        textChannel.sendMessage(embedBuilder.build()).queue();
    }

    private static EmbedBuilder getParsedPoll(Poll poll, Guild guild)
    {

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.answer.forEach(s -> {
            long votesCount = poll.votes.keySet().stream().filter(k -> poll.votes.get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOJI[count.get()]).append("  -  ").append(s).append("  -  Votes: `").append(votesCount).append("` \n");
            count.addAndGet(1);
        });

        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setAuthor(poll.getCreator(guild).getEffectiveName() + "'s poll.", null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil: " + poll.heading + "\n\n" + ansSTR.toString())
                .setFooter("Use " + BotSettings.PREFIX + "vote v <number> to vote.");


    }

    private void createPoll(String[] args, MessageReceivedEvent event)
    {

        if (polls.containsKey(event.getGuild()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "Another poll is already running on the guild");
            executed(false, event);
            return;
        }

        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));

        String heading = content.get(0);
        List<String> answer = new ArrayList<>(content.subList(1, content.size()));

        Message msg = textChannel.sendMessage("Poll will be created...").complete();

        Poll poll = new Poll(Objects.requireNonNull(event.getMember()), heading, answer, msg);

        polls.put(event.getGuild(), poll);

        textChannel.editMessageById(msg.getId(), getParsedPoll(poll, event.getGuild()).build()).queue();
        textChannel.pinMessageById(msg.getId()).queue();

        tempList.put(event.getGuild(), polls.get(event.getGuild()).getMessage(event.getGuild()));

        try {
            savePoll(event.getGuild());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        event.getMessage().delete().queue();

        executed(true, event);

    }

    private static void addVote(Guild guild, Member author, int voteIndex)
    {

        Poll poll = polls.get(guild);

        if (poll.votes.containsKey(author.getUser().getId()))
        {

            tempList.get(guild).getTextChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("You only can vote once, " + author.getAsMention() + ".")
                            .build()
            ).queue(message ->
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            message.delete().queue();
                        }
                    }, 3000)
            );

            return;

        }
        poll.votes.put(author.getUser().getId(), voteIndex);
        polls.replace(guild, poll);
        tempList.get(guild).editMessage(getParsedPoll(poll, guild).build()).queue();

        try {
            savePoll(guild);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private void votePoll(String[] args, MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "On the guild is no poll running");
            executed(false, event);
            return;
        }

        Poll poll = polls.get(event.getGuild());

        int vote;

        try {
            vote = Integer.parseInt(args[1]);

            if (vote > poll.answer.size())
                throw new Exception();
        } catch (Exception exception) {
            CustomMsg.CMD_ERROR(event, "vote", "Invalid input");
            return;
        }

        addVote(event.getGuild(), Objects.requireNonNull(event.getMember()), vote);
        event.getMessage().delete().queue();

        executed(true, event);

    }

    private static void savePoll(Guild guild) throws IOException
    {

        if (!polls.containsKey(guild))
            return;

        File path = new File("SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId());
        if (!path.exists())
            path.mkdirs();

        String saveFile = "SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/vote.dat";
        Poll poll = polls.get(guild);

        FileOutputStream fos = new FileOutputStream(saveFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(poll);
        oos.close();

    }

    private static Poll getPoll(Guild guild) throws IOException, ClassNotFoundException
    {

        if (polls.containsKey(guild))
            return null;

        String saveFile = "SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/vote.dat";

        FileInputStream fis = new FileInputStream(saveFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Poll out = (Poll) ois.readObject();
        ois.close();
        return out;

    }

    public static void loadPoll(JDA jda)
    {

        jda.getGuilds().forEach(guild -> {
            File file = new File("SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/vote.dat");

            if (file.exists())
                try {
                    Poll poll = getPoll(guild);
                    polls.put(guild, poll);
                    assert poll != null;
                    tempList.put(guild, poll.getMessage(guild));
                } catch (IOException | ClassNotFoundException | NullPointerException exception) {
                    exception.printStackTrace();
                }
        });

    }

    private void statsPoll(MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "On the guild is no poll running");
            executed(false, event);
            return;
        }

        Poll poll = polls.get(event.getGuild());
        Guild guild = event.getGuild();

        if (!poll.getMessage(guild).getTextChannel().equals(event.getTextChannel()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "Tried to access poll from false channel");
            return;
        }

        textChannel.sendMessage(getParsedPoll(poll, guild).build()).queue();

        executed(true, event);

    }

    private void closePoll(MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "On the guild is no poll running");
            executed(false, event);
            return;
        }

        Guild guild = event.getGuild();
        Poll poll = polls.get(guild);

        if (!poll.getMessage(guild).getTextChannel().equals(event.getTextChannel()))
        {
            CustomMsg.CMD_ERROR(event, "vote", "Tried to access poll from false channel");
            executed(false, event);
            return;
        }


        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.MESSAGE_MANAGE) || event.getMember().getId().equals(poll.getCreator(guild).getId()))
        {

            tempList.get(event.getGuild()).delete().queue();
            polls.remove(guild);
            tempList.remove(guild);
            textChannel.sendMessage(getParsedPoll(poll, guild).build()).queue();
            message("Poll closed by " + event.getAuthor().getAsMention() + ".", new Color(0xE51770));

            executed(true, event);

        } else
        {
            CustomMsg.CMD_ERROR(event, "vote", "Tried to close a poll without the permissions");
            executed(false, event);
        }

        event.getMessage().delete().queue();

        new File("SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/vote.dat").delete();

    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "vote";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.MESSAGE_WRITE;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        textChannel = event.getTextChannel();

        if (args.length < 1)
        {
            CustomMsg.CMD_ERROR(event, "vote", "One or two positional arguments expected but zero found");
            executed(false, event);
            return;
        }

        switch (args[0])
        {

            case "create":
                createPoll(args, event);
                break;

            case "v":
                votePoll(args, event);
                break;

            case "stats":
                statsPoll(event);
                break;

            case "close":
                closePoll(event);
                break;

        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "vote");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "vote <option>``. For more information use ``" + BotSettings.PREFIX + "help``.";
    }

}
