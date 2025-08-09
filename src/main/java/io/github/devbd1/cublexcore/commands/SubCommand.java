package io.github.devbd1.cublexcore.commands;

import org.bukkit.command.CommandSender;
import java.util.List;

   /**
    * Represents a subcommand that can be registered with the command manager.
    */
   public interface SubCommand {
    String DEFAULT_DESCRIPTION = "No description provided.";
    String COMMAND_PREFIX = "/";

    /**
     * Gets the primary name of this command.
     * @return The command's name
     */
    String getName();

    /**
     * Gets alternative names (aliases) for this command.
     * Note: This method is kept for backward compatibility, but aliases are now
     * primarily managed through config.yml via CommandConfig.getAliases().
     * @return List of command aliases
     */
    List<String> getAliases();

    /**
     * Gets the list of permissions required to use this command.
     * Note: This method is kept for backward compatibility, but permissions are now
     * primarily managed through config.yml via CommandConfig.getPermissions().
     * @return List of permission strings
     */
    List<String> getPermissions();

    /**
     * Gets the command description.
     * @return Command description
     */
    default String getDescription() {
        return DEFAULT_DESCRIPTION;
    }

    /**
     * Gets the command usage syntax.
     *
     * @return Command usage string
     */
    default String getUsage() {
        return COMMAND_PREFIX + "cublex " + getName() + " (" + String.join("/", getAliases()) + ")";
    }

    /**
     * Provides tab completion suggestions for this command.
     * Note: Permission checking is handled by CommandManager before this method is called.
     *
     * @param sender The command sender
     * @param args Current command arguments
     * @return List of suggestions
     */
    List<String> tabComplete(CommandSender sender, String[] args);

    /**
     * Executes the command logic.
     * Note: Permission checking is handled by CommandManager before this method is called.
     *
     * @param sender The command sender
     * @param args The command arguments
     * @return true if the command was executed successfully
     */
    boolean execute(CommandSender sender, String[] args);

}