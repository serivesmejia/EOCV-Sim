package com.github.serivesmejia.eocvsim.config;

import com.github.serivesmejia.eocvsim.util.Log;

import java.io.FileNotFoundException;

public class ConfigManager {

    ConfigLoader configLoader = new ConfigLoader();
    Config config;

    public Thread configUpdaterThread = new Thread(new ConfigUpdater());

    public void init() {

        Log.info("ConfigManager", "Initializing...");

        try {
            config = configLoader.loadFromFile();
            if(config == null) {
                Log.error("ConfigManager", "Error while parsing config file, it will be replaced and fixed, but the user configurations will be reset");
            } else {
                Log.info("ConfigManager", "Loaded config from file successfully");
            }
            Log.white();
        } catch (FileNotFoundException ex) {
            config = new Config();
            Log.info("ConfigManager", "Creating config file...");
            configLoader.saveToFile(config);
        }

        configUpdaterThread.start();

    }

    public Config getConfig() {
        return config;
    }

    //runnable for updating config file every X seconds
    public class ConfigUpdater implements Runnable {

        public long sleepTime;

        public ConfigUpdater() {
            this(15000);
        }

        public ConfigUpdater(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                configLoader.saveToFile(config);
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

}
