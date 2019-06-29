package cz.tefek.kekminer.config;

public class KekMinerConfig
{
    private String token;

    public KekMinerConfig(String botToken)
    {
        this.token = botToken;
    }

    public String getToken()
    {
        return this.token;
    }
}
