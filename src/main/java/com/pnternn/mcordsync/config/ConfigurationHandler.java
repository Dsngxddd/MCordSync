package com.pnternn.mcordsync.config;

import com.pnternn.mcordsync.MCordSync;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConfigurationHandler {
    private static YamlConfiguration config;

    public void init(){
        loadConfigFile();
    }

    private void loadConfigFile(){
        File configFile = new File(MCordSync.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        if(!configFile.exists()){
            MCordSync.getInstance().saveResource("config.yml",true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public static ConfigurationSection getSection(String key) {
        if (config == null) {
            return null;
        }
        return config.getConfigurationSection(key);
    }
    public static Set<String> getKeys(String key) {
        ConfigurationSection section = ConfigurationHandler.getSection(key);
        if (section == null) {
            return Collections.emptySet();
        }
        return section.getKeys(false);
    }
    public static void reloadConfig(){
        File configFile = new File(MCordSync.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public static YamlConfiguration getConfig() {
        return config;
    }
    public static void saveConfig(){
        File configFile = new File(MCordSync.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getValue(String key){
        return getConfig().getString(key);
    }
    public static List<String> getList(String key){
        return getConfig().getStringList(key);
    }
}
