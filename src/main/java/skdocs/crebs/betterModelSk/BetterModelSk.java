package skdocs.crebs.betterModelSk;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterModelSk extends JavaPlugin {

    private static BetterModelSk instance;

    public static BetterModelSk getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        AddonLoader loader = new AddonLoader(this);
        if (!loader.canLoad()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("BetterModelSk enabled - registered better model Skript syntax.");
    }

    @Override
    public void onDisable() {
    }
}
