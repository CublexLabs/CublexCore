package com.DevBD1.corlex.hooks;

import com.DevBD1.corlex.api.CorlexAPIProvider;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateInterceptor {

    private final JavaPlugin plugin;

    public TranslateInterceptor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.SET_ACTION_BAR_TEXT,
                PacketType.Play.Server.TITLE
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!CorlexAPIProvider.isAvailable()) return;

                Player player = event.getPlayer();
                WrappedChatComponent wrapped = event.getPacket().getChatComponents().read(0);
                if (wrapped == null || wrapped.getJson() == null) return;

                String json = wrapped.getJson();
                Component originalComponent = GsonComponentSerializer.gson().deserialize(json);
                String plainText = LegacyComponentSerializer.legacySection().serialize(originalComponent);

                if (!plainText.startsWith("translate.")) return;

                // Extract key and arguments
                String[] parts = plainText.split(",");
                String key = parts[0].trim();
                Map<String, String> placeholders = new HashMap<>();

                for (int i = 1; i < parts.length; i++) {
                    placeholders.put("$" + i, parts[i].trim());
                }

                String translated = CorlexAPIProvider.get().get(player, key, placeholders);
                Component newComponent = Component.text(translated);
                String newJson = GsonComponentSerializer.gson().serialize(newComponent);

                event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(newJson));
                plugin.getLogger().fine("[Corlex] Translated: " + plainText + " → " + translated);
            }
        });
    }
}
