package cz.tefek.kekminer.bot.inventory;

import java.util.Map.Entry;

public class ItemStack
{
    private final Item item;
    private long amount;

    public ItemStack(Item item)
    {
        this(item, 1);
    }

    public ItemStack(Item item, long count)
    {
        this.item = item;
        this.amount = count;
    }

    ItemStack(Entry<Item, Long> itemEntry)
    {
        this(itemEntry.getKey(), itemEntry.getValue());
    }

    public Item getItem()
    {
        return this.item;
    }

    public long getAmount()
    {
        return this.amount;
    }

    public void addAmount(long amount)
    {
        this.amount += amount;
    }

    public void setAmount(long amount)
    {
        this.amount = amount;
    }

    public String str()
    {
        return String.format("%l %s %s", this.getAmount(), this.getItem().getIcon(), this.getItem().getName());
    }

    @Override
    public String toString()
    {
        return this.str();
    }
}
