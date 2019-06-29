package cz.tefek.kekminer.bot.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import cz.tefek.kekminer.adventure.Adventure;
import cz.tefek.kekminer.adventure.state.EnumState;
import cz.tefek.kekminer.bot.command.CommandAssertException;

public class UserInventory
{
    private static transient final TypeAdapter<EnumState> stateAdapter = new AdvStateAdapter();
    private static transient final TypeAdapter<Item> itemAdapter = new ItemAdapter();

    private static transient final String saveFolder = "userdata/";

    private transient long userID;
    private Map<Item, Long> inventory = new HashMap<Item, Long>();

    private Adventure adventure;

    private transient boolean dirty = false;

    private UserInventory(long uid)
    {
        this.userID = uid;
    }

    public long getUserID()
    {
        return this.userID;
    }

    public static UserInventory load(long uid) throws Exception
    {
        var saveFolderDir = new File(saveFolder);

        if (!saveFolderDir.isDirectory())
        {
            saveFolderDir.mkdirs();
        }

        var saveFile = new File(saveFolderDir, "data_" + uid + ".json");

        if (!saveFile.isFile() && saveFile.exists())
        {
            throw new CommandAssertException("Your inventory failed to initialize, because the file is obstructed.\nPlease contact a developer.");
        }

        if (saveFile.isFile())
        {
            try (var fr = new FileReader(saveFile))
            {
                var gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(EnumState.class, stateAdapter);
                gsonBuilder.registerTypeAdapter(Item.class, itemAdapter);
                var gson = gsonBuilder.create();

                var ui = gson.fromJson(fr, UserInventory.class);
                ui.userID = uid;

                return ui;
            }
        }

        return new UserInventory(uid);
    }

    public void save() throws Exception
    {
        var saveFolderDir = new File(saveFolder);

        if (!saveFolderDir.isDirectory())
        {
            saveFolderDir.mkdirs();
        }

        var saveFile = new File(saveFolder, "data_" + this.userID + ".json");

        try (var fw = new FileWriter(saveFile))
        {
            var gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(EnumState.class, stateAdapter);
            gsonBuilder.registerTypeAdapter(Item.class, itemAdapter);
            var gson = gsonBuilder.create();

            gson.toJson(this, fw);
        }
    }

    protected void markDirty()
    {
        this.dirty = true;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void addItem(Item item)
    {
        this.inventory.compute(item, (key, val) -> (val == null) ? 1 : val + 1);
        this.markDirty();
    }

    public void addItem(Item item, final long amount)
    {
        this.inventory.compute(item, (key, val) -> (val == null) ? amount : val + amount);
        this.markDirty();
    }

    public void addItemStack(ItemStack itemStack)
    {
        this.addItem(itemStack.getItem(), itemStack.getAmount());
    }

    public long getItemAmount(Item item)
    {
        return this.inventory.getOrDefault(item, 0L);
    }

    public ItemStack getItemStack(Item item)
    {
        return new ItemStack(item, this.getItemAmount(item));
    }

    public List<ItemStack> getInventory()
    {
        return this.inventory.entrySet().stream().map(ItemStack::new).collect(Collectors.toUnmodifiableList());
    }

    public Adventure getAdventure()
    {
        return this.adventure;
    }

    public void setAdventure(Adventure adventure)
    {
        this.adventure = adventure;
        this.dirty = true;
    }

    public void checkDirtyChildren()
    {
        if (this.adventure != null)
        {
            this.dirty |= this.adventure.isDirty();
        }
    }
}
