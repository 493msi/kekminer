package cz.tefek.kekminer.adventure;

import java.util.ArrayList;
import java.util.List;

import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.adventure.state.StateOutput;
import cz.tefek.kekminer.bot.command.CommandContext;
import cz.tefek.kekminer.bot.inventory.ItemStack;

public class Adventure
{
    private EnumState state;
    private List<ItemStack> loot;
    private int level;
    private int lives;

    private static final transient int DEFAULT_LIVES = 3;

    private transient boolean dirty = false;

    public Adventure()
    {
        this.lives = DEFAULT_LIVES;
        this.level = 0;
        this.loot = new ArrayList<>();
        this.state = EnumState.START;
    }

    public int increaseLevel()
    {
        this.dirty = true;
        return ++this.level;
    }

    public int getLevel()
    {
        return this.level;
    }

    public int getLives()
    {
        return this.lives;
    }

    public void loseLife()
    {
        this.lives--;
        this.dirty = true;
    }

    public void addLife()
    {
        this.lives++;
        this.dirty = true;
    }

    public void addItems(ItemStack is)
    {
        for (var stack : this.loot)
        {
            if (stack.getItem() == is.getItem())
            {
                stack.addAmount(is.getAmount());
                return;
            }
        }

        this.loot.add(is);
        this.dirty = true;
    }

    public StateOutput setState(CommandContext ctx, EnumState state)
    {
        this.state = state;
        this.dirty = true;
        return this.state.trigger(ctx);
    }

    public EnumState getState()
    {
        return this.state;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void resetLives()
    {
        this.lives = DEFAULT_LIVES;
    }

    public void clearLoot()
    {
        this.loot.clear();
    }

    public List<ItemStack> getLoot()
    {
        return this.loot;
    }
}
