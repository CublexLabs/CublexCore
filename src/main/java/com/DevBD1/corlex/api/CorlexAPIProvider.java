package com.DevBD1.corlex.api;

import org.bukkit.Bukkit;

public class CorlexAPIProvider {

    private static CorlexAPI instance;

    public static CorlexAPI get() {
        if (instance == null) {
            instance = Bukkit.getServicesManager().load(CorlexAPI.class);
            if (instance == null) {
                throw new IllegalStateException("CorlexAPI is not available. Is the Corlex plugin loaded?");
            }
        }
        return instance;
    }
}
