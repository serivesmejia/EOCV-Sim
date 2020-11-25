package com.github.serivesmejia.eocvsim.config;

import com.github.serivesmejia.eocvsim.util.Log;

public class ConfigManager {

    ConfigLoader configLoader = new ConfigLoader();

    Config config;

    public void init() {

        Log.info("ConfigManager", "Initializing...");

        config = configLoader.loadFromFile();

        if(config == null) {
            Log.error("ConfigManager", "Error while parsing config file, it will be replaced and fixed, but the user configurations will be reset");
            Log.white();
            config = new Config();
        }

    }

    public Config getConfig() {
        return config;
    }

}
