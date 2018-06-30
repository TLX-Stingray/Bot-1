package discord.bot;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class App extends ListenerAdapter {
    public static JDA jda;
    public static String prefix = Ref.getPrefix();
    public static String token = Ref.getToken();
    public static Set<String> badwords;
    public static String storeMod = System.getenv("STORAGE_MODIFIER");

    public static void main(String[] args) throws Exception {
        jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        jda.addEventListener(new App());
        jda.getPresence().setGame(Game.playing("on " + Integer.toString(jda.getGuilds().size()) + " Servers | help"));
        System.out.print("Bot running w/ token: ' " + token + " ' With prefix set to:  '" + prefix + "'\n");
        String fileName = "txt_Files/bad_word_list_UTF8.txt";
        ArrayList<String> myDict = new ArrayList<String>();
        InputStream resourcestream = App.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader r = new BufferedReader(new InputStreamReader(resourcestream));

        if (resourcestream == null) {
            throw new FileNotFoundException(fileName);
        }

        String line;
        while ((line = r.readLine()) != null) {
            myDict.add(line);
        }
        //Read File Content
        badwords = new HashSet<>(myDict);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent evt)
    {
            //Objects
            User objUser = evt.getAuthor();
            MessageChannel objMsgCh = evt.getChannel();
            TextChannel objtxtMsgCh = evt.getTextChannel();
            Message objMsg = evt.getMessage();
            String lowerMsg = objMsg.getContentRaw().toLowerCase();
            String userFull = objUser.getName() + "#" + objUser.getDiscriminator();
            Guild guild = evt.getGuild();
            String str = objMsg.getContentRaw();
            String[] splitStr = str.trim().split("\\s+");
            Properties guildproperties = new Properties();
            File propFile = new File(storeMod + guild.getId() + ".properties");
            Boolean propExists;
            if (propFile.exists()) {
                propExists = true;
                try {
                    FileInputStream propIn = new FileInputStream(propFile.getPath());
                    guildproperties.load(propIn);
                } catch (Exception e) {
                    System.out.print(e.getStackTrace().toString() + "/n");
                }

            } else {
                propExists = false;
            }

            //Message Validation
            if (evt.getAuthor().isBot()) return;
            if (objMsgCh instanceof PrivateChannel) return;

            if (objMsg.getContentRaw().contains(prefix) || objMsg.getContentRaw().contains(jda.getSelfUser().getAsMention())) {
                if (guild != null) {
                    System.out.print("Received: " + objMsg.getContentRaw() + " on Server: " + guild.getName() + " (" + guild.getId() + ") \n");
                } else {
                    System.out.print("Received: " + objMsg.getContentRaw() + " on Guild showing NULL \n");
                }
            }
            //Profanity check
            //for (String s : splitStr)
            //{
            //    if (badwords.contains(s.toLowerCase()))
            //    {
            //        System.out.print("Profanity Detected in message by: " + objUser.getName() + " (" + objUser.getId() + ") on server: "
            //                                + guild.getName() + " (" + guild.getId() + ") \n");
            //        objUser.openPrivateChannel().queue((channel) ->
            //        {
            //            channel.sendMessage("I know you might be mad, but please stop using profanity in your messages :(").queue();
            //            objMsg.delete().queue();
            //        });
            //    }
            //}

            //Commands
            //HELP
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "help") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " help")) {
                objMsgCh.sendMessage(HelpSys.buildGeneralHelp()).queue();
            }
