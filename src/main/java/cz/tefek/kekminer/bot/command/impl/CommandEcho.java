package cz.tefek.kekminer.bot.command.impl;

import cz.tefek.kekminer.bot.command.Command;
import cz.tefek.kekminer.bot.command.CommandContext;

@Command(name = "echo", description = "The echo command, mirrors what you write.")
public class CommandEcho
{
    public static void invoke(CommandContext ctx, String text)
    {
        ctx.quickRespondSanitized(text);
    }
}
