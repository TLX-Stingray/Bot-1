package Discord.bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class App extends ListenerAdapter {
    public static JDA jda;

    public static void main(String[] args) throws Exception {
        jda = new JDABuilder(AccountType.BOT).setToken(Ref.token).buildBlocking();
        jda.addEventListener(new App());
        jda.getPresence().setGame(Game.playing("Prefix: " + Ref.prefix + " | use " + Ref.prefix + "help servers: " + jda.getGuilds().size()));
        System.out.print("done Init \n");
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        //Objects
        User objUser = evt.getAuthor();
        MessageChannel objMsgCh = evt.getChannel();
        Message objMsg = evt.getMessage();
        String lowerMsg = objMsg.getContentRaw().toLowerCase();
        String userFull = objUser.getName() + "#" + objUser.getDiscriminator();
        Guild guild = evt.getGuild();
        if (objMsg.getContentRaw().contains(Ref.prefix) || objMsg.getContentRaw().contains(jda.getSelfUser().getAsMention())) {
            System.out.print("Received: " + objMsg.getContentRaw() + " on Server: " + guild.getName() + " (" + guild.getId() + ") \n");
        }

        //Bot-Prevention
        if (evt.getAuthor().isBot()) return;

        //Commands
        //HELP
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " help")) {
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
        if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " userinfo"))
        {
            objUser.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + Ref.prefix + "userinfo {user as mention} `").queue();
            });
        }
    //execute command
        if (objMsg.getContentRaw().contains(Ref.prefix + "userinfo")) {
            String UserID = objUser.getId();
            Member newMemb = guild.getMemberById(UserID);
            User newUser = objUser;
            String msgNoCmd = lowerMsg.replace(Ref.prefix + "userinfo", "");
            //Interpret Command for Different user specified
            if (!msgNoCmd.equalsIgnoreCase("")) {
                if (!msgNoCmd.contains("<@")) {
                    objUser.openPrivateChannel().queue((channel) ->
                    {
                        channel.sendMessage("You used my 'userinfo' command wrongly, make sure to mention the user you wish to check the info of in the message " +
                                "for example: " + Ref.prefix + "userinfo @User . Hope you understand :grinning:").queue();
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
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "botinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " botinfo")) {
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
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "channelinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " channelinfo"))
        {   //Init Embed
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
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "serverinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " serverinfo"))
        {
            //Init Embed
            EmbedBuilder seb = new EmbedBuilder();
            seb.setTitle("Info About the server named: " + guild.getName());
            seb.setColor(Color.RED);
            seb.setImage(guild.getIconUrl());
            //Add Fields
            seb.addField("Server ID:", guild.getId(), false);
            seb.addField("Member Amount:", Integer.toString(guild.getMembers().size()), false);
            seb.addField("Created On:" , guild.getCreationTime().toLocalDate().toString(), false);
            seb.addField("Owner:", guild.getOwner().getUser().getName() + "#" +guild.getOwner().getUser().getDiscriminator() +
                                " (" + guild.getOwner().getUser().getId() + ")",false);
            seb.addField("Text Channel Amount:", Integer.toString(guild.getTextChannels().size()), false);
            //Get Text Channels
            String txtChannelList = "";
            for (TextChannel txt : guild.getTextChannels())
            {
                txtChannelList += txt.getName() + " (" + txt.getId() + ") \n";
            }
            seb.addField("Text Channels:", txtChannelList, false);
            seb.addField("Voice Channel Amount:", Integer.toString(guild.getVoiceChannels().size()), false);
            //Get Voice Channels
            String vcChannelList = "";
            for (VoiceChannel vc : guild.getVoiceChannels())
            {
                vcChannelList += vc.getName() + " (" + vc.getId() + ") \n";
            }
            seb.addField("Voice Channels:", vcChannelList, false);
            //Build and Send Embed
            objMsgCh.sendMessage(seb.build()).queue();
        }
//EMPTYCHANNEL
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "emptychannel") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " emptychannel"))
        {
            //Get History Size
            int i = objMsgCh.getIterableHistory().complete().size();;
            //Delete messages based on history size
            for (Message message : objMsgCh.getIterableHistory().cache(false)) {
                message.delete().queue();
                if (--i <= 0) return;
            }
        }
//CLEAR
    //Check for Mention
        if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " clear"))
        {
            objUser.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + Ref.prefix + "clear (amount) `").queue();
            });
        }
    //Execute command
        if (objMsg.getContentRaw().toLowerCase().contains(Ref.prefix + "clear")) {
            String samount = objMsg.getContentRaw().replace(Ref.prefix + "clear ", "");
        //Test if amount is Integer
            if ((Ref.isInteger(samount))) {
                int i = Integer.parseInt(samount) + 1;
            //Delete Message History
                for (Message message : objMsgCh.getIterableHistory().cache(false)) {
                    message.delete().queue();
                    if (--i <= 0) return;
                }
            } else {
                objUser.openPrivateChannel().queue((channel) ->
                {   //Notify User via PM
                    channel.sendMessage("You must make sure you use the clear command correctly '" + Ref.prefix
                            + "clear {amount)', if the format is correct you should make sure you used a valid integer").queue();
                });
            }

        }
//Delete Message
        if (objMsg.getContentRaw().contains(Ref.prefix) || objMsg.getContentRaw().contains(jda.getSelfUser().getAsMention())) {
            objMsg.delete().queue();
        }

    }


}


