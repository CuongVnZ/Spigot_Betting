package net.cuongvnz.business1;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class AbstractManager implements Listener {

    public static BettingPlugin plugin;

    public AbstractManager(BettingPlugin pl) {
        plugin = pl;
        try {
            load(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Little bit intensive but only used on startup
        String full = this.getClass().toString();
        String s = full;
        try {
            full = full.substring(full.indexOf("net.cuongvnz.") + "net.cuongvnz.".length());
            String[] data = full.split("\\.");
            s = data[0] + "-" + data[data.length - 1];
        } catch (Exception e) {
            System.out.println("Error parsing AbstractManager display name for " + this.getClass());
        }
        plugin.getLogger().info("Loaded " + s + ".");
    }

    private void load(BettingPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initialize();
        try {
            ManagerInstances.registerManager(this.getClass(), this, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unload(BettingPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    public abstract void initialize();

}
