package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.craftventory.internal.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void sendMessage(Player player, String message) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        if(message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        message = TextUtil.parseColors(message);
        player.sendMessage(message);
    }

    public static void sendMessage(Player player, ConfigurationSection section, String key) {

        if(section == null) {
            throw new IllegalArgumentException("section cannot be null");
        }

        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be null or empty");
        }

        String message = section.getString(key, "");
        MessageUtil.sendMessage(player, message);
    }
}
