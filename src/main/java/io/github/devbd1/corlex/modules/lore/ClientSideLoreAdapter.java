package io.github.devbd1.corlex.modules.lore;

import io.github.devbd1.corlex.hooks.CubItems.CubItemsLoreRegistry;
import io.github.devbd1.corlex.listeners.LoreRefreshListener;
import io.github.devbd1.corlex.utilities.LocaleGetter;
import de.tr7zw.changeme.nbtapi.NBTItem;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientSideLoreAdapter {

    private final JavaPlugin plugin;
    private final ProtocolManager protocolManager;

    public ClientSideLoreAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void register() {
        CubItemsLoreRegistry.load(new File(plugin.getDataFolder().getParentFile(), "CubItems"));

        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();

                if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
                    ItemStack item = event.getPacket().getItemModifier().read(0);
                    ItemStack modified = injectClientSideLore(player, item);
                    event.getPacket().getItemModifier().write(0, modified);
                } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
                    List<ItemStack> items = event.getPacket().getItemListModifier().read(0);
                    List<ItemStack> modifiedItems = items.stream()
                            .map(item -> injectClientSideLore(player, item))
                            .collect(Collectors.toList());
                    event.getPacket().getItemListModifier().write(0, modifiedItems);
                }
            }
        });

        Bukkit.getPluginManager().registerEvents(new LoreRefreshListener(), plugin);
    }

    private ItemStack injectClientSideLore(Player player, ItemStack item) {

        if (item == null || item.getType() == Material.AIR) return item;

        System.out.println("[Corlex] Processing item: " + item.getType());

        //NBTItem nbt = new NBTItem(item);
        //if (!nbt.hasTag("cubItems")) return item;

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasTag("cubItems")) {
            System.out.println("[Corlex] No 'cubItems' tag found.");
            return item;
        }

        String key = nbt.getString("cubItems");
        System.out.println("[Corlex] Found cubItems tag: " + key);

        //String lang = CorlexAPI.getLanguage(player); // or your localization accessor
        String lang = LocaleGetter.getPlayerLocale(player);

        List<String> customLore = CubItemsLoreRegistry.getLore(key, lang);
        //if (customLore.isEmpty()) return item;
        if (customLore.isEmpty()) {
            System.out.println("[Corlex] No lore found for key: " + key);
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<String> baseLore = meta.getLore();
        if (baseLore == null) baseLore = new ArrayList<>();

        // Combine both: real lore + fake lore
        List<String> combined = new ArrayList<>(baseLore);
        combined.add("");
        combined.addAll(customLore);

        meta.setLore(combined);
        item.setItemMeta(meta);
        return item;
    }
}
