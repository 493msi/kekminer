package cz.tefek.kekminer.bot;

import java.util.Random;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import com.google.gson.Gson;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import cz.tefek.kekminer.config.KekMinerConfig;
import cz.tefek.kekminer.config.MissingConfigException;

public class KekMiner
{
    private static final String configFolderPath = "config/";

    private static final String mainConfigFileName = "settings.json";

    private KekMinerConfig config;

    private MainListener listener;

    private ShardManager shardManager;

    private RandomDataGenerator rdg;

    private Random random;

    public KekMiner() throws Exception
    {
        this.random = new Random();
        this.rdg = new RandomDataGenerator();

        var configFolder = new File(configFolderPath);

        if (!configFolder.isDirectory())
        {
            System.out.println("The config folder is missing, recreating...");
            configFolder.mkdirs();
        }

        var configFile = new File(configFolderPath, mainConfigFileName);

        var gson = new Gson();

        if (!configFile.exists())
        {
            System.err.println("The config file is missing, creating it with default config...");
            System.err.println("Please fill in the required parameters once the application quits and rerun it...");

            var fakeConfig = new KekMinerConfig("<insert your token here>");

            try (var fos = new PrintWriter(configFile))
            {
                gson.toJson(fakeConfig, fos);

                throw new MissingConfigException("Main configuration file: " + configFile.getAbsolutePath());
            }
        }

        try (var fr = new FileReader(configFile))
        {
            this.config = gson.fromJson(fr, KekMinerConfig.class);
        }

        if (this.config.getToken() == null)
        {
            throw new MissingConfigException("bot token in " + configFile.getAbsolutePath());
        }
    }

    public void start() throws Exception
    {
        var dsmb = new DefaultShardManagerBuilder();
        dsmb.setToken(this.config.getToken());
        this.listener = new MainListener();
        dsmb.addEventListeners(this.listener);
        this.shardManager = dsmb.build();
    }

    public ShardManager getShardManager()
    {
        return this.shardManager;
    }

    public KekMinerConfig getConfig()
    {
        return this.config;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public RandomDataGenerator getRandomDataGenerator()
    {
        return this.rdg;
    }
}
