package cz.tefek.kekminer.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import cz.tefek.kekminer.KekMinerMain;
import cz.tefek.kekminer.bot.inventory.UserInventory;
import cz.tefek.kekminer.util.LangUtil;

public class CommandParser
{
    private static final Map<Command, List<Method>> methodCache = new HashMap<>();
    private static final Map<Command, int[]> methodArgLengthCache = new HashMap<>();

    public static void parse(GuildMessageReceivedEvent event)
    {
        final var botMention = event.getJDA().getSelfUser().getAsMention();

        var message = event.getMessage();
        var messageContent = message.getContentRaw();

        if (!(messageContent.startsWith(botMention)))
        {
            return;
        }

        var commandRaw = messageContent.substring(botMention.length()).strip();

        var commandAlias = commandRaw.codePoints().takeWhile(ch -> !Character.isWhitespace(ch)).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        var command = CommandRegistry.find(commandAlias.strip());

        if (command == null)
        {
            return;
        }

        List<Method> methods = getCommandMethods(command);

        if (methods.size() == 0)
        {
            event.getChannel().sendMessage("*Sorry, this command is currently disabled.*").queue();
            return;
        }

        var argString = commandRaw.substring(commandAlias.length());

        int[] argLengths = methodArgLengthCache.get(command);

        if (argLengths == null)
        {
            argLengths = methods.stream().mapToInt(Method::getParameterCount).map(i -> i - 1).distinct().sorted().toArray();
            methodArgLengthCache.put(command, argLengths);
        }

        var argList = ArgParser.splitToArgs(argString, argLengths);

        Method toInvoke = null;
        String[] argsToUse = null;

        for (var method : methods)
        {
            var parCount = method.getParameterCount() - 1;

            if (parCount == 0 && argList.size() == 0)
            {
                toInvoke = method;
                argsToUse = new String[0];
                break;
            }

            for (var args : argList)
            {
                if (args.length == parCount)
                {
                    toInvoke = method;
                    argsToUse = args;
                    break;
                }
            }
        }

        if (toInvoke == null)
        {
            var help = generateHelp(event.getJDA(), commandAlias, command, Optional.of("Invalid parameters! Correct usage:"));
            event.getChannel().sendMessage(help).queue();

            return;
        }

        var uuid = UUID.randomUUID();

        var ctx = new CommandContext();
        ctx.jda = event.getJDA();
        ctx.bot = ctx.jda.getSelfUser();
        ctx.calledCommandRaw = commandAlias;
        ctx.caller = event.getAuthor();
        ctx.callerMember = event.getMember();
        ctx.command = command;
        ctx.guild = event.getGuild();
        ctx.message = event.getMessage();
        ctx.random = KekMinerMain.kekMinerInstance.getRandom();
        ctx.rdg = KekMinerMain.kekMinerInstance.getRandomDataGenerator();
        ctx.textChannel = event.getChannel();
        ctx.uuid = uuid;

        var invokeArgs = new Object[toInvoke.getParameterCount()];
        invokeArgs[0] = ctx;

        var index = 0;

        var methodTypes = toInvoke.getParameterTypes();

        for (int i = 1; i < invokeArgs.length; i++, index++)
        {
            if (methodTypes[i] == String.class)
            {
                invokeArgs[i] = argsToUse[index];
            }
            else if (methodTypes[i] == int.class || methodTypes[i] == Integer.class)
            {
                try
                {
                    invokeArgs[i] = Integer.parseInt(argsToUse[index]);
                }
                catch (NumberFormatException e)
                {
                    var errorMessage = String.format("Error! The %s argument must be a valid number! Correct usage:", LangUtil.integerToOrdinal(index));
                    var help = generateHelp(event.getJDA(), commandAlias, command, Optional.of(errorMessage));
                    ctx.quickRespond(help);
                    return;
                }
            }
            else if (methodTypes[i] == long.class || methodTypes[i] == Long.class)
            {
                try
                {
                    invokeArgs[i] = Long.parseLong(argsToUse[index]);
                }
                catch (NumberFormatException e)
                {
                    var errorMessage = String.format("Error! The %s argument must be a valid number! Correct usage:", LangUtil.integerToOrdinal(index));
                    var help = generateHelp(event.getJDA(), commandAlias, command, Optional.of(errorMessage));
                    ctx.quickRespond(help);
                    return;
                }
            }
        }

        try
        {
            ctx.ui = UserInventory.load(ctx.caller.getIdLong());
        }
        catch (CommandAssertException e)
        {
            ctx.quickRespond("***An error has occured while executing this command.***\n*" + e.whatHappened() + "*\nError ID: " + uuid);
            System.err.println("[ERROR ID " + uuid + "]");
            e.printStackTrace();
            return;
        }
        catch (Exception e)
        {
            ctx.quickRespond("***An error has occured while executing this command.***\n*If this problem persists, please contact the developer.*\nError ID: " + uuid);
            System.err.println("[ERROR ID " + uuid + "]");
            e.printStackTrace();
            return;
        }

        try
        {
            toInvoke.invoke(null, invokeArgs);
        }
        catch (InvocationTargetException e)
        {
            var tgt = e.getTargetException();

            if (tgt instanceof CommandAssertException)
            {
                ctx.quickRespond(((CommandAssertException) tgt).whatHappened());
            }
            else
            {
                ctx.quickRespond("***An error has occured while executing this command.***\n*If this problem persists, please contact the developer.*\nError ID: " + uuid);
                System.err.println("[ERROR ID " + uuid + "]");
                e.printStackTrace();
                return;
            }
        }
        catch (Exception e)
        {
            ctx.quickRespond("***An error has occured while executing this command.***\n*If this problem persists, please contact the developer.*Error ID: " + uuid);
            System.err.println("[ERROR ID " + uuid + "]");
            e.printStackTrace();
            return;
        }

        ctx.ui.checkDirtyChildren();

        if (ctx.ui.isDirty())
        {
            try
            {
                ctx.ui.save();
            }
            catch (CommandAssertException e)
            {
                ctx.quickRespond("***An error has occured while executing this command.***\n*" + e.whatHappened() + "*\nError ID: " + uuid);
                System.err.println("[ERROR ID " + uuid + "]");
                e.printStackTrace();
                return;
            }
            catch (Exception e)
            {
                ctx.quickRespond("***An error has occured while executing this command.***\n*If this problem persists, please contact the developer.*\nError ID: " + uuid);
                System.err.println("[ERROR ID " + uuid + "]");
                e.printStackTrace();
                return;
            }
        }
    }

