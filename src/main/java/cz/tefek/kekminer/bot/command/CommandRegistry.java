package cz.tefek.kekminer.bot.command;

import java.util.HashMap;

import cz.tefek.kekminer.bot.command.impl.CommandEcho;
import cz.tefek.kekminer.bot.command.impl.CommandInventory;
import cz.tefek.kekminer.bot.command.impl.CommandLoot;
import cz.tefek.kekminer.bot.command.impl.CommandPlay;

public class CommandRegistry
{
    private static final HashMap<String, Command> aliasesMap = new HashMap<>();

    private static final HashMap<Command, Class<?>> classes = new HashMap<>();

    public static void register(Class<?> clazz)
    {
        var cmd = clazz.getDeclaredAnnotation(Command.class);

        registerAliases(cmd, cmd.name(), cmd.aliases());

        classes.put(cmd, clazz);

        System.out.println("Registered the command " + cmd);
    }

    private static void registerAliases(Command command, String main, String... aliases)
    {
        if (aliasesMap.put(main, command) != null)
        {
            System.err.println("ERROR: Name conflict: " + main);
        }

        for (var alias : aliases)
        {
            if (aliasesMap.put(alias, command) != null)
            {
                System.err.println("WARNING: Alias conflict: " + alias);
            }
        }
    }

    public static Class<?> getAccordingClass(Command cmd)
    {
        return classes.get(cmd);
    }

    public static Command find(String cmdName)
    {
        return aliasesMap.get(cmdName.toLowerCase());
    }

    public static void addStatically()
    {
        register(CommandEcho.class);
        register(CommandInventory.class);
        register(CommandPlay.class);
        register(CommandLoot.class);
    }
}
