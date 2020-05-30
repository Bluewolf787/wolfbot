package de.bluewolf.listener;

import de.bluewolf.settings.utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class readyListener extends ListenerAdapter
{

    public void onReady(ReadyEvent event)
    {

        StringBuilder out = new StringBuilder("\nLogged in as ").append(utils.ConsoleColors.YELLOW).append(event.getJDA().getSelfUser().getAsTag()).append(utils.ConsoleColors.RESET)
                .append(". This Bot is running on following servers: \n\n");

        for (Guild guild : event.getJDA().getGuilds())
        {
            out.append(utils.ConsoleColors.GREEN).append(guild.getName()).append(utils.ConsoleColors.RESET)
                    .append(" (").append(utils.ConsoleColors.GREEN).append(guild.getId()).append(utils.ConsoleColors.RESET)
                    .append(") \n---------------------------- \n");
        }

        System.out.println(out);

    }

}
