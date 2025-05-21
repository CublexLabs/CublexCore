// com.DevBD1.corlex.api.lore.ClientSideLoreService
package com.DevBD1.corlex.api.lore;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public interface ClientSideLoreService {
    void setClientSideLore(ItemStack item, List<String> lore, Predicate<Player> condition);
}
