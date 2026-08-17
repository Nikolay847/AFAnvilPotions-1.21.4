package ru.anton_flame.afanvilpotions;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anton_flame.afanvilpotions.commands.AFAnvilPotionsCommand;
import ru.anton_flame.afanvilpotions.listeners.Listeners;
import ru.anton_flame.afanvilpotions.utils.ConfigManager;

public final class AFAnvilPotions extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Плагин был включен!");
        saveDefaultConfig();
        ConfigManager.setupConfigValues(this);
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        PluginCommand afAnvilPotionsCommand = getCommand("afanvilpotions");
        AFAnvilPotionsCommand afAnvilPotionsCommandClass = new AFAnvilPotionsCommand(this);
        afAnvilPotionsCommand.setExecutor(afAnvilPotionsCommandClass);
        afAnvilPotionsCommand.setTabCompleter(afAnvilPotionsCommandClass);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин был выключен!");
    }
}
