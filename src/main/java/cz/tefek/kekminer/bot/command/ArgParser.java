package cz.tefek.kekminer.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ArgParser
{
    private static String codepointSubstring(String input, int index, int length)
    {
        return input.substring(input.offsetByCodePoints(0, index), input.offsetByCodePoints(0, index + length));
    }

    public static List<String[]> splitToArgs(String source, int[] allowedOperandCounts)
    {
        if (allowedOperandCounts == null)
        {
            return null;
        }

        int minOperands = Arrays.stream(allowedOperandCounts).min().getAsInt();

        if (minOperands < 0)
        {
            return null;
        }

        var output = new ArrayList<String[]>();

        if (source == null || source.isBlank())
        {
            return output;
        }

        source = source.strip();

        boolean quote = false;
        boolean openParam = false;
        int operandStart = 0;
        int position = 0;
        int operandNum = 0;

        var argList = new ArrayList<String>();
        var extended = new HashMap<Integer, String>();
        var codepointCount = source.codePointCount(0, source.length());

        while (true)
        {
            if (position == codepointCount)
            {
                break;
            }

            int codepoint = source.codePointAt(position);

            if (Character.isWhitespace(codepoint))
            {
                if (!openParam)
                {
                    operandStart++;
                }
                else
                {
                    var lastQuotePos = source.lastIndexOf('"');
                    var pastLastQuote = lastQuotePos < position;

                    if (!quote || pastLastQuote)
                    {
                        var arg = codepointSubstring(source, operandStart, position - operandStart);
                        argList.add(arg);
                        operandNum++;
                        openParam = false;
                    }
                }
            }
            else if (codepoint == '"')
            {
                if (!openParam)
                {
                    quote = true;
                    openParam = true;
                    operandStart = position;

                    extended.put(operandNum, codepointSubstring(source, position, codepointCount - position));
                }
                else
                {
                    if (quote)
                    {
                        var endParam = false;

                        if (position + 1 < codepointCount)
                        {
                            var nextCp = source.codePointAt(position + 1);

                            if (Character.isWhitespace(nextCp))
                            {
                                endParam = true;
                            }
                        }
                        else
                        {
                            endParam = true;
                        }

                        if (endParam)
                        {
                            var arg = codepointSubstring(source, operandStart + 1, position - operandStart - 1);
                            argList.add(arg);
                            operandNum++;
                            openParam = false;
                            quote = false;
                        }
                    }
                }
            }
            else
            {
                if (!openParam)
                {
                    quote = false;
                    openParam = true;
                    operandStart = position;

                    extended.put(operandNum, codepointSubstring(source, position, codepointCount - position));
                }

                if (position + 1 == codepointCount)
                {
                    var arg = codepointSubstring(source, operandStart, position - operandStart + 1);
                    argList.add(arg);
                    operandNum++;
                    openParam = false;
                    quote = false;
                }
            }

            position++;
        }

        var argCount = argList.size();

        if (argCount < minOperands)
        {
            return output;
        }

        for (int opCount : allowedOperandCounts)
        {
            if (argCount < opCount)
            {
                continue;
            }
            else if (argCount > opCount)
            {
                var sub = argList.subList(0, opCount - 1);

                var arr = sub.toArray(new String[opCount]);
                arr[opCount - 1] = extended.get(opCount - 1);

                output.add(arr);
            }
            else
            {
                output.add(argList.toArray(new String[argCount]));
            }
        }

        return output;
    }
}
