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
        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " help")) {
            System.out.print("Sending Help to: " + userFull);
            objUser.openPrivateChannel().queue((channel) ->
            {
                int i = 20;
                for (Message message : channel.getIterableHistory().cache(false)) {
                    message.delete().queue();
                    if (--i <= 0) return;
                }
                channel.sendMessage(Ref.cmds).queue();
            });
            objMsg.delete().queue();
        }
        if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " userinfo"))
        {
            objUser.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + Ref.prefix + "userinfo {user as mention} `").queue();
            });
        }
        if (objMsg.getContentRaw().contains(Ref.prefix + "userinfo")) {
            String UserID = objUser.getId();
            Member newMemb = guild.getMemberById(UserID);
            User newUser = objUser;
            String username = objUser.getName();
            String msgNoCmd = lowerMsg.replace(Ref.prefix + "userinfo", "");
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

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("User Info of " + newUser.getName());
            eb.setColor(Ref.userCL);
            if (newUser.isBot()) {
                eb.setTitle("Bot Info of " + newUser.getName());
                eb.setColor(Ref.botCL);
                eb.addField("THIS USER IS A BOT", "", false);
            }
            eb.setImage(newUser.getAvatarUrl());
            eb.addField("Display Name: ", newMemb.getEffectiveName(), false);
            eb.addField("ID:", newUser.getId(), false);
            eb.addField("Joined on", newMemb.getJoinDate().toLocalDate().toString(), false);
            eb.addField("Online State:", newMemb.getOnlineStatus().toString(), false);

            objMsgCh.sendMessage(eb.build()).queue();
        }

        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "botinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " botinfo")) {
            EmbedBuilder seb = new EmbedBuilder();
            seb.setTitle("Server info of: " + jda.getSelfUser().getName() + " running on " + jda.getGuilds().size() + " servers");
            String Serverlist = "";
            for (Guild g : evt.getJDA().getGuilds()) {
                Serverlist += g.getName() + " (" + g.getId() + ") \n";
            }
            seb.addField("Server List:", Serverlist, false);
            seb.setColor(Color.RED);
            objMsgCh.sendMessage(seb.build()).queue();
        }

        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "channelinfo") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " channelinfo"))
        {
            EmbedBuilder ceb = new EmbedBuilder();
            ceb.setTitle("Channel info of " + objMsgCh.getName());
            ceb.setColor(Color.RED);
            ceb.addField("Channel ID: ", objMsgCh.getId(), false);
            ceb.addField("History size: ", Integer.toString(objMsgCh.getIterableHistory().complete().size() - 1), false);
            ceb.addField("Created on: ", objMsgCh.getCreationTime().toLocalDate().toString(), false);


            objMsgCh.sendMessage(ceb.build()).queue();
        }

        if (objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "emptychannel") || objMsg.getContentRaw().equalsIgnoreCase(jda.getSelfUser().getAsMention() + " emptychannel"))
        {
            int i = objMsgCh.getIterableHistory().complete().size();;
            for (Message message : objMsgCh.getIterableHistory().cache(false)) {
                message.delete().queue();
                if (--i <= 0) return;
            }
        }
        if (objMsg.getContentRaw().toLowerCase().contains(jda.getSelfUser().getAsMention() + " clear"))
        {
            objUser.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage("Please use the command with the prefix, this command does not support mentioning example: `" + Ref.prefix + "clear (amount) `").queue();
            });
        }
        if (objMsg.getContentRaw().toLowerCase().contains(Ref.prefix + "clear")) {
            String samount = objMsg.getContentRaw().replace(Ref.prefix + "clear ", "");
            if ((Ref.isInteger(samount))) {
                int i = Integer.parseInt(samount) + 1;
                for (Message message : objMsgCh.getIterableHistory().cache(false)) {
                    message.delete().queue();
                    if (--i <= 0) return;
                }
            } else {
                objUser.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("You must make sure you use the clear command correctly '" + Ref.prefix
                            + "clear {amount)', if the format is correct you should make sure you used a valid integer").queue();
                });
            }

        }

        if (objMsg.getContentRaw().contains(Ref.prefix) || objMsg.getContentRaw().contains(jda.getSelfUser().getAsMention())) {
            objMsg.delete().queue();
        }

    }


}


