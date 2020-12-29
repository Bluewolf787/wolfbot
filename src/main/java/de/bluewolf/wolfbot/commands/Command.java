package de.bluewolf.wolfbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public interface Command
{

    boolean called(String[] args, MessageReceivedEvent event) throws SQLException;
    void action(String[] args, MessageReceivedEvent event) throws ParseException, IOException, SQLException;
    void executed(boolean success, MessageReceivedEvent event);
    String help();


}