//Config
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "config"))
            {
                if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR))
                {
                    objMsgCh.sendMessage(HelpSys.buildConfigHelp()).queue();
                } else
                {
                    objMsgCh.sendMessage("You need `ADMINISTRATOR` to use this command").queue();
                }
            }
            //CREATE
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "config create")) {
                if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)) {
                    Properties properties = new Properties();
                    File file = new File(storeMod + guild.getId() + ".properties");

                    if (!file.exists()) {
                        try {
                            FileOutputStream fileOut = new FileOutputStream(file);
                            properties.setProperty("ProfanityFilter", "false");
                            properties.setProperty("WelcomeMessage", "false");
                            properties.setProperty("AutoRoleOn", "false");
                            properties.setProperty("DelCommands", "false");

                            properties.store(fileOut, "Server Settings");
                            fileOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        objMsgCh.sendMessage("Config Initialized Success").queue();
                    } else
                    {
                        objMsgCh.sendMessage("Config was already initialized").queue();
                    }
                } else {
                    objMsgCh.sendMessage("You need `ADMINISTRATOR` to use this command").queue();
                }
            }
            //GET
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "config get"))
            {
                if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)){
                    if (propExists)
                    {
                        String properties = "";
                        for (String s : guildproperties.stringPropertyNames())
                        {
                            if (s.equalsIgnoreCase("AutoRole"))
                            {
                                Role autorole = guild.getRoleById(guildproperties.getProperty(s));
                                properties += "**" + s + "** =" + guildproperties.getProperty(s) + " (" + autorole.getName() + ") \n";
                            } else if (s.equalsIgnoreCase("WelcomeChannel"))
                            {
                                TextChannel welcomechannel = guild.getTextChannelById(guildproperties.getProperty(s));
                                properties += "**" + s + "** =" + guildproperties.getProperty(s) + " (" + welcomechannel.getAsMention() + ") \n";
                            } else {
                                properties += "**" + s + "** = " + guildproperties.getProperty(s) + " \n";
                            }

                        }
                        EmbedBuilder peb = new EmbedBuilder();
                        peb.setTitle("Current Properties for " + jda.getSelfUser().getName());
                        peb.setDescription("This is the current properties for " + jda.getSelfUser().getName() + " on " + guild.getName());
                        peb.addField("", properties, false);
                        peb.setColor(Color.RED);

                        objMsgCh.sendMessage(peb.build()).queue();

                    }else
                    {
                        objMsgCh.sendMessage("Please Initiate the properties file with `config create`").queue();
                    }
                } else
                {
                    objMsgCh.sendMessage("You need `ADMINISTRATOR` to use this command").queue();
                }
            }
            //AUTOROLE
            if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "config autorole")) {
                if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)) {
                    if (propExists) {
                        String value = objMsg.getContentRaw().toLowerCase().replace(prefix + "config autorole ", "");
                        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                            if (guild.getMember(jda.getSelfUser()).hasPermission(Permission.MANAGE_ROLES)) {
                                guildproperties.setProperty("AutoRoleOn", value);
                                try {
                                    FileOutputStream fileOut = new FileOutputStream(propFile);
                                    guildproperties.store(fileOut, "Updated Settings");
                                    fileOut.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                EmbedBuilder reb = new EmbedBuilder();
                                String msgValue = value;
                                if (value.equalsIgnoreCase("true"))
                                    msgValue = value + ", make sure to set your autorole by using `config setautorole [Role Name]`";
                                reb.addField("AutoRole set to:", msgValue, false);
                                objMsgCh.sendMessage(reb.build()).queue();
                            }else if (value.equalsIgnoreCase("true")) {
                                objMsgCh.sendMessage("Sorry, I need `MANAGE_ROLES` to be able to do Autorole").queue();
                            }


                        } else {
                            objMsgCh.sendMessage("Please specify a value betweeen `true` or `false`").queue();
                        }

                    } else {
                        objMsgCh.sendMessage("Please Initiate the properties file with `config create`").queue();
                    }
                } else {
                    objMsgCh.sendMessage("You need `ADMINISTRATOR` to use this command").queue();
                }
            }
            //SETAUTOROLE
            if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "config setautorole"))
            {
                String roleName = objMsg.getContentRaw().toLowerCase().replace(prefix + "config setautorole ", "");
                if (guild.getRolesByName(roleName, true).size() >= 1)
                {
                    List<Role> autoRoleResults= new ArrayList<>();
                    autoRoleResults = guild.getRolesByName(roleName, true);
                    Role autoRole = autoRoleResults.get(0);
                    List<Role> botRoles = new ArrayList<>();
                    botRoles = guild.getMember(jda.getSelfUser()).getRoles();
                    Role botHighestRole = botRoles.get(0);
                    Integer botHRpos = botHighestRole.getPosition();
                    Integer autoRolePos = autoRole.getPosition();
                    if (autoRolePos < botHRpos) {
                        guildproperties.setProperty("AutoRole", autoRole.getId());
                        try {
                            FileOutputStream fileOut = new FileOutputStream(propFile);
                            guildproperties.store(fileOut, "Updated Settings");
                            fileOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        EmbedBuilder reb = new EmbedBuilder();
                        reb.setTitle("Set Autorole to:");
                        reb.setDescription(autoRole.getName());
                        objMsgCh.sendMessage(reb.build()).queue();
                    } else {
                        objMsgCh.sendMessage("Sorry, I can't use a role higher than or equal to `" + botHighestRole.getName() + "`").queue();
                    }

                }else
                {
                    objMsgCh.sendMessage("Sorry, I can't find `" + roleName + "`").queue();
                }
            }
            //WELCOMEMESSAGE
        if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "config welcomemessage"))
        {
            if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)) {
                if (propExists) {
                    String value = objMsg.getContentRaw().toLowerCase().replace(prefix + "config welcomemessage ", "");
                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                        guildproperties.setProperty("WelcomeMessage", value);
                        try {
                            FileOutputStream fileOut = new FileOutputStream(propFile);
                            guildproperties.store(fileOut, "Updated Settings");
                            fileOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        EmbedBuilder reb = new EmbedBuilder();
                        String msgValue = value;
                        if (value.equalsIgnoreCase("true")) msgValue = value + ", make sure to set your WelcomeChannel by using `config welcomechannel [#TextChannelName]`";
                        reb.addField("WelcomeMessage set to:", msgValue, false);
                        objMsgCh.sendMessage(reb.build()).queue();

                    } else {
                        objMsgCh.sendMessage("Please specify a value betweeen `true` or `false`").queue();
                    }

                } else {
                    objMsgCh.sendMessage("Please Initiate the properties file with `Config create`").queue();
                }
            } else {
                objMsgCh.sendMessage("You need `ADMINISTRTOR` to use this command").queue();
            }
        }
        //WELCOMECHANNEL
        if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "config welcomechannel"))
        {
            if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)) {
                if (propExists){
                    String welcomeChannel = objMsg.getContentRaw().toLowerCase().replace(prefix + "config welcomechannel ", "");
                    welcomeChannel = welcomeChannel.replace("<#", "");
                    welcomeChannel = welcomeChannel.replace(">", "");
                    if (guild.getTextChannelById(welcomeChannel) != null) {
                        if (guild.getMember(jda.getSelfUser()).hasPermission(guild.getTextChannelById(welcomeChannel), Permission.MESSAGE_MANAGE)) {
                            guildproperties.setProperty("WelcomeChannel", welcomeChannel);
                            try {
                                FileOutputStream fileOut = new FileOutputStream(propFile);
                                guildproperties.store(fileOut, "Updated Settings");
                                fileOut.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            EmbedBuilder reb = new EmbedBuilder();
                            String msgValue = guild.getTextChannelById(welcomeChannel).getName();
                            reb.addField("Welcome Message Channel set to:", msgValue, false);
                            objMsgCh.sendMessage(reb.build()).queue();
                        }
                    }
                } else
                {
                    objMsgCh.sendMessage("Please Initiate the properties file with `Config create`").queue();
                }
            }else {
                objMsgCh.sendMessage("You need `ADMINISTRTOR` to use this command").queue();
            }
        }
        //DELCOMMANDS
        if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "config delcommands")) {
            if (guild.getMember(objUser).hasPermission(Permission.ADMINISTRATOR)) {
                if (propExists) {
                    String value = objMsg.getContentRaw().toLowerCase().replace(prefix + "config delcommands ", "");
                    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                        guildproperties.setProperty("DelCommands", value);
                        try {
                            FileOutputStream fileOut = new FileOutputStream(propFile);
                            guildproperties.store(fileOut, "Updated Settings");
                            fileOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        EmbedBuilder reb = new EmbedBuilder();
                        reb.addField("Delcommands set to", value, false);
                        objMsgCh.sendMessage(reb.build()).queue();
                    } else {
                        objMsgCh.sendMessage("Please specify a value betweeen `true` or `false`").queue();
                    }
                } else {
                    objMsgCh.sendMessage("You need `ADMINISTRTOR` to use this command").queue();
                }
            } else{
                objMsgCh.sendMessage("Please Initiate the properties file with `Config create`").queue();
            }
        }
