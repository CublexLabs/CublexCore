package io.github.devbd1.cublexcore.commands.test;

import io.github.devbd1.cublexcore.commands.CommandConfig;
import io.github.devbd1.cublexcore.commands.SubCommand;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TestDialogScreen implements SubCommand {

    /**
     * Gets the primary name of this command.
     *
     * @return The command's name
     */
    @Override
    public String getName() {
        return "testdialog";
    }

    /**
     * Gets alternative names (aliases) for this command.
     * Note: This method is kept for backward compatibility, but aliases are now
     * primarily managed through config.yml via CommandConfig.getAliases().
     *
     * @return List of command aliases
     */
    @Override
    public List<String> getAliases() {
        return CommandConfig.getAliases("admin_cmds");
    }

    /**
     * Gets the list of permissions required to use this command.
     * Note: This method is kept for backward compatibility, but permissions are now
     * primarily managed through config.yml via CommandConfig.getPermissions().
     *
     * @return List of permission strings
     */
    @Override
    public List<String> getPermissions() {
        return CommandConfig.getPermissions("admin_cmds");
    }

    /**
     * Gets the command description.
     *
     * @return Command description
     */
    @Override
    public String getDescription() {
        return "Tests the dialog screen, that displays a Custom Screen. This is a feature as of 1.21.6!";
    }

    /**
     * Gets the command usage syntax.
     *
     * @return Command usage string
     */
    @Override
    public String getUsage() {
        return SubCommand.super.getUsage();
    }

    /**
     * Provides tab completion suggestions for this command.
     * Note: Permission checking is handled by CommandManager before this method is called.
     *
     * @param sender The command sender
     * @param args   Current command arguments
     * @return List of suggestions
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    /**
     * Executes the command logic.
     * Note: Permission checking is handled by CommandManager before this method is called.
     *
     * @param sender The command sender
     * @param args   The command arguments
     * @return true if the command was executed successfully
     */
    @SuppressWarnings("all")
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        ActionButton no = ActionButton.create(
                Component.text("Discard", TextColor.color(0xFFA0B1)),
                Component.text("Click to discard your input."),
                100,
                null
        );

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Configure your new experience value"))
                .inputs(List.of(
                        DialogInput.numberRange("level", Component.text("Level", NamedTextColor.GREEN), 0f, 100f)
                                .step(1f)
                                .initial(0f)
                                .width(300)
                                .build(),
                        DialogInput.numberRange("experience", Component.text("Experience", NamedTextColor.GREEN), 0f, 100f)
                                .step(1f)
                                .initial(0f)
                                .labelFormat("%s: %s percent to the next level")
                                .width(300)
                                .build(),
                        DialogInput.text("player_name", Component.text("Player Name", NamedTextColor.BLUE))
                                .maxLength(16)
                                .width(300)
                                .labelVisible(true)
                                .initial("?? name ??")
                                .multiline(TextDialogInput.MultilineOptions.create(2, 50))

                                .build()
                ))
                .build()
        )
        .type(DialogType.confirmation(
                ActionButton.create(
                        Component.text("Confirm", TextColor.color(0xAEFFC1)),
                        Component.text("Click to confirm your input."),
                        100,
                        DialogAction.customClick(Key.key("papermc:user_input/confirm"), null)
                ),
                no
        ))
);

        sender.showDialog(dialog);

        return false;
    }
}
