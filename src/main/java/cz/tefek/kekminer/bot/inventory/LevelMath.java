package cz.tefek.kekminer.bot.inventory;

public class LevelMath
{
    public static long getXPRequiredToAdvance(long levelNow, long levelFuture)
    {
        return getCumulativeXPForLevel(levelFuture) - getCumulativeXPForLevel(levelNow);
    }

    public static long getCumulativeXPForLevel(long level)
    {
        return (1000L + (level - 1) * 25L) * (level - 1);
    }
}
