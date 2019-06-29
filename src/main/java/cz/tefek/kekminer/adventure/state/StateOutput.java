package cz.tefek.kekminer.adventure.state;

import java.awt.image.BufferedImage;

public class StateOutput
{
    private String title;
    private String text;
    private BufferedImage image;

    public StateOutput(String title, String text, BufferedImage image)
    {
        this.title = title;
        this.text = text;
        this.image = image;
    }

    public BufferedImage getImage()
    {
        return this.image;
    }

    public String getText()
    {
        return this.text;
    }

    public String getTitle()
    {
        return this.title;
    }
}
