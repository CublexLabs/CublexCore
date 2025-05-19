package com.DevBD1.corlex.api;

public class CorlexAPIProvider {

    private static CorlexAPI instance;

    public static void register(CorlexAPI api) {
        if (instance != null) throw new IllegalStateException("CorlexAPI already registered");
        instance = api;
    }

    public static CorlexAPI get() {
        if (instance == null) throw new IllegalStateException("CorlexAPI is not available yet");
        return instance;
    }

    public static boolean isAvailable() {
        return instance != null;
    }
}
