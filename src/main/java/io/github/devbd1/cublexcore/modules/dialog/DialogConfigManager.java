package io.github.devbd1.cublexcore.modules.dialog;

import io.papermc.paper.dialog.Dialog;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Loads dialogs.yml and builds Dialogs dynamically from configuration.
 */
public class DialogConfigManager {
    private static JavaPlugin plugin;
    private static FileConfiguration dialogsConfig;

    private DialogConfigManager() {}

    public static void init(JavaPlugin pl) {
        plugin = pl;
        ensureDialogsFile();
        dialogsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "dialogs.yml"));
    }

    public static NamedTextColor parseNamedColor(String v) {
        NamedTextColor color = NamedTextColor.NAMES.value(v);
        if (color == null) {
            throw new IllegalArgumentException("Unknown named color: " + v);
        }
        return color;
    }

    private static void ensureDialogsFile() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolder.mkdirs();
        }
        File dialogsFile = new File(dataFolder, "dialogs.yml");
        if (!dialogsFile.exists()) {
            // Provide a default file if you bundle it in your resources
            try {
                plugin.saveResource("dialogs.yml", false);
            } catch (IllegalArgumentException ignored) {
                // No bundled resource; create empty structure
                YamlConfiguration cfg = new YamlConfiguration();
                cfg.createSection("dialogs");
                try {
                    cfg.save(dialogsFile);
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to create dialogs.yml: " + e.getMessage());
                }
            }
        }
    }

    public static Dialog buildDialog(String id) {
        if (dialogsConfig == null) {
            // Lazy safety if init wasn't called
            init(JavaPlugin.getProvidingPlugin(DialogConfigManager.class));
        }
        
        plugin.getLogger().info("[DEBUG] Building dialog with ID: " + id);
        
        ConfigurationSection root = dialogsConfig.getConfigurationSection("dialogs." + id);
        if (root == null) {
            plugin.getLogger().warning("Dialog id not found in dialogs.yml: " + id);
            return null;
        }

        plugin.getLogger().info("[DEBUG] Found dialog config section for: " + id);

        // Title
        Component title = readComponent(root.getConfigurationSection("title"),
                Component.text("Dialog"));
        plugin.getLogger().info("[DEBUG] Dialog title: " + title.toString());

        // Inputs - Fixed approach
        List<io.papermc.paper.registry.data.dialog.input.DialogInput> inputs = new ArrayList<>();
        
        // Get inputs as raw list
        List<?> inputsList = root.getList("inputs");
        plugin.getLogger().info("[DEBUG] Inputs as list: " + (inputsList != null ? inputsList.size() + " items" : "null"));
        
        if (inputsList != null && !inputsList.isEmpty()) {
            plugin.getLogger().info("[DEBUG] Processing inputs as list...");
            for (int i = 0; i < inputsList.size(); i++) {
                Object inputData = inputsList.get(i);
                plugin.getLogger().info("[DEBUG] Processing input " + i + " of type: " + inputData.getClass().getName());
                
                if (inputData instanceof java.util.Map<?, ?> inputMap) {
                    plugin.getLogger().info("[DEBUG] Input " + i + " map keys: " + inputMap.keySet());
                    
                    // Convert the map to a temporary configuration section for easier processing
                    org.bukkit.configuration.MemoryConfiguration tempConfig = new org.bukkit.configuration.MemoryConfiguration();
                    for (Map.Entry<?, ?> entry : inputMap.entrySet()) {
                        tempConfig.set(entry.getKey().toString(), entry.getValue());
                    }
                    
                    DialogInput built = buildInputFromMap(tempConfig);
                    if (built != null) {
                        inputs.add(built);
                        plugin.getLogger().info("[DEBUG] Successfully built input " + i);
                    } else {
                        plugin.getLogger().warning("[DEBUG] Failed to build input " + i);
                    }
                } else {
                    plugin.getLogger().warning("[DEBUG] Input " + i + " is not a map, skipping");
                }
            }
        } else {
            plugin.getLogger().warning("[DEBUG] No inputs found in dialog config!");
        }
        
        plugin.getLogger().info("[DEBUG] Total inputs built: " + inputs.size());

        DialogBase base = DialogBase.builder(title)
                .inputs(inputs)
                .build();
        plugin.getLogger().info("[DEBUG] DialogBase created with " + inputs.size() + " inputs");

        String type = root.getString("type", "confirmation").toLowerCase(Locale.ROOT);
        DialogType dialogType = switch (type) {
            case "confirmation" -> buildConfirmationType(root.getConfigurationSection("buttons"));
            default -> {
                plugin.getLogger().warning("Unsupported dialog type: " + type + " (using confirmation)");
                yield buildConfirmationType(root.getConfigurationSection("buttons"));
            }
        };

        Dialog dialog = Dialog.create(b -> b.empty().base(base).type(dialogType));
        plugin.getLogger().info("[DEBUG] Final dialog created successfully");
        
        return dialog;
    }

