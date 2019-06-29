package cz.tefek.kekminer.adventure.state.outcome;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.bot.command.CommandContext;

public interface IStateGenerator
{
    public EnumState roll(CommandContext ctx);
}
