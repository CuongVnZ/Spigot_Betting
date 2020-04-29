package net.cuongvnz.business1;

import java.io.File;

import net.cuongvnz.business1.menus.MenuManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.cuongvnz.business1.bet.BetManager;
import net.cuongvnz.business1.commands.CommandManager;

public class BettingPlugin extends JavaPlugin{

    public static BettingPlugin plugin;

    public Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;

        if (!setupEconomy() ) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File f = getDataFolder();
        if (!f.exists())
            f.mkdirs();
        
        saveDefaultConfig();

        // Instantiate Managers here
        new CommandManager(this);
        new MenuManager(this);
        new BetManager(this);

        Settings.reload();
        plugin.getLogger().info("Betting Game by ChinnSu.");

    }

    @Override
    public void onDisable() {
        try {
            ManagerInstances.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
}