//USERINFO
            //Check for Mention
            if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " userinfo")) {
                objUser.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + prefix + "userinfo {user as mention} `").queue();
                });
            }
            //execute command
            if (objMsg.getContentRaw().contains(prefix + "userinfo")) {
                String UserID = objUser.getId();
                Member newMemb = guild.getMemberById(UserID);
                User newUser = objUser;
                String msgNoCmd = lowerMsg.replace(prefix + "userinfo", "");
                //Interpret Command for Different user specified
                if (!msgNoCmd.equalsIgnoreCase("")) {
                    if (!msgNoCmd.contains("<@")) {
                        objUser.openPrivateChannel().queue((channel) ->
                        {
                            channel.sendMessage("You used my 'userinfo' command wrongly, make sure to mention the user you wish to check the info of in the message " +
                                    "for example: " + prefix + "userinfo @User . Hope you understand :grinning:").queue();
                        });

                        return;
                    }
                    String ID = msgNoCmd.replace(" <@", "");
                    ID = ID.replace(">", "");
                    newMemb = guild.getMemberById(ID);
                    newUser = newMemb.getUser();
                }
                //Init Embed
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("User Info of " + newUser.getName());
                eb.setColor(Ref.userCL);
                eb.setImage(newUser.getAvatarUrl());
                //Reinit Embed if user is a bot
                if (newUser.isBot()) {
                    eb.setTitle("Bot Info of " + newUser.getName());
                    eb.setColor(Ref.botCL);
                    eb.addField("THIS USER IS A BOT", "", false);
                }
                //Add Fields
                eb.addField("Display Name: ", newMemb.getEffectiveName(), false);
                eb.addField("ID:", newUser.getId(), false);
                eb.addField("Joined on", newMemb.getJoinDate().toLocalDate().toString(), false);
                eb.addField("Online State:", newMemb.getOnlineStatus().toString(), false);
                //Build and Send Embed
                objMsgCh.sendMessage(eb.build()).queue();
            }
    //BOTINFO
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "botinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " botinfo")) {
                //Init Embed
                EmbedBuilder seb = new EmbedBuilder();
                seb.setTitle("Server info of: " + jda.getSelfUser().getName() + " running on " + jda.getGuilds().size() + " servers");
                seb.setColor(Color.RED);
                //Get Bot Roles
                List<Role> botRoles = new ArrayList<>();
                botRoles = guild.getMember(jda.getSelfUser()).getRoles();
                String botRolesString = "";
                for (Role r : botRoles)
                {
                    botRolesString += r.getName() + "\n";
                }
                //Retrieved server list
                //add Fields
                seb.addField("Server Count:", Integer.toString(jda.getGuilds().size()), false);
                seb.addField("Uptime:", GetUptime.getUptime(), false);
                seb.addField("Roles:", botRolesString, false);
                //Build and Send Embed
                objMsgCh.sendMessage(seb.build()).queue();
            }
    //CHANNELINFO
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "channelinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " channelinfo")) {   //Init Embed
                EmbedBuilder ceb = new EmbedBuilder();
                ceb.setTitle("Channel info of " + objMsgCh.getName());
                ceb.setColor(Color.RED);
                //Add Fields
                ceb.addField("Channel ID: ", objMsgCh.getId(), false);
                ceb.addField("History size: ", Integer.toString(objMsgCh.getIterableHistory().complete().size() - 1), false);
                ceb.addField("Created on: ", objMsgCh.getCreationTime().toLocalDate().toString(), false);
                //Build and Send Embed
                objMsgCh.sendMessage(ceb.build()).queue();
            }
