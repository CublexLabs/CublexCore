package io.github.devbd1.corlex.modules.text;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.devbd1.corlex.services.CorlexAPI;
import io.github.devbd1.corlex.utilities.LoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PacketTranslator {
    private static final Gson GSON = new Gson();

    /**
     * Call once in onEnable() after you’ve built your CorlexAPI and LoggingManager.
     */
    public static void init(JavaPlugin plugin, CorlexAPI api, LoggingManager logger) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            logger.log("ProtocolLib not found. PacketTranslator disabled.");
            return;
        }
        logger.log("ProtocolLib found. Registering PacketTranslator.");
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    handlePacket(event, api);
                } catch (Throwable t) {
                    logger.log("Error in PacketTranslator: " + t.getMessage());
                    t.printStackTrace();
                }
            }
        });
    }

    private static void handlePacket(PacketEvent event, CorlexAPI api) {
        PacketType type = event.getPacketType();
        if (type != PacketType.Play.Server.CHAT
                && type != PacketType.Play.Server.SYSTEM_CHAT) {
            return;
        }

        Player player = event.getPlayer();
        WrappedChatComponent comp = event.getPacket()
                .getChatComponents()
                .read(0);
        if (comp == null) return;
        String rawJson = comp.getJson();
        if (rawJson == null || rawJson.isEmpty()) return;

        // parse the JSON into a tree
        JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();
        JsonElement extraEl = root.get("extra");
        if (extraEl == null || !extraEl.isJsonArray()) return;
        JsonArray extra = extraEl.getAsJsonArray();

        boolean sawMarker = false;
        for (int i = 0; i < extra.size(); i++) {
            JsonElement el = extra.get(i);

            // 1) a bare "translate@" or "translate." string?
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                String txt = el.getAsString();
                if ("translate@".equals(txt) || "translate.".equals(txt)) {
                    sawMarker = true;
                    extra.set(i, new JsonPrimitive("")); // remove marker
                    continue;
                }
            }

            // 2) if we just saw a marker, this object has the key
            if (sawMarker && el.isJsonObject()) {
                JsonObject compObj = el.getAsJsonObject();
                JsonElement textEl = compObj.get("text");
                if (textEl != null && textEl.isJsonPrimitive()) {
                    String key = textEl.getAsString();
                    String translated = api.get(player, key, Map.of());
                    compObj.addProperty("text", translated);
                }
                sawMarker = false;
            }
        }

        // serialize back and write into packet
        String newJson = GSON.toJson(root);
        event.getPacket()
                .getChatComponents()
                .write(0, WrappedChatComponent.fromJson(newJson));
    }
}
