package io.github.devbd1.cublexcore.modules;

import io.github.devbd1.cublexcore.Main;
import io.github.devbd1.cublexcore.modules.text.PacketTranslator;
import io.github.devbd1.cublexcore.services.CorlexAPI;
import io.github.devbd1.cublexcore.modules.logger.LoggingManager;

public class ModuleRegistrar {
    private ModuleRegistrar() {
    }
    public static void register(Main plugin, CorlexAPI api, LoggingManager logger) {

        // ** ProtocolLib packet‐translation hook **
        PacketTranslator.init(plugin, api, logger);
    }
}
