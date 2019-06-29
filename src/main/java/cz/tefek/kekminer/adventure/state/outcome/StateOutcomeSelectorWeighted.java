package cz.tefek.kekminer.adventure.state.outcome;

import java.util.ArrayList;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.adventure.state.outcome.StateOutcomeSelectorWeighted.WeightPair;
import cz.tefek.kekminer.bot.command.CommandContext;

public class StateOutcomeSelectorWeighted extends ArrayList<WeightPair> implements IStateGenerator
{
    /**
     * 
     */
    private static final long serialVersionUID = 4790638962774118150L;

    private static final transient int DEFAULT_WEIGHT = 100;

    public void add(EnumState state, int weight)
    {
        this.add(new WeightPair(state, weight));
    }

    public void add(EnumState state)
    {
        this.add(new WeightPair(state, DEFAULT_WEIGHT));
    }

    @Override
    public EnumState roll(CommandContext ctx)
    {
        if (this.isEmpty())
        {
            return null;
        }

        var sumWeights = this.stream().mapToInt(WeightPair::getWeight).sum();
        var currentWeight = 0;
        var rolledWeight = ctx.random.nextInt(sumWeights);
        int i = 0;
        var currentItem = this.get(i);

        while (currentWeight < rolledWeight)
        {
            currentItem = this.get(++i);
            currentWeight += currentItem.weight;
        }

        return currentItem.getState();
    }

    public static final class WeightPair
    {
        private EnumState state;
        private int weight;

        public WeightPair(EnumState state, int weight)
        {
            this.state = state;
            this.weight = weight;
        }

        public EnumState getState()
        {
            return this.state;
        }

        public int getWeight()
        {
            return this.weight;
        }
    }
}
