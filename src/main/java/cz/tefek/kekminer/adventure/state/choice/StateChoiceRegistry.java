package cz.tefek.kekminer.adventure.state.choice;

import java.util.Optional;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.adventure.state.outcome.OutcomeRegistry;

public class StateChoiceRegistry
{
    public static final StateChoice acceptDead = new StateChoice("You are out of lives...", StateChoice.EXIT, ctx -> EnumState.CHECKOUT, Optional.of(ctx ->
    {
        return ctx.ui.getAdventure().getLives() == 0;
    }));

    public static final StateChoice acceptRespawn = new StateChoice("Continue...", StateChoice.TICK, OutcomeRegistry.genericEncounter, Optional.of(ctx ->
    {
        return ctx.ui.getAdventure().getLives() > 0;
    }));

    public static final StateChoice acceptExit = new StateChoice("Continue...", StateChoice.TICK, EnumState.START);

    public static final StateChoice genericContinue = new StateChoice("Continue...", StateChoice.TICK, OutcomeRegistry.genericContinue);

    public static final StateChoice mine = new StateChoice("Mine some resources...", StateChoice.TICK, OutcomeRegistry.genericContinue);

    public static final StateChoice mineBig = new StateChoice("Mine many resources...", StateChoice.MONEY, OutcomeRegistry.bigMine);

    public static final StateChoice goDown = new StateChoice("Descend deeper...", StateChoice.DOWN, EnumState.WENT_DOWN);

    public static final StateChoice goLeft = new StateChoice("Go left...", StateChoice.LEFT, OutcomeRegistry.genericEncounter);

    public static final StateChoice goForward = new StateChoice("Go forward...", StateChoice.UP, OutcomeRegistry.genericEncounter);

    public static final StateChoice goRight = new StateChoice("Go right...", StateChoice.RIGHT, OutcomeRegistry.genericEncounter);

    public static final StateChoice leave = new StateChoice("Leave the mine...", StateChoice.EXIT, EnumState.CHECKOUT);
}
