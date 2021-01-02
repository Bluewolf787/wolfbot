package de.bluewolf.wolfbot.commands.chat;

import de.bluewolf.wolfbot.audio.AudioInfo;
import de.bluewolf.wolfbot.audio.PlayerSendHandler;
import de.bluewolf.wolfbot.audio.TrackManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CmdMusic implements Command
{

    private static final int PLAYLIST_LIMIT = 1000;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();


    public CmdMusic()
    {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer (Guild guild)
    {
        AudioPlayer audioPlayer = MANAGER.createPlayer();
        TrackManager manager = new TrackManager(audioPlayer);
        audioPlayer.addListener(manager);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(audioPlayer));

        PLAYERS.put(guild, new AbstractMap.SimpleEntry<>(audioPlayer, manager));

        return audioPlayer;
    }

    private boolean hasPlayer(Guild guild)
    {
        return PLAYERS.containsKey(guild);

    }

    private AudioPlayer getPlayer(Guild guild)
    {
        if(hasPlayer(guild))
            return PLAYERS.get(guild).getKey();
        else
            return createPlayer(guild);
    }

    private TrackManager getManager(Guild guild)
    {
        return PLAYERS.get(guild).getValue();
    }

    private boolean isIdle(Guild guild)
    {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() != null;
    }

    private void loadTrack(String identifier, Member author, Message msg)
    {

        Guild guild = author.getGuild();
        getPlayer(guild);

        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler()
        {

            @Override
            public void trackLoaded(AudioTrack track)
            {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                //playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT : playlist.getTracks().size());
                for (int i = 0; i < (Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT)); i++)
                {
                    getManager(guild).queue(playlist.getTracks().get(i), author);
                }
            }

            @Override
            public void noMatches()
            {
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
            }
        });

    }

    private void skip(Guild guild)
    {
        getPlayer(guild).stopTrack();
    }

    private String getTimestamp(long milis)
    {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);

        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String buildQueueMessages(AudioInfo info)
    {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long lenght = trackInfo.length;

        return "`[ " + getTimestamp(lenght) + " ]` " + title + "\n";
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) throws SQLException
    {
        String command = "music";
        Permission permission;
        ResultSet getPermission = DatabaseHelper.query("SELECT Permission FROM Permissions WHERE GuildId = '" + event.getGuild().getId() + "' AND Cmd = '" + command + "';");
        if (getPermission.next())
            permission = Permission.getFromOffset(getPermission.getInt("Permission"));
        else
            permission = Permission.VOICE_CONNECT;

        return BotSettings.checkPermissions(event, permission, command);
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {

        guild = event.getGuild();

        if(args.length < 1)
        {
            CustomMsg.CMD_ERROR(event, "music", "One or two positional arguments expected but zero found");
            executed(false, event);
            return;
        }

        switch (args[0].toLowerCase())
        {

            case "play":

                if(args.length < 2) {
                    CustomMsg.CMD_ERROR(event, "music", "Two positional arguments expected but only one or zero found");
                    executed(false, event);
                    return;
                }

                String input = Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);

                if(!(input.startsWith("http://") || input.startsWith("https://")))
                    input = "ytsearch: " + input;

                loadTrack(input, Objects.requireNonNull(event.getMember()), event.getMessage());
                // AudioTrack track1 = getPlayer(guild).getPlayingTrack();
                // AudioTrackInfo info1 = track1.getInfo();
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.MAGENTA)
                                .setDescription(
                                        "Track added to queue!"
                                )
                                .build()
                ).queue();

                executed(true, event);

                break;

            case "skip":

                if (hasPlayer(guild))
                {

                    for (int i = (args.length > 1 ? Integer.parseInt(args[1]) : 1); i == 1; i--)
                    {
                        skip(guild);
                        event.getTextChannel().sendMessage(
                                new EmbedBuilder()
                                        .setColor(Color.MAGENTA)
                                        .setDescription(
                                                "Track was skipped!"
                                        )
                                        .build()
                        ).queue();
                    }

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

            case "stop":

                if (hasPlayer(guild))
                {

                    getManager(guild).purgeQueue();
                    skip(guild);
                    guild.getAudioManager().closeAudioConnection();
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setDescription(
                                            "The player is now stopped!"
                                    )
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;


            case "shuffle":

                if (hasPlayer(guild))
                {

                    getManager(guild).shuffleQueue();
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setDescription(
                                            "The queue will now shuffle!"
                                    )
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

            case "info":
            case "track":

                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();

                // TODO: try/catch NUllPointerException

                if (hasPlayer(guild))
                {

                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setTitle("Current Track Info")
                                    .setDescription("")
                                    .addField("Title", info.title, false)
                                    .addField("Duration", "`[ " + getTimestamp(track.getPosition()) + "/ " + getTimestamp(track.getDuration()) + " ]`", false)
                                    .addField("Author", info.author, false)
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

            case "queue":

                int pageNumb = args.length > 1 ? Integer.parseInt(args[1]) : 1;

                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;

                if (hasPlayer(guild))
                {

                    getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessages(audioInfo)));

                    if (tracks.size() > 20)
                        trackSublist = tracks.subList((pageNumb-1)*20, (pageNumb-1)*20+20);
                    else
                        trackSublist = tracks;

                    //String out = trackSublist.stream().collect(Collectors.joining("\n"));
                    String out = String.join("\n", trackSublist);
                    int pageNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;

                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setDescription(
                                            "**CURRENT QUEUE**\n\n" + // getManager(guild).getQueue().stream()
                                                    "*[Tracks | Page " + pageNumb + " / " + pageNumbAll + "]*\n" + out
                                    )
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

            case "pause":

                if (hasPlayer(guild))
                {

                    getPlayer(guild).setPaused(true);
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setDescription(
                                            "The player is now paused!"
                                    )
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

            case "unpause":

                if (hasPlayer(guild))
                {

                    getPlayer(guild).setPaused(false);
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.MAGENTA)
                                    .setDescription(
                                            "The player is now unpaused!"
                                    )
                                    .build()
                    ).queue();

                }
                else
                    event.getTextChannel().sendMessage(CustomMsg.NO_TRACK).queue();

                executed(true, event);

                break;

        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "music");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "music <option>``. For more information use ``" + BotSettings.PREFIX + "help``.";
    }

}

