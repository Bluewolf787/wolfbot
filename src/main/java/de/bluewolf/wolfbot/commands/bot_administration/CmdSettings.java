package de.bluewolf.wolfbot.commands.bot_administration;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.ConsoleColors;
import de.bluewolf.wolfbot.utils.CustomMsg;
import de.bluewolf.wolfbot.settings.Secret;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class CmdSettings implements Command
{

    static ConsoleColors colors = new ConsoleColors();

    @Override
    public boolean called(String[] args, MessageReceivedEvent event)
    {
        String author = event.getAuthor().getId();

        if (author.equals(Secret.ID))
            return false;
        else
        {
            CustomMsg.NO_PERM(event, "settings");
            return true;
        }
    }

    private void restart(MessageReceivedEvent event)
    {

        executed(true, event);

        BotSettings.DOWNTIME = true;

        event.getJDA().getPresence().setStatus(BotSettings.DOWNTIME_STATUS);
        event.getJDA().getPresence().setActivity(BotSettings.RESTART_ACTIVITY);

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setDescription(
                                "This Bot is `RESTARTING` in 5 seconds."
                        ).build()
        ).queue();

        System.out.println(CustomMsg.INFO_PREFIX + colors.YELLOW + "This Bot is RESTARTING in 5 seconds.");

        // TODO: restart

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.ORANGE)
                                .setDescription(
                                        "This Bot is now `STOPPED` and will be `RESTARTING`."
                                ).build()
                ).queue();

                System.out.println(CustomMsg.INFO_PREFIX + colors.RED + "This Bot is now STOPPED and will be RESTARTING.");

                System.exit(0);
            }
        }, 5000);



    }

    private void stop(MessageReceivedEvent event)
    {

        executed(true, event);

        BotSettings.DOWNTIME = true;

        event.getJDA().getPresence().setStatus(BotSettings.DOWNTIME_STATUS);
        event.getJDA().getPresence().setActivity(BotSettings.STOP_ACTIVITY);

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(
                                "This Bot is `STOPPING` in 5 seconds."
                        ).build()
        ).queue();

        System.out.println(CustomMsg.INFO_PREFIX + colors.RED + "This Bot is STOPPING in 5 seconds.");

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription(
                                        "This Bot is now `STOPPED`."
                                ).build()
                ).queue();

                System.out.println(CustomMsg.INFO_PREFIX + colors.RED + "This Bot is now STOPPED." + colors.RESET);

                System.exit(0);
            }
        }, 5000);

    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException
    {

        if (args.length < 1)
        {
            CustomMsg.CMD_ERROR(event, "settings", "One positional argument expected but zero found");
            executed(false, event);
            return;
        }

        switch (args[0])
        {
            case "restart":
                restart(event);
                break;

            case "stop":
                stop(event);
                break;
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {
        if (success)
            CustomMsg.COMMAND_EXECUTED(event, "settings");
        else
            CustomMsg.HELP_MSG(event, help());
    }

    @Override
    public String help()
    {
        return "Use ``" + BotSettings.PREFIX + "settings <option>``. For more information use ``" + BotSettings.PREFIX + "help s``.";
    }

}
