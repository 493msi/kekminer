package cz.tefek.kekminer.adventure.state.outcome;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.bot.command.CommandContext;

public class StateOutcomeOneOf implements IStateGenerator
{
    private EnumState[] states;

    public StateOutcomeOneOf(EnumState... states)
    {
        if (states.length < 1)
        {
            throw new IllegalArgumentException("At least one argument is needed.");
        }

        this.states = states;
    }

    @Override
    public EnumState roll(CommandContext ctx)
    {
        return this.states[ctx.random.nextInt(this.states.length - 1)];
    }
}
