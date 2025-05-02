package com.github.syr0ws.minewaypoints.platform.spigot.integration;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Integration {

    private final Plugin plugin;
    private final List<Listener> listeners = new ArrayList<>();
    private boolean enabled;

    public Integration(Plugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");
        this.plugin = plugin;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract boolean canBeEnabled();

    public abstract String getName();

    public void enable() {

        if (this.enabled) {
            throw new IllegalStateException("Integration already enabled");
        }

        this.enabled = true;
        this.onEnable();
        this.plugin.getLogger().info("Integration '%s' has been enabled.".formatted(this.getName()));
    }

    public void disable() {

        if (!this.enabled) {
            throw new IllegalStateException("Integration not enabled");
        }

        this.enabled = false;
        this.unregisterListeners();
        this.onDisable();
        this.plugin.getLogger().info("Integration '%s' has been disabled.".formatted(this.getName()));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    protected void registerListener(Listener listener) {
        Validate.notNull(listener, "listener cannot be null");

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(listener, this.plugin);

        this.listeners.add(listener);
    }

    private void unregisterListeners() {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.listeners.clear();
    }
}
