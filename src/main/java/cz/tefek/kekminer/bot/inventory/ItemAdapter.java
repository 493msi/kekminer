package cz.tefek.kekminer.bot.inventory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ItemAdapter extends TypeAdapter<Item>
{

    @Override
    public void write(JsonWriter out, Item value) throws IOException
    {
        out.value(value.name());
    }

    @Override
    public Item read(JsonReader in) throws IOException
    {
        return Item.valueOf(in.nextString().toUpperCase());
    }

}
