package io.github.devbd1.cublexcore.modules.dialog;

import io.papermc.paper.dialog.Dialog;
import org.bukkit.command.CommandSender;

public class DialogBuilder {
    /**
     * Builds and shows a dialog using config-defined dialogs.
     * Usage: /yourcmd <dialogId>
     * If no id is provided, falls back to "exp_config".
     */
    public boolean build(CommandSender sender, String[] args) {
        final String dialogId = (args != null && args.length > 0) ? args[0] : "exp_config";

        Dialog dialog = DialogConfigManager.buildDialog(dialogId);
        if (dialog == null) {
            sender.sendMessage("Could not build dialog: " + dialogId + " (check dialogs.yml)");
            return false;
        }

        sender.showDialog(dialog);
        return true;
    }
}