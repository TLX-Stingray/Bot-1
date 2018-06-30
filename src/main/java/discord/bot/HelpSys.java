package discord.bot;

public class HelpSys {
    public static String buildGeneralHelp()
    {
        String HelpString = "";
        HelpString  = "__**Command List for " + App.jda.getSelfUser().getName() + ":**__ \n";
        HelpString += "Make sure to join the support server for updates and help --> https://discord.gg/XEbBn6a \n";
        HelpString += "All Commands work with the bot Prefix (default is `{}`) \n";
        HelpString += "Only some commands work by mentioning the bot, those commands are marked with :lips: \n";
        HelpString += " \n";
        HelpString += "**Userinfo @[user]** - Shows info about mentioned user, if no-one is mentioned the bot will return info about the sender. \n";
        HelpString += "**Botinfo** - (:lips:) Shows info about the bot, maybe you want to know it... \n";
        HelpString += "**Serverinfo** - (:lips:) Shows info about the current server, it's only public info, don't worry \n";
        HelpString += "**Channelinfo** - (:lips:) Shows info about the current Textchannel, I think you get the idea \n";
        HelpString += "**Clear [amount]** - clears the specified amount of messages, you HAVE to specify a valid integer \n";
        HelpString += "**Message [message]** - forwards your message on to the main bot developer, don't spam or get banned from this service \n";
        HelpString += "**Config** - will send you a different help text if you are an Administrator, alows you to change server specific properties of the bot \n";

        return HelpString;
    }

    public static String buildConfigHelp()
    {
        String ConfigHelp = "";
        ConfigHelp  = "__**Help for Config Command:**__ \n";
        ConfigHelp += "Config command looks like this `config <parameter> (value)` \n";
        ConfigHelp += "__Parameters__ \n";
        ConfigHelp += "Please note that square brackets should be your value for example: \n `config welcomechannel #[Textchannel]` will become `config welcomechannel #general` \n";
        ConfigHelp += " \n";
        ConfigHelp += "**create** - initializes the config system for your server and defaults all values to `false`, do this first \n";
        ConfigHelp += "**get** - get current config values for the current server \n";
        ConfigHelp += "**welcomemessage** - valid values are `true` and `false` , turns the Welcome Message service on/off \n";
        ConfigHelp += "**welcomechannel** - valid values are #[Textchannel], sets the desired channel for the welcome messages \n";
        ConfigHelp += "**Autorole** - valid values are `true` and `false`, autorole gives every new member a role on join \n";
        ConfigHelp += "**setAutorole** - valid values are Role names (NOT MENTIONS), sets the role to give to new members, searches for a role with a matching name \n";
        ConfigHelp += "**delcommands** - valid values are `true` and `false`, if this setting is true it will delete the messages users send to execute commands providing the bot has the `MESSAGE_MANAGE` permission \n";

        return ConfigHelp;
    }
}
