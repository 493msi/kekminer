package cz.tefek.kekminer.adventure.state.outcome;

import cz.tefek.kekminer.adventure.state.EnumState;

public class OutcomeRegistry
{
    public static IStateGenerator genericEncounter = ctx ->
    {
        if (ctx.random.nextDouble() < 0.08 - Math.min(ctx.ui.getAdventure().getLevel(), 100) / 2000.0)
        {
            return EnumState.NEXT_LEVEL;
        }

        if (ctx.random.nextDouble() < 0.20 - Math.min(ctx.ui.getAdventure().getLevel(), 50) / 1500.0)
        {
            return EnumState.SMALL_AMOUNT_OF_RESOURCES;
        }

        if (ctx.random.nextDouble() < 0.5 + Math.min(Math.max(ctx.ui.getAdventure().getLevel(), 20), 150) / 1000.0)
        {
            return EnumState.LARGE_AMOUNT_OF_RESOURCES;
        }

        return EnumState.LARGE_CAVE;
    };

    public static IStateGenerator attacked = new StateOutcomeSelectorWeighted() {
        /**
         * 
         */
        private static final long serialVersionUID = 650887165321894840L;

        {
            this.add(EnumState.DEAD, 20);
            this.add(EnumState.ESCAPED, 20);
        }
    };

    public static IStateGenerator genericContinue = ctx ->
    {
        if (ctx.ui.getAdventure().getLevel() == 0)
        {
            return EnumState.START;
        }
        else
        {
            return genericEncounter.roll(ctx);
        }
    };

    public static IStateGenerator goDown = ctx ->
    {
        return EnumState.WENT_DOWN;
    };

    public static IStateGenerator bigMine = new StateOutcomeSelectorWeighted() {
        /**
         * 
         */
        private static final long serialVersionUID = -6016977123976037752L;

        {
            this.add(EnumState.SMALL_AMOUNT_OF_RESOURCES, 50);
            this.add(EnumState.DEAD_FALLING_ROCKS, 10);
        }
    };
}