    public static String generateHelp(JDA jda, final String usedAlias, Command command, Optional<String> customMessage)
    {
        if (command == null)
        {
            return "**Error:** Unknown command!";
        }

        var selfPing = jda.getSelfUser().getAsTag();
        var methods = getCommandMethods(command);

        StringBuilder sb = new StringBuilder();
        sb.append("**");
        sb.append(customMessage.isPresent() ? customMessage.get() : "Usage:");
        sb.append("**");
        sb.append("```html\n");
        methods.stream().map(Method::getParameters).forEach(paramArr ->
        {
            sb.append("@");
            sb.append(selfPing);
            sb.append(' ');
            sb.append(usedAlias);
            Arrays.stream(paramArr).skip(1).map(Parameter::getName).forEachOrdered(par ->
            {
                sb.append(" <");
                sb.append(par);
                sb.append('>');
            });
            sb.append("\n");
        });

        sb.append("```");

        return sb.toString();
    }

    private static List<Method> getCommandMethods(Command command)
    {
        List<Method> suitableMethods = methodCache.get(command);

        if (suitableMethods == null)
        {
            var clazz = CommandRegistry.getAccordingClass(command);

            var allMethods = clazz.getDeclaredMethods();

            suitableMethods = new ArrayList<>(allMethods.length);

            for (var method : allMethods)
            {
                if (!Modifier.isStatic(method.getModifiers()))
                {
                    continue;
                }

                var params = method.getParameters();
                if (params.length < 1)
                {
                    continue;
                }

                if (params[0].getType() != CommandContext.class)
                {
                    continue;
                }

                suitableMethods.add(method);
            }

            Collections.sort(suitableMethods, Comparator.comparingInt(Method::getParameterCount).reversed());
            methodCache.put(command, suitableMethods);
        }

        return suitableMethods;
    }
}
