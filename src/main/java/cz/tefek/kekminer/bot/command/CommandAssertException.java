package cz.tefek.kekminer.bot.command;

public class CommandAssertException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 4256586754691739277L;

    private String failurePoint;

    public CommandAssertException(String what)
    {
        this.failurePoint = what;
    }

    public String whatHappened()
    {
        return this.failurePoint;
    }
}
