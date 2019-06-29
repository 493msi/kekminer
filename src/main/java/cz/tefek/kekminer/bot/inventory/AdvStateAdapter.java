package cz.tefek.kekminer.bot.inventory;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import cz.tefek.kekminer.adventure.state.EnumState;

public class AdvStateAdapter extends TypeAdapter<EnumState>
{

    @Override
    public void write(JsonWriter out, EnumState value) throws IOException
    {
        out.value(value.name());
    }

    @Override
    public EnumState read(JsonReader in) throws IOException
    {
        return EnumState.valueOf(in.nextString().toUpperCase());
    }

}
