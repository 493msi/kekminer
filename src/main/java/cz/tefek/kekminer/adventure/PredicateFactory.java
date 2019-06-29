package cz.tefek.kekminer.adventure;

import java.util.function.Predicate;

import cz.tefek.kekminer.bot.command.CommandContext;

public class PredicateFactory
{
    public static <T> Predicate<T> alwaysTrue()
    {
        return t -> true;
    }

    public static <T> Predicate<T> alwaysFalse()
    {
        return t -> false;
    }

    public static Predicate<CommandContext> randomChance(double chance)
    {
        return ctx -> ctx.random.nextDouble() < chance;
    }
}
