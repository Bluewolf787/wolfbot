package de.bluewolf.core;

import de.bluewolf.commands.*;
import de.bluewolf.listener.CommandListener;
import de.bluewolf.listener.ReadyListener;
import de.bluewolf.settings.secret;
import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot
{

    private static JDA api;
    private static JDABuilder builder;

    public static void main(String[] args) throws LoginException {

        api = JDABuilder.createDefault(secret.TOKEN)
                .setAutoReconnect(true)
                .setStatus(utils.STATUS)
                .setActivity(utils.ACTIVITY)
                .build();

        initializeCommands();
        initializeListener();

    }


    private static void initializeCommands()
    {
        commandHandler.commands.put("ping", new CmdPing());
        commandHandler.commands.put("help", new CmdHelp());
        commandHandler.commands.put("clear", new CmdClear());
        commandHandler.commands.put("m", new CmdMusic());
        commandHandler.commands.put("music", new CmdMusic());
        commandHandler.commands.put("vote", new CmdVote());
        commandHandler.commands.put("game", new CmdGameroles());
        commandHandler.commands.put("news", new CmdNews());
    }

    private static void initializeListener()
    {
        // System Listener
        api.addEventListener(new CommandListener());
        api.addEventListener(new ReadyListener());
    }
}