//SERVERINFO
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "serverinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " serverinfo")) {
                //Init Embed
                EmbedBuilder seb = new EmbedBuilder();
                seb.setTitle("Info About the server named: " + guild.getName());
                seb.setColor(Color.RED);
                seb.setImage(guild.getIconUrl());
                //Add Fields
                seb.addField("Server ID:", guild.getId(), false);
                seb.addField("Member Amount:", Integer.toString(guild.getMembers().size()), false);
                seb.addField("Created On:", guild.getCreationTime().toLocalDate().toString(), false);
                seb.addField("Owner:", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator() +
                        " (" + guild.getOwner().getUser().getId() + ")", false);
                seb.addField("Text Channel Amount:", Integer.toString(guild.getTextChannels().size()), false);
                //Get Text Channels
                String txtChannelList = "";
                for (TextChannel txt : guild.getTextChannels()) {
                    txtChannelList += txt.getName() + " (" + txt.getId() + ") \n";
                }
                seb.addField("Text Channels:", txtChannelList, false);
                seb.addField("Voice Channel Amount:", Integer.toString(guild.getVoiceChannels().size()), false);
                //Get Voice Channels
                String vcChannelList = "";
                for (VoiceChannel vc : guild.getVoiceChannels()) {
                    vcChannelList += vc.getName() + " (" + vc.getId() + ") \n";
                }
                seb.addField("Voice Channels:", vcChannelList, false);
                //Build and Send Embed
                objMsgCh.sendMessage(seb.build()).queue();
            }
