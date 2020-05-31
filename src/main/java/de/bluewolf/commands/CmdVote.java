package de.bluewolf.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import de.bluewolf.settings.utils;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CmdVote implements Command, Serializable
{

    private static TextChannel channel;

    private static final HashMap<Guild, Poll> polls = new HashMap<>();

    private static final String[] EMOJI = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};

    private static class Poll implements Serializable
    {

        private final String creator;
        private final String heading;
        private final List<String> answers;
        private final HashMap<String, Integer> votes;

        private Poll(Member creator, String heading, List<String> answers)
        {
            this.creator = creator.getUser().getId();
            this.heading = heading;
            this.answers = answers;
            this.votes = new HashMap<>();
        }

        private Member getCreator(Guild guild)
        {
            return guild.getMember(Objects.requireNonNull(guild.getJDA().getUserById(creator)));
        }

    }

    private static void message(String content)
    {
        EmbedBuilder eb = new EmbedBuilder().setDescription(content).setColor(Color.RED);
        channel.sendMessage(eb.build()).queue();
    }

    private EmbedBuilder getParsedPoll(Poll poll, Guild guild)
    {

        StringBuilder ansSTR = new StringBuilder();
        final AtomicInteger count = new AtomicInteger();

        poll.answers.forEach(s ->
        {

            long votesCount = poll.votes.keySet().stream().filter(k -> poll.votes.get(k).equals(count.get() + 1)).count();
            ansSTR.append(EMOJI[count.get()]).append("  -  ").append(s).append("  -  Votes: `").append(votesCount).append("` \n");
            count.addAndGet(1);

        });

        return new EmbedBuilder()
                .setAuthor(poll.getCreator(guild).getEffectiveName() + "'s poll.", null, poll.getCreator(guild).getUser().getAvatarUrl())
                .setDescription(":pencil:   " + poll.heading + "\n\n" + ansSTR.toString())
                .setFooter("Enter " + utils.PREFIX + "vote v <number> to vote!", null)
                .setColor(Color.CYAN);

    }

    private void createPoll(String[] args, MessageReceivedEvent event)
    {

        if (polls.containsKey(event.getGuild()))
        {
            message("On this guild is already a vote running!");
            return;
        }

        String argsSTRG = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
        List<String> content = Arrays.asList(argsSTRG.split("\\|"));

        String heading = content.get(0);
        List<String> answers = new ArrayList<>(content.subList(1, content.size()));

        Poll poll = new Poll(Objects.requireNonNull(event.getMember()), heading, answers);
        polls.put(event.getGuild(), poll);

        channel.sendMessage(getParsedPoll(poll, event.getGuild()).build()).queue();
        event.getMessage().delete().queue();

    }

    private void votePoll(String[] args, MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            message("On this guild is currently no poll running!");
            return;
        }

        Poll poll = polls.get(event.getGuild());

        int vote;

        try {
            vote = Integer.parseInt(args[1]);
            if (vote > poll.answers.size())
                throw new Exception();
        } catch (Exception e)
        {
            message("Enter a valid number to vote!");
            return;
        }

        if (poll.votes.containsKey(event.getAuthor().getId()))
        {
            Message msg = event.getTextChannel().sendMessage(
                    new EmbedBuilder().setDescription("You only can vote once " + Objects.requireNonNull(event.getMember()).getAsMention() + "!").build()
            ).complete();

            new Timer().schedule(new TimerTask()
            {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, 3000);

            event.getMessage().delete().queue();
            return;
        }

        poll.votes.put(event.getAuthor().getId(), vote);
        polls.replace(event.getGuild(), poll);
        event.getMessage().delete().queue();

    }

    private void voteStats(MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            message("On this guild is currently no vote running!");
            return;
        }

        channel.sendMessage(getParsedPoll(polls.get(event.getGuild()), event.getGuild()).build()).queue();

    }

    private void closeVote(MessageReceivedEvent event)
    {

        if (!polls.containsKey(event.getGuild()))
        {
            message("On this guild is currently no vote running!");
            return;
        }

        Guild guild = event.getGuild();
        Poll poll = polls.get(guild);

        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.MESSAGE_MANAGE))
        {

            polls.remove(guild);
            channel.sendMessage(getParsedPoll(poll, guild).build()).queue();
            message("Poll closed by " + event.getAuthor().getAsMention() + ".");

        } else
            message("Sorry, but only a team member can close this poll!");


    }

    private void savePoll(Guild guild) throws IOException
    {

        if (!polls.containsKey(guild))
            return;

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

    public static void loadPolls(JDA jda)
    {

        jda.getGuilds().forEach(guild ->
        {

            File f = new File("SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/vote.dat");

            if (f.exists())
                try {
                    polls.put(guild, getPoll(guild));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

        });

    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {

        channel = event.getTextChannel();

        if (args.length < 1)
        {
            utils.ERROR_MSG(event, "Use '-help' to show all commands!");
            return;
        }

        switch (args[0])
        {

            case "create":
                createPoll(args, event);
                break;

            case "close":
                closeVote(event);
                break;

            case "v":
                votePoll(args, event);
                break;

            case "stats":
                voteStats(event);
                break;


        }

        polls.forEach(((guild, poll) ->
        {

            File path = new File("SERVER_SETTINGS/POLLS/" + guild.getName() + "-" + guild.getId() + "/");
            if (!path.exists())
                path.mkdirs();

            try {
                savePoll(guild);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }));

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