// New method to build input from a map-based configuration
private static DialogInput buildInputFromMap(org.bukkit.configuration.Configuration config) {
    plugin.getLogger().info("[DEBUG] Building input from map-based config");
    
    String id = config.getString("id");
    plugin.getLogger().info("[DEBUG] Input ID: " + id);
    
    if (id == null || id.isBlank()) {
        plugin.getLogger().warning("Dialog input is missing 'id' field");
        return null;
    }
    
    String type = config.getString("type", "text").toLowerCase(Locale.ROOT);
    plugin.getLogger().info("[DEBUG] Input type: " + type);

    // Parse label
    Component label;
    Object labelObj = config.get("label");
    if (labelObj instanceof java.util.Map<?, ?> labelMap) {
        org.bukkit.configuration.MemoryConfiguration labelConfig = new org.bukkit.configuration.MemoryConfiguration();
        for (Map.Entry<?, ?> entry : labelMap.entrySet()) {
            labelConfig.set(entry.getKey().toString(), entry.getValue());
        }
        label = readComponent(labelConfig, Component.text(id));
    } else {
        label = Component.text(id);
    }
    
    plugin.getLogger().info("[DEBUG] Input label: " + label.toString());

    DialogInput result = switch (type) {
        case "number_range" -> {
            plugin.getLogger().info("[DEBUG] Building number_range input");
            float min = (float) config.getDouble("min", 0);
            float max = (float) config.getDouble("max", 100);
            float step = (float) config.getDouble("step", 1);
            float initial = (float) config.getDouble("initial", min);
            int width = config.getInt("width", 300);
            String labelFormat = config.getString("label_format", null);
            
            plugin.getLogger().info("[DEBUG] Number range - min:" + min + ", max:" + max + ", step:" + step + ", initial:" + initial + ", width:" + width);

            var builder = DialogInput.numberRange(id, label, min, max)
                    .step(step)
                    .initial(initial)
                    .width(width);

            if (labelFormat != null && !labelFormat.isBlank()) {
                builder = builder.labelFormat(labelFormat);
                plugin.getLogger().info("[DEBUG] Applied label format: " + labelFormat);
            }

            yield builder.build();
        }
        case "text" -> {
            plugin.getLogger().info("[DEBUG] Building text input");
            String initial = config.getString("initial", "");
            int width = config.getInt("width", 300);
            int maxLength = config.getInt("max_length", 0);
            boolean labelVisible = config.getBoolean("label_visible", true);
            
            plugin.getLogger().info("[DEBUG] Text - initial:'" + initial + "', width:" + width + ", maxLength:" + maxLength + ", labelVisible:" + labelVisible);

            var builder = DialogInput.text(id, label)
                    .initial(initial)
                    .width(width)
                    .labelVisible(labelVisible);

            if (maxLength > 0) {
                builder = builder.maxLength(maxLength);
                plugin.getLogger().info("[DEBUG] Applied max length: " + maxLength);
            }

            // Handle multiline
            Object multilineObj = config.get("multiline");
            if (multilineObj instanceof java.util.Map<?, ?> multiMap) {
                org.bukkit.configuration.MemoryConfiguration multiConfig = new org.bukkit.configuration.MemoryConfiguration();
                for (Map.Entry<?, ?> entry : multiMap.entrySet()) {
                    multiConfig.set(entry.getKey().toString(), entry.getValue());
                }
                int minRows = multiConfig.getInt("min_rows", 1);
                int maxColumns = multiConfig.getInt("max_columns", 50);
                builder = builder.multiline(TextDialogInput.MultilineOptions.create(minRows, maxColumns));
                plugin.getLogger().info("[DEBUG] Applied multiline - minRows:" + minRows + ", maxColumns:" + maxColumns);
            }

            yield builder.build();
        }
        default -> {
            plugin.getLogger().warning("Unsupported input type: " + type + " for id=" + id);
            yield null;
        }
    };
    
    plugin.getLogger().info("[DEBUG] Built input result: " + (result != null ? "SUCCESS" : "FAILED"));
    return result;
}

