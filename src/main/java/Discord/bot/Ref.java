package Discord.bot;

import java.awt.*;

public class Ref
{
        //Config Variables
        public static final String token = "NDMyOTg1NTAwMTczODYwODg0.Dgk3zg.nfQ28nR2zmWaFKBQg53MSwTQL0A";
        public static final String prefix = "?"; //cannot be @,< or >
        public static final Color userCL = Color.ORANGE;
        public static final Color botCL = Color.BLUE;

        public static final String cmds = "``` Bot Commands and parameter with description '{}' is optional parameters and '()' is required (leave out the brackets in both cases). Remember to add the prefix if you forgot\n" +
                prefix +"Help - Duh, the one you are watching. \n" +
                prefix +"Userinfo @{User} - Returns info of user using command or if another user is mentioned it will give info about user \n" +
                prefix +"Clear (amount) - clears the specified amount of message history from the current channel```";

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

}
