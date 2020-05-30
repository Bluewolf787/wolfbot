package de.bluewolf.core;

import de.bluewolf.listener.readyListener;
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
                .setActivity(utils.ACTIVITY)
                .build();

        initializeCommands();
        initializeListener();

       /* try {
            api = builder.build().awaitReady();
        } catch (LoginException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }*/

    }


    private static void initializeCommands()
    {
    }

    private static void initializeListener()
    {

        api.addEventListener(new readyListener());

    }
}
