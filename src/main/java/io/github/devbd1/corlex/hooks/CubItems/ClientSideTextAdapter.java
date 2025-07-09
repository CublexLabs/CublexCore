package io.github.devbd1.corlex.hooks.CubItems;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.devbd1.corlex.modules.text.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intercepts various chat and title packets starting with a configurable prefix and translates keys using ProtocolLib
 * and the existing Lang translator function.
 */
public class ClientSideTextAdapter {
    private static final String PREFIX = "translate.";
    private final ProtocolManager protocolManager;
    private final Pattern translatePattern = Pattern.compile("\\\"" + PREFIX + "([A-Za-z0-9_.]+)\\\"");

    public ClientSideTextAdapter(JavaPlugin plugin) {
        // Ensure Lang is initialized (if needed)
//        Lang.setup(plugin);
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Registers the packet listener within the given plugin context for multiple packet types.
     */
    public void register(JavaPlugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(
                plugin,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.SET_ACTION_BAR_TEXT,
                PacketType.Play.Server.TITLE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrappedChatComponent original;
                try {
                    original = event.getPacket().getChatComponents().read(0);
                } catch (IllegalArgumentException e) {
                    // Some packets may not have chat components
                    return;
                }
                String json = original.getJson();

                // Only process if prefix exists
                if (!json.contains(PREFIX)) return;

                Matcher matcher = translatePattern.matcher(json);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String key = matcher.group(1);
                    // Use Lang translator function, stripping prefix
//                    String translation = Lang.translate(key);
//                    matcher.appendReplacement(sb, "\"" + Matcher.quoteReplacement(translation) + "\"");
                }
                matcher.appendTail(sb);

                event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(sb.toString()));
            }
        });
        plugin.getLogger().info("ClientSideTextAdapter registered using Lang translator.");
    }

    /**
     * Reloads Lang translations at runtime.
     */
    public void reload() {
//        Lang.reload();
    }
}
