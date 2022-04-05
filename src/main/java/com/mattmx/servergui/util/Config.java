package com.mattmx.servergui.util;

import com.mattmx.servergui.Servergui;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static YamlConfiguration DEFAULT;
    public static String DEFAULT_PATH = Servergui.get().getDataFolder() + "/config.yml";
    public static YamlConfiguration MESSAGES;
    public static String MESSAGES_PATH = Servergui.get().getDataFolder() + "/messages.yml";

    public static void init() {
        DEFAULT = get(DEFAULT_PATH, "config.yml");
        MESSAGES = get(MESSAGES_PATH, "messages.yml");
    }

    public static void save(YamlConfiguration config, String dest) {
        try {
            config.save(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration get(String path) {
        return get(path, null);
    }

    public static YamlConfiguration get(String path, String def) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (def != null) {
                    Servergui.get().saveResource(def, false);
                } else {
                    file.createNewFile();
                }
                Servergui.get().logger().info("Created " + path);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml;
    }
}