// Update readComponent to handle both ConfigurationSection and Configuration
private static Component readComponent(org.bukkit.configuration.Configuration config, Component def) {
    if (config == null) return def;
    String text = config.getString("text", "");
    TextColor color = namedOrHex(config.getString("color", null));
    return color != null ? Component.text(text, color) : Component.text(text);
}

    private static DialogType buildConfirmationType(ConfigurationSection buttonsSec) {
        if (buttonsSec == null) {
            // Fallback buttons
            ActionButton confirm = ActionButton.create(
                    Component.text("Confirm", namedOrHex("green")),
                    Component.text("Click to confirm."),
                    100,
                    null
            );
            ActionButton cancel = ActionButton.create(
                    Component.text("Cancel", namedOrHex("red")),
                    Component.text("Click to cancel."),
                    100,
                    null
            );
            return DialogType.confirmation(confirm, cancel);
        }

        ActionButton confirm = buildButton(buttonsSec.getConfigurationSection("confirm"),
                "Confirm", "Click to confirm.", "green", 100);
        ActionButton cancel = buildButton(buttonsSec.getConfigurationSection("cancel"),
                "Cancel", "Click to cancel.", "red", 100);

        return DialogType.confirmation(confirm, cancel);
    }

    private static ActionButton buildButton(ConfigurationSection sec,
                                            String defText,
                                            String defDesc,
                                            String defColor,
                                            int defWeight) {
        if (sec == null) {
            return ActionButton.create(
                    Component.text(defText, namedOrHex(defColor)),
                    Component.text(defDesc),
                    defWeight,
                    null
            );
        }

        String text = sec.getString("text", defText);
        String desc = sec.getString("description", defDesc);
        String colorStr = sec.getString("color", defColor);
        int weight = sec.getInt("weight", defWeight);
        DialogAction action = buildAction(sec.getConfigurationSection("action"));

        return ActionButton.create(
                Component.text(text, namedOrHex(colorStr)),
                Component.text(desc),
                weight,
                action
        );
        }

    private static DialogAction buildAction(ConfigurationSection sec) {
        if (sec == null) return null;
        String type = sec.getString("type", "none").toLowerCase(Locale.ROOT);
        return switch (type) {
            case "custom_click" -> {
                String key = sec.getString("key", "papermc:user_input/confirm");
                yield DialogAction.customClick(Key.key(key), null);
            }
            case "none", "null" -> null;
            default -> {
                plugin.getLogger().warning("Unsupported action type: " + type);
                yield null;
            }
        };
    }

