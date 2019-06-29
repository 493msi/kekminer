package cz.tefek.kekminer.bot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import cz.tefek.kekminer.KekMinerMain;
import cz.tefek.kekminer.adventure.MessageUserBinding;
import cz.tefek.kekminer.adventure.state.choice.StateChoice;
import cz.tefek.kekminer.bot.command.Command;
import cz.tefek.kekminer.bot.command.CommandContext;
import cz.tefek.kekminer.bot.command.CommandParser;
import cz.tefek.kekminer.bot.command.impl.CommandLoot;
import cz.tefek.kekminer.bot.inventory.UserInventory;

public class MainListener extends ListenerAdapter
{
    public Map<Long, Object> locks = new ConcurrentSkipListMap<>();

    @Override
    public void onReady(ReadyEvent event)
    {
        var shardInfo = event.getJDA().getShardInfo();
        System.out.println("Shard ready: " + shardInfo.getShardId());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        var author = event.getAuthor();

        if (author.isBot())
        {
            return;
        }

        var userID = author.getIdLong();
        this.locks.putIfAbsent(userID, new Object());

        synchronized (this.locks.get(userID))
        {
            CommandParser.parse(event);
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
        if (event.getUser().isBot())
        {
            return;
        }

        var messageID = event.getMessageIdLong();
        var textChannel = event.getChannel();
        var user = event.getUser();
        var userID = user.getIdLong();

        this.locks.putIfAbsent(userID, new Object());

        synchronized (this.locks.get(userID))
        {
            var boundMessageID = MessageUserBinding.get(userID);

            if (boundMessageID.isEmpty() || boundMessageID.get().longValue() != messageID)
            {
                return;
            }

            var uuid = UUID.randomUUID();

            try
            {
                var ui = UserInventory.load(userID);

                var adv = ui.getAdventure();

                if (adv == null)
                {
                    return;
                }

                var state = adv.getState();

                var ctx = new CommandContext();
                ctx.jda = event.getJDA();
                ctx.bot = ctx.jda.getSelfUser();
                // ctx.calledCommandRaw = commandAlias;
                ctx.caller = event.getUser();
                ctx.callerMember = event.getMember();
                // ctx.command = command;
                ctx.guild = event.getGuild();
                ctx.random = KekMinerMain.kekMinerInstance.getRandom();
                ctx.rdg = KekMinerMain.kekMinerInstance.getRandomDataGenerator();
                ctx.textChannel = event.getChannel();
                ctx.message = ctx.textChannel.retrieveMessageById(messageID).complete();
                ctx.uuid = uuid;
                ctx.ui = ui;
                ctx.emote = event.getReactionEmote();

                var choiceOld = state.getChoice(ctx);

                if (!ctx.emote.isEmoji())
                {
                    return;
                }

                var emoji = ctx.emote.getEmoji();

                for (var ch : choiceOld)
                {
                    if (emoji.equals(ch.getEmoji()))
                    {
                        var stateNew = ch.chooseNext(ctx);
                        var output = adv.setState(ctx, stateNew);

                        var choiceNew = stateNew.getChoice(ctx);
                        var choiceStr = choiceNew.stream().map(option -> String.format("%s **%s**", option.getEmoji(), option.getName())).collect(Collectors.joining("\n"));

                        var eb = new EmbedBuilder();
                        eb.setTitle(output.getTitle());
                        eb.setAuthor(ctx.caller.getAsTag(), null, ctx.caller.getEffectiveAvatarUrl());
                        eb.setThumbnail(ctx.bot.getEffectiveAvatarUrl());
                        eb.setColor(0x008080);
                        eb.addField("Options: ", choiceStr, false);
                        eb.setDescription(String.format("**Depth:** %d\n**Lives:** %d\n*%s*", adv.getLevel(), adv.getLives(), output.getText()));
                        eb.setFooter(String.format("*Use `@%s %s` to show your current loot. Navigate via the emoji on this message.*", ctx.bot.getAsTag(), CommandLoot.class.getDeclaredAnnotation(Command.class).name()));

                        ctx.textChannel.clearReactionsById(messageID).complete();

                        ctx.textChannel.sendMessage(eb.build()).submit().thenAcceptAsync(messageNew ->
                        {
                            MessageUserBinding.add(ctx.caller, messageNew);
                            choiceNew.stream().map(StateChoice::getEmoji).map(messageNew::addReaction).forEach(RestAction<Void>::queue);
                        });
                    }
                }

                ui.save();
            }
            catch (Exception e)
            {
                var mes = new MessageBuilder("***An error has occured while executing this command.***\n*If this problem persists, please contact the developer.*\nError ID: " + uuid).setEmbed(null).build();
                textChannel.editMessageById(messageID, mes).queue();
                System.err.println("[ERROR ID " + uuid + "]");
                e.printStackTrace();
                return;
            }
        }
    }
}
