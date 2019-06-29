package kekminer;

import cz.tefek.kekminer.bot.command.ArgParser;

public class ArgTest
{
    public static void main(String[] args)
    {
        var permutations = ArgParser.splitToArgs("wqho \"edwqoipjfdwe gfirewojoif\" weriofjewor", new int[] { 2,
                3 });

        permutations.forEach(perm ->
        {
            System.out.println(String.join(",", perm));
        });
    }
}
