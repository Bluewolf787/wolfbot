package de.bluewolf.wolfbot.listener;

import de.bluewolf.wolfbot.utils.ConsoleColors;
import de.bluewolf.wolfbot.utils.CustomMsg;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;

/**
 * @author Bluewolf787
 * @project wolfbot
 * @package de.bluewolf.wolfbot.listener
 * @created 25/Sep/2020 - 23:19
 */

public class DirectMsgListener extends ListenerAdapter
{

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {

        String[] msgs = new String[]
                {
                        "Hi, what's up?", "My favorite pets are cats! :cat:", "Dogs are nice! :dog:", "I want world domination!".toUpperCase(),
                        "What's your purpose?", "I had a wonderful dream...you haven't written me.", "Did someone said something?",
                        "OH, WOW. How I get here???", "I'm a time traveler and I have to warn you...your life is changing. Watch out!",
                        "Oh, hi mate! How are you going?",

                };

        String receivedMsg = event.getMessage().toString().toLowerCase();

        if (event.getMessage().getAuthor() == event.getJDA().getSelfUser())
            return;
        else
        {
            System.out.println(
                    CustomMsg.INFO_PREFIX + "Received a private message: "
                            + ConsoleColors.BLACK + "'" + ConsoleColors.PURPLE + receivedMsg + ConsoleColors.BLACK + "' " + ConsoleColors.RESET
                            + "at " + ConsoleColors.GREEN + java.time.LocalDateTime.now() + ConsoleColors.RESET
            );

            int answer = new Random().nextInt(msgs.length);
            event.getChannel().sendMessage(msgs[answer]).queue();
        }

        event.getChannel().close().queue();

    }

}
