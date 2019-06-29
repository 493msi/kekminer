package cz.tefek.kekminer.config;

public class MissingConfigException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -6362980746014018635L;

    public MissingConfigException(String configName)
    {
        super("Missing config: " + configName);
    }
}
