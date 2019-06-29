package cz.tefek.kekminer.adventure.state.choice;

import java.util.Optional;
import java.util.function.Predicate;

import cz.tefek.kekminer.adventure.PredicateFactory;
import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.adventure.state.outcome.IStateGenerator;
import cz.tefek.kekminer.adventure.state.outcome.StateStaticOutcome;
import cz.tefek.kekminer.bot.command.CommandContext;

public class StateChoice
{
    // Common emoji for reaction navigation
    public static transient final String LEFT = "◀";
    public static transient final String RIGHT = "▶";
    public static transient final String UP = "🔼";
    public static transient final String DOWN = "🔽";

    public static transient final String TICK = "✅";
    public static transient final String MONEY = "💲";

    public static transient final String EXIT = "🛑";

    private String name;
    private String emoji;
    private Predicate<CommandContext> appears;
    private IStateGenerator outcomeGenerator;

    public StateChoice(String name, String emoji, EnumState wentDown)
    {
        this(name, emoji, new StateStaticOutcome(wentDown));
    }

    public StateChoice(String name, String emoji, IStateGenerator outcomeGenerator)
    {
        this(name, emoji, outcomeGenerator, Optional.of(PredicateFactory.alwaysTrue()));
    }

    public StateChoice(String name, String emoji, IStateGenerator outcomeGenerator, Optional<Predicate<CommandContext>> appears)
    {
        this.name = name;
        this.emoji = emoji;
        this.outcomeGenerator = outcomeGenerator;
        this.appears = appears.isPresent() ? appears.get() : PredicateFactory.alwaysTrue();
    }

    public boolean doesAppear(CommandContext ctx)
    {
        return this.appears.test(ctx);
    }

    public String getName()
    {
        return this.name;
    }

    public String getEmoji()
    {
        return this.emoji;
    }

    public EnumState chooseNext(CommandContext ctx)
    {
        return this.outcomeGenerator.roll(ctx);
    }
}
