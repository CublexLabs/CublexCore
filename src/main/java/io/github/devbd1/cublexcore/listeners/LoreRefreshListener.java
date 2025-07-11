    package io.github.devbd1.cublexcore.listeners;

    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.player.PlayerDropItemEvent;
    import org.bukkit.event.player.PlayerPickupItemEvent;
    import org.bukkit.event.inventory.InventoryClickEvent;
    import org.bukkit.event.player.PlayerItemHeldEvent;
    import org.bukkit.entity.Player;

    public class LoreRefreshListener implements Listener {

        @EventHandler
        public void onItemDrop(PlayerDropItemEvent event) {
            refresh(event.getPlayer());
        }

        @EventHandler
        public void onItemPickup(PlayerPickupItemEvent event) {
            refresh(event.getPlayer());
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getWhoClicked() instanceof Player player) {
                refresh(player);
            }
        }

        @EventHandler
        public void onItemHeld(PlayerItemHeldEvent event) {
            refresh(event.getPlayer());
        }

        private void refresh(Player player) {
            //player.updateInventory();
        }
    }
