package cz.tefek.kekminer;

import cz.tefek.kekminer.bot.KekMiner;
import cz.tefek.kekminer.bot.command.CommandRegistry;

public class KekMinerMain
{
    public static KekMiner kekMinerInstance;

    public static void main(String[] args) throws ClassNotFoundException
    {
        CommandRegistry.addStatically();

        try
        {
            kekMinerInstance = new KekMiner();
            kekMinerInstance.start();
        }
        catch (Exception e)
        {
            System.err.println("ERROR: Could not initialize the bot!");
            e.printStackTrace();
            return;
        }
    }
}
