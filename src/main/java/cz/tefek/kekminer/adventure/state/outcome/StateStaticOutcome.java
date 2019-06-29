package cz.tefek.kekminer.adventure.state.outcome;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.bot.command.CommandContext;

public class StateStaticOutcome implements IStateGenerator
{
    private EnumState output;

    public StateStaticOutcome(EnumState wentDown)
    {
        this.output = wentDown;
    }

    @Override
    public EnumState roll(CommandContext ctx)
    {
        return this.output;
    }
}