//CLEAR
            //Check for Mention
            if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " clear")) {
                objUser.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + prefix + "clear (amount) `").queue();
                });
            }
            //Execute command
            if (objMsg.getContentRaw().toLowerCase().contains(prefix + "clear") && guild.getMember(jda.getSelfUser()).hasPermission(Permission.MESSAGE_MANAGE)) {
                String samount = objMsg.getContentRaw().replace(prefix + "clear ", "");
                //Test if amount is Integer
                if ((Ref.isInteger(samount))) {
                    int i = Integer.parseInt(samount) + 1;
                    if (i <= 100) {
                        List<Message> retrieved = objtxtMsgCh.getHistory().retrievePast(i).complete();
                        objtxtMsgCh.deleteMessages(retrieved).queue();
                    }
                } else {
                    objUser.openPrivateChannel().queue((channel) ->
                    {   //Notify User via PM
                        channel.sendMessage("You must make sure you use the clear command correctly '" + prefix
                                + "clear {amount)', if the format is correct you should make sure you used a valid integer").queue();
                    });
                }

            }
//MESSAGE COMMAND
            //Check for Mention
            if (objMsg.getContentRaw().toLowerCase().startsWith(jda.getSelfUser().getAsMention() + " message")) {
                objUser.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + prefix + "message (Message) `").queue();
                });
            }

            if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "message")) {
                User owner = jda.getUserById("167336416861224961");
                String msgNoCommand = objMsg.getContentRaw().replace(prefix + "message", "");
                EmbedBuilder peb = new EmbedBuilder();
                peb.setTitle("Message From: " + objUser.getName() + " (" + objUser.getId() + ") ");
                peb.addField("", msgNoCommand, false);

                owner.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage(peb.build()).queue();
                });
            }

