# WolfBot

# Table of Contents
1. [Preface](#preface)
2. [Use WolfBot](#use-wolfbot)
3. [Features](#features)
    1. [Commands](#commands)

## Preface
WolfBot is a JDA Discord Bot. It's one of my first bigger projects I've ever coded and "finished" (I will still work on it).
Don't be too serious, I know not everything makes much sense, and the code is not the best and can be optimized in many points,
but the solutions I found and used were the best working for me. I learned and had a lot of fun and added some features only for learning 
purpose.

---

## Use WolfBot
[[Click here]](https://discord.com/api/oauth2/authorize?client_id=346302891998576650&permissions=335113463&scope=bot)
when you like to add WolfBot to your Discord server.
>It is not intended to self-hosted the bot nor to edit the source code

# Features
## Commands
Command prefix: `-`

Categories | [Administration](#administration) | [Management](#management) | [Moderation](#moderation) | [chat](#chat)  
---- | ---- | ---- | ---- | ----
**Commands** | [setchannel](#setchannel) | [manageroles](#manageroles) | [ban](#ban) | [help](#help)
 |  | [setpermission](#setpermission) | [role](#role) | [unban](#unban) | [game](#game)
 |  |  |  | [kick](#kick) | [music](#music)
 |  |  |  | [clear](#clear) | [vote](#vote)
 |  |  |  |  | [avatar](#avatar)

### Administration

+ ###### setchannel
  Sets the channel for the execution of a specific command
  <br><br>
  **Default Permissions:** ``Manage Channels``
  <br><br>
  **Alias:** ``setchannel``, ``channel``
  <br><br>
  **Usage:**
  ```
  setchannel <command>           // Sets the current channel for the command
  setchannel <command> <channel> // Sets the specified channel for the command
  ```

+ ###### setpermission
  Sets the permission which are needed to execute a specific command
  <br><br>
  **Default Permissions:** ``Manage Permissions``
  <br><br>
  **Alias:** ``setpermission``, ``permission``
  <br><br>
  **Usage:**
  ````
  setpermission <command> <permission> // Sets the specified permission for the command
  ````

### Management

+ ###### manageroles
  Creates and deletes roles or only adds roles to the database or removes
  <br><br>
  **Default Permissions:** ``Manage Roles``
  <br><br>
  **Alias:** ``manageroles``, ``mroles``
  <br><br>
  **Usage:**
  ````
  // Roles can be created/added for two types: general or game
  // Roles can be created/added with optional one or two aliases 
  
  manageroles create <type> role:<rolename> alias1:<alias> alias2:<alias>  // Creates a new role on the guild for the specified type with optional one or two alieases
  manageroles delete <type> role<rolename>                                 // Deletes a role from the specified type
  
  manageroles add <type> role:<rolename> alias1:<alias> alias2:<alias>     // Adds a new/existing role to the database for the specified type with optional one or two alieases
  manageroles remove <type> role:<rolename>                                // Removes a role from the specified type from the database
  
  manageroles list <type> // Lists all roles from the specified type
  ````

+ ###### role
  Adds a role from type general to guild member or removes it
  <br><br>
  **Default Permissions:** ``Manage Roles``
  <br><br>
  **Alias:** ``role``
  <br><br>
  **Usage:**
  ````
  // User Tag format: ExampleName#1234
  role add member:<usertag> role:<rolename>     // Adds the specified role from type general to a guild member
  role remove member:<usertag> role:<rolename>  // Reomves the spicified role from type general from a guild member
  
  role info // Lists all general roles
  ````

### Moderation

+ ###### ban
  Bans a member on the guild
  <br><br>
  **Default Permissions:** ``Ban Members``
  <br><br>
  **Alias:** ``ban``
  <br><br>
  **Usage:**
  ````
  // Get the user ID when entering \@Username in a Discord channel
  
  ban <userid> <reason> // Bans the specified user for the reason
  ````

+ ###### unban
  Revokes a ban of a user on the guild
  <br><br>
  **Default Permissions:** ``Ban Members``
  <br><br>
  **Alias:** ``unban``
  <br><br>
  **Usage:**
  ````
  // Get the user ID when entering \@Username in a Discord channel
  
  unban <userid> // Revokes a ban of the specified user
  ````

+ ###### kick
  Kicks a member on the guild
  <br><br>
  **Default Permissions:** ``Kick Members``
  <br><br>
  **Alias:** ``kick``
  <br><br>
  **Usage:**
  ````
  // Get the user ID when entering \@Username in a Discord channel
  
  kick <userid> <reason> // Kicks the specified user for the reason
  ````

+ ###### clear
  Delete messages in a text channel
  <br><br>
  **Default Permissions:** ``Manage Messages``
  <br><br>
  **Alias:** ``clear``
  <br><br>
  **Usage:**
  ````
  clear <amount of messages> // Deletes the specified amount of messages (between 2 and 100) 
  ````

### Chat

+ ###### help
  Sends a command overview as private message or get mor information for a specific command
  <br><br>
  **Default Permissions:** ``Send Messages``
  <br><br>
  **Alias:** ``help``
  <br><br>
  **Usage:**
  ````
  help            // Send a command overview as private message
  help s          // Send a command overview of staff commands as private message
  help <command>  // Get more information about the spicified command
  ````

+ ###### game
  Adds a game role to the guild member
  <br><br>
  **Default Permissions:** ``Send Messages``
  <br><br>
  **Alias:** ``game``
  <br><br>
  **Usage:**
  ````
  game <rolename> // Adds a role from type game to message sender
  
  game info       // List all game roles
  ````

+ ###### music
  Plays songs from YouTube
  <br><br>
  **Default Permissions:** ``Voice Connect``
  <br><br>
  **Alias:** ``music``, ``m``
  <br><br>
  **Usage:**
  ````
  // Message sender has to be in a voice channel
  
  music play <link>  // Plays a songs from YouTube (Enter video link or name)
  music stop         // Stops the player and clears the playlist
  music skip         // Skips the current track
  music shuffle      // Toggle shuffle mode
  music info/track   // Shows the currently playing song
  music queue        // Shows the songs in the queue
  music pause        // Pauses the player
  music unpause      // Unpauses the player
  ````

+ ###### vote
  Creates a poll on the guild
  <br><br>
  **Default Permissions:** ``Send Messages``
  <br><br>
  **Alias:** ``vote``
  <br><br>
  **Usage:**
  ````
  vote create <title|answer option 1|answer option 2|...>   // Creates a new poll with specified title and answer options
  vote close                                                // Closes the current poll
  vote v <number>                                           // Votes for answer option
  vote stats                                                // Shows the stats of the currently running poll
  ````

+ ###### avatar
  Gets the avatar URL from a user
  <br><br>
  **Default Permissions:** ``Send Messages``
  <br><br>
  **Alias:** ``avatar``
  <br><br>
  **Usage:**
  ````
  // Usertag format: ExampleName#1234
  
  avatar <usertag>   // Gets the avatar URL from a user
  ````