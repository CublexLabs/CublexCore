package io.github.devbd1.cublexcore.modules.dialog;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class DialogEventListener implements Listener {
    private static final Key CONFIRM_ID = Key.key("papermc:user_input/confirm");

    @EventHandler
    public void onCustomClick(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(CONFIRM_ID)) return;

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) return;

        Float levelF = view.getFloat("level");
        Float expF = view.getFloat("experience");
        String playerName = view.getText("player_name"); // Use getText() instead of getString()

        if (levelF == null || expF == null) return;

        int levels = levelF.intValue();
        float expPercent = expF;

        if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
            Player player = conn.getPlayer();
            player.sendMessage("Setting " + (playerName != null && !playerName.isEmpty() ? playerName : "yourself") + " to level " + levels);
            player.setLevel(levels);
            player.setExp(expPercent / 100f);
        }
    }
}
