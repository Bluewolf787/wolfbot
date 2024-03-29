package de.bluewolf.wolfbot.core;

import de.bluewolf.wolfbot.commands.Command;
import de.bluewolf.wolfbot.utils.CustomMsg;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class CommandHandler
{

    public static final CommandParser parse = new CommandParser();
    public static HashMap<String, Command> commands = new HashMap<>();

    public static void handleCommand(CommandParser.CommandContainer cmd) throws IOException, ParseException, SQLException {

        if(commands.containsKey(cmd.invoke))
        {

            boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

            if (!safe)
                commands.get(cmd.invoke).action(cmd.args, cmd.event);

        }
        else
        {
            CustomMsg.UNKNOWN_CMD(cmd.event, cmd.raw);
        }

    }

}
