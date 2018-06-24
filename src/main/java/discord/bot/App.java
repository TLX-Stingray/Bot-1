package discord.bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App extends ListenerAdapter {
    public static JDA jda;
    public static String prefix = Ref.getPrefix();
    public static String token = Ref.getToken();
    public static Set<String> badwords;

    public static void main(String[] args) throws Exception {
        jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        jda.addEventListener(new App());
        jda.getPresence().setGame(Game.playing("Prefix: " + prefix + " | use " + prefix + "help | servers: " + jda.getGuilds().size()));
        System.out.print("Bot running w/ token: ' " + token + " ' With prefix set to:  '" + prefix + "'\n");
        String fileName = "txt_Files/bad_word_list_UTF8.txt";
        ArrayList<String> myDict = new ArrayList<String>();
        InputStream resourcestream = App.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader r = new BufferedReader(new InputStreamReader(resourcestream));


        if (resourcestream == null)
        {
            throw new FileNotFoundException(fileName);
        }

        String line;
        while ((line=r.readLine()) != null) {
            myDict.add(line);
        }
        //Read File Content
        badwords = new HashSet<>(myDict);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
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
        for (String s : splitStr)
        {
            if (badwords.contains(s.toLowerCase()))
            {
                System.out.print("Profanity Detected in message by: " + objUser.getName() + " (" + objUser.getId() + ") on server: "
                                        + guild.getName() + " (" + guild.getId() + ") \n");
                objUser.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("I know you might be mad, but please stop using profanity in your messages :(").queue();
                    objMsg.delete().queue();
                });
            }
        }
        //Commands
        //HELP
        if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "help") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " help")) {
            //Print in Console
            System.out.print("Sending Help to: " + userFull);
            //send help to user via PM
            objUser.openPrivateChannel().queue((channel) ->
            {
                int i = 20;
                //Clear PM history
                for (Message message : channel.getIterableHistory().cache(false)) {
                    message.delete().queue();
                    if (--i <= 0) return;
                }
                channel.sendMessage(Ref.cmds).queue();
            });
            objMsg.delete().queue();
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
            //Retrieved server list
            String Serverlist = "";
            for (Guild g : evt.getJDA().getGuilds()) {
                Serverlist += g.getName() + " (" + g.getId() + ") \n";
            }
            //add Fields
            seb.addField("Server List:", Serverlist, false);
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
        if (objMsg.getContentRaw().toLowerCase().contains(prefix + "clear")) {
            String samount = objMsg.getContentRaw().replace(prefix + "clear ", "");
            //Test if amount is Integer
            if ((Ref.isInteger(samount))) {
                int i = Integer.parseInt(samount) + 1;
                if (i <= 100)
                {
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


//DEV ONLY COMMANDS
    //UPDATE SOON
        if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "updatesoon"))
        {
            if (objUser.getId().equals("167336416861224961")) {
                jda.getPresence().setGame(Game.playing("UPDATE ROLLING OUT"));
            }
            else
            {
                System.out.print("Dev Command from Non-Dev \n");
            }
        }
    //UPDATE CANCEL
        if (objMsg.getContentRaw().equalsIgnoreCase(prefix + "updatecan"))
        {
            if (objUser.getId().equals("167336416861224961")) {
                jda.getPresence().setGame(Game.playing("Prefix: " + prefix + " | use " + prefix + "help | servers: " + jda.getGuilds().size()));
            }
            else
            {
                System.out.print("Dev Command from Non-Dev \n");
            }
        }
//Delete Message
        if (objMsg.getContentRaw().contains(prefix) || objMsg.getContentRaw().contains(jda.getSelfUser().getAsMention())) {
            objMsg.delete().queue();
        }

    }


}


