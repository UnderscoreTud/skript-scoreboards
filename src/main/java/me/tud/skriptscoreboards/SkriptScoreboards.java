package me.tud.skriptscoreboards;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import com.google.common.base.Preconditions;
import me.tud.skriptscoreboards.fastboard.FastBoardManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkriptScoreboards extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 23472;

    private static SkriptScoreboards instance;
    private SkriptAddon addonInstance;

    @Override
    public void onEnable() {
        instance = this;

        if (!getServer().getPluginManager().isPluginEnabled("Skript")) {
            getLogger().severe("Skript could not be found. Disabling skript-scoreboards...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Skript.getVersion().isSmallerThan(new Version(2, 9, 0))) {
            getLogger().severe("skript-scoreboards requires Skript 2.9.0 or later. Disabling skript-scoreboards...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!Skript.isAcceptRegistrations()) {
            getLogger().severe("Skript is not accepting registrations. Disabling skript-scoreboards...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        FastBoardManager.registerListener(this);

        try {
            addonInstance = Skript.registerAddon(this).setLanguageFileDirectory("lang");
            addonInstance.loadClasses("me.tud.skriptscoreboards.elements");
        } catch (Exception e) {
            getLogger().severe("An error occurred while loading skript-scoreboards. Disabling skript-scoreboards...");
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    public SkriptAddon getAddonInstance() {
        return addonInstance;
    }

    public static SkriptScoreboards instance() {
        Preconditions.checkState(instance != null, "Plugin not enabled yet");
        return instance;
    }

}
