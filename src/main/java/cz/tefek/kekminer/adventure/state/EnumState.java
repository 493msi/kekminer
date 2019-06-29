package cz.tefek.kekminer.adventure.state;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.tefek.kekminer.adventure.state.choice.StateChoice;
import cz.tefek.kekminer.adventure.state.choice.StateChoiceRegistry;
import cz.tefek.kekminer.bot.command.CommandContext;
import cz.tefek.kekminer.util.LangUtil;

public enum EnumState
{
    FALLBACK_STATE
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.genericContinue).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("What?", "You find yourself in a weird state, this definitely should not happen. It might be a bug, who knows?", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            return new StateOutput("What?", "You find yourself in a weird state, this definitely should not happen. It might be a bug, who knows?", null);
        }
    },
    START
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.goDown).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            adv.setLevel(0);
            adv.resetLives();

            return new StateOutput("The Beginning", "You are about to descend down into the dark depths of the caves...", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            return new StateOutput("The Beginning", "You are about to descend down into the dark depths of the caves...", null);
        }
    },
    CHECKOUT
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.genericContinue).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            var msg = adv.getLives() == 0 ? "Your journey has ended..."
                    : "You left the caves and returned to the surface.";
            adv.setLevel(0);
            adv.resetLives();
            adv.clearLoot();

            return new StateOutput("Summary", msg, null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            return new StateOutput("Summary", "Your journey has ended, click continue to start again.", null);
        }
    },
    WENT_DOWN
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.leave, StateChoiceRegistry.goForward).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            adv.increaseLevel();
            return new StateOutput("Into the depths", String.format("You decided to go deeper, advancing to the %s level.", LangUtil.integerToOrdinal(adv.getLevel())), null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), String.format("You are currently at the entrance of the %s level.", LangUtil.integerToOrdinal(adv.getLevel())), null);
        }
    },
    LARGE_CAVE
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.goLeft, StateChoiceRegistry.goForward, StateChoiceRegistry.goRight).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("Large cave", "You are in the middle of a large cave.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You are in the middle of a large cave.", null);
        }
    },
    SMALL_AMOUNT_OF_RESOURCES
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.mine, StateChoiceRegistry.goLeft, StateChoiceRegistry.goForward, StateChoiceRegistry.goRight).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("Small amont of resources", "You found a small chunk of some ore.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You found a small chunk of some ore.", null);
        }
    },
    LARGE_AMOUNT_OF_RESOURCES
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.mineBig, StateChoiceRegistry.mine, StateChoiceRegistry.goLeft, StateChoiceRegistry.goForward, StateChoiceRegistry.goRight).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("Many resources", "You found a large amount of valuable resources.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You found a large amount of valuable resources.", null);
        }
    },
    ESCAPED
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.goLeft, StateChoiceRegistry.goForward, StateChoiceRegistry.goRight).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("You escaped", "You just barely managed to escape the danger.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You just barely managed to escape the danger.", null);
        }
    },
    DEAD
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.acceptDead, StateChoiceRegistry.acceptRespawn).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            adv.loseLife();
            return new StateOutput("You died", "You are dead, you have " + adv.getLives() + " lives left.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You are dead, you have " + adv.getLives() + " lives left.", null);
        }
    },
    DEAD_FALLING_ROCKS
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.acceptDead, StateChoiceRegistry.acceptRespawn).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            adv.loseLife();
            return new StateOutput("You died", "A large rock has fallen onto you and you died, you have " + adv.getLives() + " lives left.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You are dead, you have " + adv.getLives() + " lives left.", null);
        }
    },
    NEXT_LEVEL
    {
        @Override
        public List<StateChoice> getChoice(CommandContext ctx)
        {
            return Arrays.asList(StateChoiceRegistry.leave, StateChoiceRegistry.goDown).stream().filter(c -> c.doesAppear(ctx)).collect(Collectors.toList());
        }

        @Override
        public StateOutput trigger(CommandContext ctx)
        {
            return new StateOutput("Next level entrance", "You found the entrance to the next level. You can either leave or continue your adventure.", null);
        }

        @Override
        public StateOutput getOutput(CommandContext ctx)
        {
            var adv = ctx.ui.getAdventure();
            return new StateOutput("Level " + adv.getLevel(), "You found the entrance to the next level. You can either leave or continue your adventure.", null);
        }
    };

    public abstract List<StateChoice> getChoice(CommandContext ctx);

    public abstract StateOutput trigger(CommandContext ctx);

    public abstract StateOutput getOutput(CommandContext ctx);
}
