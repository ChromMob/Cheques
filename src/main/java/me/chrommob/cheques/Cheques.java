package me.chrommob.cheques;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Cheques extends JavaPlugin {
    private static Cheques instance;
    private ChequesManager chequesManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        chequesManager = new ChequesManager();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Cheques getInstance() {
        return instance;
    }

    public ChequesManager getChequesManager() {
        return chequesManager;
    }
}
