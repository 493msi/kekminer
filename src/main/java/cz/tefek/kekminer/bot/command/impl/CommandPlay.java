package cz.tefek.kekminer.bot.command.impl;

import java.util.stream.Collectors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.RestAction;

import cz.tefek.kekminer.adventure.Adventure;
import cz.tefek.kekminer.adventure.MessageUserBinding;
import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.adventure.state.choice.StateChoice;
import cz.tefek.kekminer.bot.command.Command;
import cz.tefek.kekminer.bot.command.CommandContext;

@Command(name = "play", aliases = { "adventure", "start", "continue" }, description = "Starts/continues the adventure.")
public class CommandPlay
{
    public static void invoke(CommandContext ctx)
    {
        if (ctx.ui.getAdventure() == null)
        {
            var adv = new Adventure();
            ctx.ui.setAdventure(adv);
            adv.setState(ctx, EnumState.START);
        }

        var adventure = ctx.ui.getAdventure();
        var state = adventure.getState();
        var output = state.getOutput(ctx);

        var choice = state.getChoice(ctx);
        var choiceStr = choice.stream().map(option -> String.format("%s **%s**", option.getEmoji(), option.getName())).collect(Collectors.joining("\n"));

        var eb = new EmbedBuilder();
        eb.setTitle(output.getTitle());
        eb.setAuthor(ctx.caller.getAsTag(), null, ctx.caller.getEffectiveAvatarUrl());
        eb.setThumbnail(ctx.bot.getEffectiveAvatarUrl());
        eb.setColor(0x008080);
        eb.addField("Options: ", choiceStr, false);
        eb.setDescription(String.format("**Depth:** %d\n**Lives:** %d\n*%s*", adventure.getLevel(), adventure.getLives(), output.getText()));
        eb.setFooter(String.format("*Use `@%s %s` to show your current loot. Navigate via the emoji on this message.*", ctx.bot.getAsTag(), CommandLoot.class.getDeclaredAnnotation(Command.class).name()));

        ctx.textChannel.sendMessage(eb.build()).submit().thenAcceptAsync(mess ->
        {
            MessageUserBinding.add(ctx.caller, mess);
            choice.stream().map(StateChoice::getEmoji).map(mess::addReaction).forEach(RestAction<Void>::queue);
        });
    }
}