private static DialogInput buildInput(ConfigurationSection in) {
    plugin.getLogger().info("[DEBUG] Building input from config section");
    plugin.getLogger().info("[DEBUG] Input config keys: " + in.getKeys(true));
    plugin.getLogger().info("[DEBUG] Full input config: " + in.getValues(true));
    
    String id = in.getString("id");
    plugin.getLogger().info("[DEBUG] Input ID: " + id);
    
    if (id == null || id.isBlank()) {
        plugin.getLogger().warning("Dialog input is missing 'id' field");
        return null;
    }
    
    String type = in.getString("type", "text").toLowerCase(Locale.ROOT);
    plugin.getLogger().info("[DEBUG] Input type: " + type);

    Component label = readComponent(in.getConfigurationSection("label"),
            Component.text(id));
    plugin.getLogger().info("[DEBUG] Input label: " + label.toString());

    DialogInput result = switch (type) {
        case "number_range" -> {
            plugin.getLogger().info("[DEBUG] Building number_range input");
            float min = (float) in.getDouble("min", 0);
            float max = (float) in.getDouble("max", 100);
            float step = (float) in.getDouble("step", 1);
            float initial = (float) in.getDouble("initial", min);
            int width = in.getInt("width", 300);
            String labelFormat = in.getString("label_format", null);
            
            plugin.getLogger().info("[DEBUG] Number range - min:" + min + ", max:" + max + ", step:" + step + ", initial:" + initial + ", width:" + width);

            var builder = DialogInput.numberRange(id, label, min, max)
                    .step(step)
                    .initial(initial)
                    .width(width);

            if (labelFormat != null && !labelFormat.isBlank()) {
                builder = builder.labelFormat(labelFormat);
                plugin.getLogger().info("[DEBUG] Applied label format: " + labelFormat);
            }

            yield builder.build();
        }
        case "text" -> {
            plugin.getLogger().info("[DEBUG] Building text input");
            String initial = in.getString("initial", "");
            int width = in.getInt("width", 300);
            int maxLength = in.getInt("max_length", 0);
            boolean labelVisible = in.getBoolean("label_visible", true);
            
            plugin.getLogger().info("[DEBUG] Text - initial:'" + initial + "', width:" + width + ", maxLength:" + maxLength + ", labelVisible:" + labelVisible);

            var builder = DialogInput.text(id, label)
                    .initial(initial)
                    .width(width)
                    .labelVisible(labelVisible);

            if (maxLength > 0) {
                builder = builder.maxLength(maxLength);
                plugin.getLogger().info("[DEBUG] Applied max length: " + maxLength);
            }

            ConfigurationSection multi = in.getConfigurationSection("multiline");
            if (multi != null) {
                int minRows = multi.getInt("min_rows", 1);
                int maxColumns = multi.getInt("max_columns", 50);
                builder = builder.multiline(TextDialogInput.MultilineOptions.create(minRows, maxColumns));
                plugin.getLogger().info("[DEBUG] Applied multiline - minRows:" + minRows + ", maxColumns:" + maxColumns);
            }

            yield builder.build();
        }
        default -> {
            plugin.getLogger().warning("Unsupported input type: " + type + " for id=" + id);
            yield null;
        }
    };
    
    plugin.getLogger().info("[DEBUG] Built input result: " + (result != null ? "SUCCESS" : "FAILED"));
    return result;
}

    private static Component readComponent(ConfigurationSection sec, Component def) {
        if (sec == null) return def;
        String text = sec.getString("text", "");
        TextColor color = namedOrHex(sec.getString("color", null));
        return color != null ? Component.text(text, color) : Component.text(text);
    }

    // java
    private static TextColor namedOrHex(String value) {
        if (value == null || value.isBlank()) return null;

        String v = value.trim();

        // Try hex with leading '#'
        if (v.startsWith("#")) {
            TextColor hex = TextColor.fromHexString(v);
            if (hex != null) return hex;
        }

        // Try named color
        NamedTextColor named = NamedTextColor.NAMES.value(v); // case-insensitive lookup
        if (named != null) return named;

        // Best-effort: try hex without '#'
        TextColor hex = TextColor.fromHexString("#" + v);
        return hex; // may be null if invalid
    }

    // Add this method inside the DialogConfigManager class
    public static java.util.List<String> getDialogIds() {
        if (dialogsConfig == null) {
            throw new IllegalStateException("DialogConfigManager is not initialized. Call DialogConfigManager.init(plugin) first.");
        }

        org.bukkit.configuration.ConfigurationSection dialogsSection = dialogsConfig.getConfigurationSection("dialogs");
        if (dialogsSection == null) {
            return java.util.List.of();
        }

        return new java.util.ArrayList<>(dialogsSection.getKeys(false));
    }
}