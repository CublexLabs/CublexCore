package io.github.devbd1.cublexcore.modules.text;

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
import io.github.devbd1.cublexcore.services.CorlexAPI;
import io.github.devbd1.cublexcore.utilities.ColorManager;
import io.github.devbd1.cublexcore.modules.logging.LoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PacketTranslator {
    private static final Gson GSON = new Gson();

    public static void init(JavaPlugin plugin, CorlexAPI api, LoggingManager logger) {
        logger.log("[Debug] PacketTranslator.init()");
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            logger.log("[Debug] ProtocolLib not enabled; skipping PacketTranslator");
            return;
        }
        logger.log("[Debug] ProtocolLib found; registering listener");
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                logger.log("[Debug] onPacketSending: " + event.getPacketType());
                handlePacket(event, api, logger);
            }
        });
    }

    private static void handlePacket(PacketEvent event, CorlexAPI api, LoggingManager logger) {
        WrappedChatComponent comp = event.getPacket().getChatComponents().read(0);
        if (comp == null) {
            logger.log("[Debug] comp == null -> returned");
            return;
        }
        String rawJson = comp.getJson();
        logger.log("[Debug] rawJson=" + rawJson);
        if (rawJson == null || rawJson.isEmpty()) return;

        JsonObject root;
        try {
            root = JsonParser.parseString(rawJson).getAsJsonObject();
        } catch (Exception e) {
            logger.log("[Debug] JSON parse error: " + e.getMessage());
            return;
        }

        JsonElement extraEl = root.get("extra");
        if (!(extraEl instanceof JsonArray)) return;
        JsonArray extra = extraEl.getAsJsonArray();
        logger.log("[Debug] extra size=" + extra.size());

        // find marker and gather params
        for (int i = 0; i < extra.size(); i++) {
            JsonElement el = extra.get(i);

            // 1) Eğer direkt string ise
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                String str = el.getAsString();
                if (str.startsWith("translate.")) {
                    // ... [Önceki gibi işlenir]
                }
            }

            // 2) Eğer obje ve içinde "text" alanı varsa
            if (el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("text")) {
                    String text = obj.get("text").getAsString();
                    if (text.startsWith("translate.")) {
                        logger.log("[Debug] found marker in object: " + text);

                        String tail = text.substring("translate.".length());
                        String[] parts = tail.split("\\s*,\\s*");
                        if (parts.length == 0) continue;
                        String key = parts[0];
                        String[] params = new String[parts.length - 1];
                        for (int k = 1; k < parts.length; k++) {
                            params[k - 1] = parts[k];
                        }
                        logger.log("[Debug] key='" + key + "' params=" + java.util.Arrays.toString(params));
                        String template = api.get(event.getPlayer(), key, Map.of());
                        logger.log("[Debug] fetched template: " + template);

                        if (template != null) {
                            for (int p = 0; p < params.length; p++) {
                                template = template.replace("$" + (p + 1), params[p]);
                            }
                            logger.log("[Debug] after param replace: " + template);
                        } else {
                            logger.log("[Debug] no template found for key: " + key);
                            template = "";
                        }

                        // template: Çevrilmiş ve parametreleri doldurulmuş mesaj
                        String colored = ColorManager.applyColorFormatting(template);
                        obj.addProperty("text", colored);

                        // opsiyonel: click_event, hover_event silmek için:
                        // obj.remove("click_event");
                        // obj.remove("hover_event");
                    }
                }
            }
        }

        // write back
        String newJson = GSON.toJson(root);
        logger.log("[Debug] writing newJson=" + newJson);
        event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(newJson));
    }
}
