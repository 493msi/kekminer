package cz.tefek.kekminer.bot.command.impl;

import net.dv8tion.jda.api.EmbedBuilder;

import cz.tefek.kekminer.bot.command.Command;
import cz.tefek.kekminer.bot.command.CommandContext;

@Command(name = "inventory", aliases = { "i", "inv" }, description = "Shows your inventory.")
public class CommandInventory
{
    public static void invoke(CommandContext ctx)
    {
        var eb = new EmbedBuilder();
        eb.setTitle("Inventory");
        eb.setAuthor(ctx.caller.getAsTag());
        eb.setColor(0x008080);
        eb.setThumbnail(ctx.caller.getEffectiveAvatarUrl());

        var inv = ctx.ui.getInventory();

        inv.forEach(is ->
        {
            if (is.getAmount() < 0)
            {
                return;
            }

            eb.addField(is.getItem().getIcon() + " " + is.getItem().getName(), String.valueOf(is.getAmount()), true);
        });

        ctx.quickRespond(eb);
    }
}
