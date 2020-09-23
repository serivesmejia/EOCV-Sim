package com.github.serivesmejia.eocvsim.input;

import com.github.serivesmejia.eocvsim.util.SysUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InputSourceLoader {

    public HashMap<String, InputSource> loadedInputSources = new HashMap<>();

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String SOURCES_SAVEFILE_NAME = "eocvsim_sources.json";

    public static File SOURCES_SAVEFILE = new File(SysUtil.getAppData() + File.separator + SOURCES_SAVEFILE_NAME);

    public void saveInputSource(String name, InputSource source) {

        loadedInputSources.put(name, source);

    }

    public void saveInputSourcesToFile() {

        InputSourcesContainer sourcesContainer = new InputSourcesContainer();

        for(Map.Entry<String, InputSource> entry : loadedInputSources.entrySet()) {

            if(!entry.getValue().isDefault){
                InputSource source = entry.getValue().cloneSource();
                sourcesContainer.classifySource(entry.getKey(), source);
            }

        }

        String jsonInputSources = gson.toJson(sourcesContainer);

        SysUtil.saveFileStr(SOURCES_SAVEFILE, jsonInputSources);

    }

    public void loadInputSourcesFromFile() {
        loadInputSourcesFromFile(SOURCES_SAVEFILE);
    }

    public void loadInputSourcesFromFile(File f) {

        String jsonSources = SysUtil.loadFileStr(f);
        if(jsonSources.trim().equals("")) return;

        InputSourcesContainer sources = gson.fromJson(jsonSources, InputSourcesContainer.class);
        sources.updateAllSources();

        loadedInputSources = sources.allSources;

    }

    class InputSourcesContainer {

        public transient HashMap<String, InputSource> allSources = new HashMap<>();

        public HashMap<String, ImageSource> imageSources = new HashMap<>();
        public HashMap<String, CameraSource> cameraSources = new HashMap<>();

        public void updateAllSources() {

            allSources = new HashMap<>();

            for(Map.Entry<String, ImageSource> entry : imageSources.entrySet()) {
                allSources.put(entry.getKey(), entry.getValue());
            }

            for(Map.Entry<String, CameraSource> entry : cameraSources.entrySet()) {
                allSources.put(entry.getKey(), entry.getValue());
            }

        }

        public void classifySource(String sourceName, InputSource source) {

            switch(InputSourceManager.getSourceType(source)) {
                case IMAGE:
                    imageSources.put(sourceName, (ImageSource) source);
                    break;
                case CAMERA:
                    cameraSources.put(sourceName, (CameraSource) source);
                    break;
            }

        }

    }

}
