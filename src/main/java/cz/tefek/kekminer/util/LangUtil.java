package cz.tefek.kekminer.util;

public class LangUtil
{
    public static String integerToOrdinal(int i)
    {
        switch (i)
        {
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            default:
                break;
        }

        switch (i % 100)
        {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                switch (i % 10)
                {
                    case 1:
                        return i + "st";
                    case 2:
                        return i + "nd";
                    case 3:
                        return i + "rd";
                    default:
                        return i + "th";
                }
        }
    }

}
