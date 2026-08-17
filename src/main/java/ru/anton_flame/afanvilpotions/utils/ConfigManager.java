package ru.anton_flame.afanvilpotions.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigManager {

    public static boolean enabledLevel2, enabledLevel3, checkPermissionLevel2, checkPermissionLevel3;
    public static String noPermission, reloaded;
    public static List<String> help;
    public static ConfigurationSection potionsLevel2, potionsLevel3;

    public static void setupConfigValues(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        enabledLevel2 = config.getBoolean("settings.level-2.enabled");
        enabledLevel3 = config.getBoolean("settings.level-3.enabled");
        checkPermissionLevel2 = config.getBoolean("settings.level-2.check-permission");
        checkPermissionLevel3 = config.getBoolean("settings.level-3.check-permission");
        potionsLevel2 = config.getConfigurationSection("settings.level-2.potions");
        potionsLevel3 = config.getConfigurationSection("settings.level-3.potions");
        noPermission = Hex.color(config.getString("messages.no-permission"));
        reloaded = Hex.color(config.getString("messages.reloaded"));
        help = Hex.color(config.getStringList("messages.help"));
    }
}
