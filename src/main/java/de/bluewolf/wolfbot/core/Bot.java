package de.bluewolf.wolfbot.core;

import de.bluewolf.wolfbot.commands.bot_administration.*;
import de.bluewolf.wolfbot.commands.chat.*;
import de.bluewolf.wolfbot.commands.management.*;
import de.bluewolf.wolfbot.commands.moderation.*;
import de.bluewolf.wolfbot.listener.*;
import de.bluewolf.wolfbot.settings.BotSettings;
import de.bluewolf.wolfbot.utils.DatabaseHelper;
import de.bluewolf.wolfbot.settings.Secret;
import de.bluewolf.wolfbot.commands.CmdPing;
import de.bluewolf.wolfbot.utils.Timer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class Bot
{

    public static JDA api;

    public static void main(String[] args) throws LoginException
    {

        api = JDABuilder.createDefault(Secret.TOKEN)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true)
                .setStatus(BotSettings.STATUS)
                .setActivity(BotSettings.ACTIVITY)
                .build();

        Timer.startTimer();

        initializeDatabase();
        initializeListener();
        initializeCommands();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseHelper.disconnect();
            System.out.println("\nBot runs for " + Timer.getElapsedTime());
        }));

    }

    private static void initializeDatabase()
    {
        DatabaseHelper.connect();
        DatabaseHelper.createBotStatsTable();
        DatabaseHelper.createRolesTable();
        DatabaseHelper.createPermissionsTable();
    }

    private static void initializeCommands()
    {
        // | REGISTER COMMANDS for the CommandHandler | //

        // -- bot administrator
        CommandHandler.commands.put("settings", new CmdSettings());
        CommandHandler.commands.put("bot", new CmdBot());
        CommandHandler.commands.put("news", new CmdNews());

        // -- guild administration

        // -- moderation
        CommandHandler.commands.put("clear", new CmdClear());
        CommandHandler.commands.put("ban", new CmdBan());
        CommandHandler.commands.put("unban", new CmdUnban());
        CommandHandler.commands.put("kick", new CmdKick());

        // -- management
        CommandHandler.commands.put("manageroles", new CmdManageRoles());
        CommandHandler.commands.put("role", new CmdRole());
        CommandHandler.commands.put("game", new CmdGame());
        CommandHandler.commands.put("help", new CmdHelp());

        // -- chat
        CommandHandler.commands.put("m", new CmdMusic());
        CommandHandler.commands.put("music", new CmdMusic());
        CommandHandler.commands.put("vote", new CmdVote());

        // -- testing
        CommandHandler.commands.put("ping", new CmdPing());

        // | REGISTER COMMANDS with Permissions | //
        BotSettings.commandsWithPermissions.put("news", Permission.ADMINISTRATOR.getOffset());
        BotSettings.commandsWithPermissions.put("manageroles", Permission.MANAGE_ROLES.getOffset());
        BotSettings.commandsWithPermissions.put("role", Permission.MANAGE_ROLES.getOffset());
        BotSettings.commandsWithPermissions.put("clear", Permission.MESSAGE_MANAGE.getOffset());
        BotSettings.commandsWithPermissions.put("ban", Permission.BAN_MEMBERS.getOffset());
        BotSettings.commandsWithPermissions.put("unban", Permission.BAN_MEMBERS.getOffset());
        BotSettings.commandsWithPermissions.put("kick", Permission.KICK_MEMBERS.getOffset());
        BotSettings.commandsWithPermissions.put("game", Permission.MESSAGE_WRITE.getOffset());
        BotSettings.commandsWithPermissions.put("help", Permission.MESSAGE_WRITE.getOffset());
        BotSettings.commandsWithPermissions.put("music", Permission.VOICE_CONNECT.getOffset());
        BotSettings.commandsWithPermissions.put("vote", Permission.MESSAGE_WRITE.getOffset());
        BotSettings.commandsWithPermissions.put("ping", Permission.ADMINISTRATOR.getOffset());
    }

    private static void initializeListener()
    {
        // System Listener
        api.addEventListener(new CommandListener());
        api.addEventListener(new ReadyListener());
        api.addEventListener(new BotGuildListener());
        api.addEventListener(new GuildAvailableListener());
        api.addEventListener(new DirectMsgListener());
    }
}
