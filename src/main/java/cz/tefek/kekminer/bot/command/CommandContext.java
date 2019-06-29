package cz.tefek.kekminer.bot.command;

import java.util.Random;
import java.util.UUID;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.apache.commons.math3.random.RandomDataGenerator;

import javax.annotation.Nullable;

import cz.tefek.kekminer.bot.inventory.UserInventory;

public class CommandContext
{
    public JDA jda;
    public SelfUser bot;
    @Nullable
    public String calledCommandRaw;
    @Nullable
    public Command command;
    public Guild guild;
    public TextChannel textChannel;
    public User caller;
    public Member callerMember;
    @Nullable
    public Message message;
    public Random random;
    public RandomDataGenerator rdg;
    public UUID uuid;
    @Nullable
    public ReactionEmote emote;
    public UserInventory ui;

    public void quickRespond(String message)
    {
        this.textChannel.sendMessage(message).queue();
    }

    public void quickRespondSanitized(String message)
    {
        this.textChannel.sendMessage(new MessageBuilder(message).stripMentions(this.guild).build()).queue();
    }

    public void quickRespond(EmbedBuilder embed)
    {
        this.textChannel.sendMessage(embed.build()).queue();
    }
}
