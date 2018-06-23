package discord.bot;

import java.awt.*;

public class Ref
{
        //Config Variables
        public static final Color userCL = Color.ORANGE;
        public static final Color botCL = Color.BLUE;

        public static final String cmds = "``` Bot Commands and parameter with description '{}' is optional parameters and " +
                "'()' is required (leave out the brackets in both cases). " +
                "Remember to add the prefix if you forgot" +
                "(You can Also Mention the bot for commands) \n" +
                "Help - Duh, the one you are watching. \n" +
                "Userinfo @{User} - Returns info of user using command or if another user is mentioned it will give info about user \n" +
                "Botinfo - Shows info of bot. \n" +
                "Serverinfo - Shows info About the server \n" +
                "Channelinfo - shows info of the current text channel \n" +
                "Clear (amount) - clears the specified amount of message history from the current channel \n" +
                "Emptychannel - Delete all the accesible messages in the channel ```";

        public static boolean isInteger(String s) {
                try {
                        Integer.parseInt(s);
                } catch(NumberFormatException e) {
                        return false;
                } catch(NullPointerException e) {
                        return false;
                }
                // only got here if we didn't return false
                return true;
        }

        public static String getToken() {
                return System.getenv("DISCORD_TOKEN");
        }

        public static String getPrefix() {
                return System.getenv("DISCORD_PREFIX");
        }
}