//DEV ONLY COMMANDS
            //UPDATE SOON
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "updatesoon")) {
                if (objUser.getId().equals("167336416861224961")) {
                    jda.getPresence().setGame(Game.playing("UPDATE ROLLING OUT"));
                } else {
                    System.out.print("Dev Command from Non-Dev \n");
                    objMsgCh.sendMessage("You have to be a Developer to use that command, sorry :(").queue();
                }
            }
            //UPDATE CANCEL
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "updatecan")) {
                if (objUser.getId().equals("167336416861224961")) {
                    jda.getPresence().setGame(Game.playing("on " + Integer.toString(jda.getGuilds().size()) + " | help"));
                } else {
                    System.out.print("Dev Command from Non-Dev \n");
                    objMsgCh.sendMessage("You have to be a Developer to use that command, sorry :(");
                }
            }
            //SERVERLIST
            if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "serverlist")) {
                if (objUser.getId().equals("167336416861224961")) {
                    String finalList = "";
                    for (Guild gld : jda.getGuilds()) {
                        finalList += gld.getName() + " (" + gld.getId() + ") \n";
                    }
                    EmbedBuilder eServers = new EmbedBuilder();
                    eServers.setTitle("Serverinfo of " + jda.getSelfUser().getName());
                    eServers.setColor(Color.RED);
                    eServers.addField("Server List:", finalList, false);

                    objMsgCh.sendMessage(eServers.build()).queue();
                } else {
                    System.out.print("Dev Command from Non-Dev \n");
                    objMsgCh.sendMessage("You have to be a Developer to use that command, sorry :(");
                }
            }
            //GETINVITE
            if (objMsg.getContentRaw().toLowerCase().startsWith(prefix + "getinvite")) {
                if (objUser.getId().equals("167336416861224961")) {
                    String supGuildId = objMsg.getContentRaw().replace(prefix + "getinvite ", "");
                    if (!supGuildId.isEmpty()) {
                        if (jda.getGuildById(supGuildId).isAvailable()) {
                            Guild getInvFrom = jda.getGuildById(supGuildId);
                            if (jda.getGuilds().contains(getInvFrom)) {
                                List<Invite> invList = new ArrayList<>();
                                List<TextChannel> txtChList = getInvFrom.getTextChannels();
                                for (TextChannel tc : txtChList) {
                                    if (getInvFrom.getMember(jda.getSelfUser()).hasPermission(tc, Permission.CREATE_INSTANT_INVITE)) {
                                        invList.add(tc.createInvite().complete());
                                    }
                                }
                                if (invList.size() >= 1) {
                                    objMsgCh.sendMessage("Here you go --> " + invList.get(0).getURL()).queue();
                                } else {
                                    objMsgCh.sendMessage("Sorry, I can't find anything :pensive: ").queue();
                                }
                            }
                        }
                    }
                } else {
                    System.out.print("Dev Command from Non-Dev \n");
                    objMsgCh.sendMessage("You have to be a Developer to use that command, sorry :(");
                }
            }

//Delete Message
            String delCommands = "";
            if (guildproperties.containsKey("DelCommands")) {delCommands = guildproperties.getProperty("DelCommands");}
            else
            {
                if (propExists) {
                    guildproperties.setProperty("DelCommands", "true");
                    try {
                        FileOutputStream fileOut = new FileOutputStream(propFile);
                        guildproperties.store(fileOut, "Updated Settings");
                        fileOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    delCommands = "false";
                } else
                {
                    delCommands = "true";
                }
            }
            if (delCommands.equalsIgnoreCase("true"))
            {
                if (objMsg.getContentRaw().toLowerCase().contains(prefix))
                {
                    objMsg.delete().queue();
                }
            }


        }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent evt)
    {
        Guild guild = evt.getGuild();
        Properties guildproperties = new Properties();
        File propFile = new File(storeMod + guild.getId() + ".properties");
        Boolean propExists;
        if (propFile.exists()) {
            propExists = true;
            try {
                FileInputStream propIn = new FileInputStream(propFile.getPath());
                guildproperties.load(propIn);
            } catch (Exception e) {
                System.out.print(e.getStackTrace().toString() + "/n");
            }

        } else {
            propExists = false;
        }

        if (propExists)
        {
            String welcomeMessageOnStr = guildproperties.getProperty("WelcomeMessage");
            if (welcomeMessageOnStr.equalsIgnoreCase("true"))
            {
                String newGuyAsMention = evt.getMember().getUser().getAsMention();
                String guildName = guild.getName();
                String Message = "Welcome to " + guildName + ", " + newGuyAsMention + ", we hope you enjoy your time here :smiley: ";
                if (guildproperties.containsKey("WelcomeChannel"))
                {
                    String welcomeChannelS = guildproperties.getProperty("WelcomeChannel");
                    TextChannel welcomeChannel = guild.getTextChannelById(welcomeChannelS);
                    welcomeChannel.sendMessage(Message).queue();
                }
            }

            String autoRoleOn = guildproperties.getProperty("AutoRoleOn");
            if (guildproperties.containsKey("AutoRole"))
            {
                String roleId = guildproperties.getProperty("AutoRole");
                Role AutoRole = guild.getRoleById(roleId);
                guild.getController().addRolesToMember(evt.getMember(), AutoRole).queue();
            }
        }
    }
}






