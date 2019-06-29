package cz.tefek.kekminer.bot.inventory;

public enum Item
{
    KEKTOKEN("<:kektoken:593127776714752010>", "KekToken");

    private String icon;
    private String name;

    Item(String icon, String name)
    {
        this.icon = icon;
        this.name = name;
    }

    public String getIcon()
    {
        return this.icon;
    }

    public String getName()
    {
        return this.name;
    }
}
