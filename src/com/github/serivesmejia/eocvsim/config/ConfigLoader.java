package com.github.serivesmejia.eocvsim.config;

import com.github.serivesmejia.eocvsim.input.InputSourceLoader;
import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.SysUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class ConfigLoader {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String CONFIG_SAVEFILE_NAME = "eocvsim_config.json";
    public static File CONFIG_SAVEFILE = new File(SysUtil.getAppData() + File.separator + CONFIG_SAVEFILE_NAME);

    public Config loadFromFile(File file) {

        if(!file.exists()) return null;

        String jsonConfig = SysUtil.loadFileStr(file);
        if(jsonConfig.trim().equals("")) return null;

        try {
            return gson.fromJson(jsonConfig, Config.class);
        } catch(Throwable ex) {
            return null;
        }

    }

    public Config loadFromFile() {
        return loadFromFile(CONFIG_SAVEFILE);
    }

    public void saveToFile(File file, Config conf) {
        String jsonConfig = gson.toJson(conf);
        SysUtil.saveFileStr(file, jsonConfig);
    }

    public void saveToFile(Config conf) {
        saveToFile(CONFIG_SAVEFILE, conf);
    }

}
