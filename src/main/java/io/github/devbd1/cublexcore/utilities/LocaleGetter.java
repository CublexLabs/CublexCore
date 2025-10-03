/**
 * This class gets and returns Player's Locale, and returns the Default Locale if it catches an error.
 * The check is in the list of supported locales in another program.
 */
package io.github.devbd1.cublexcore.utilities;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Locale;

public class LocaleGetter {

    public static String getPlayerLocale(Player player) {
        try {
            // Önce Paper API'sini dene (locale())
            Method localeMethod = Player.class.getMethod("locale");
            Object result = localeMethod.invoke(player);
            if (result instanceof Locale) {
                return ((Locale) result).getLanguage().toLowerCase();
            }
        } catch (NoSuchMethodException ignored) {
            // Paper metodu yoksa -> Spigot kullan
            try {
                Method getLocaleMethod = Player.class.getMethod("getLocale");
                Object result = getLocaleMethod.invoke(player);
                if (result instanceof String) {
                    // Örn: "en_us" gibi -> sadece dili al
                    String[] parts = ((String) result).split("_");
                    return parts[0].toLowerCase();
                }
            } catch (Exception e) {
                // Hata olduysa fallback'e düş
                return getDefaultLocale();
            }
        } catch (Exception e) {
            return getDefaultLocale();
        }

        // Hiçbir yöntem çalışmazsa fallback
        return getDefaultLocale();
    }

    public static String getDefaultLocale() {

        return ConfigManager.getString("fallback-language", "en").toLowerCase();
    }
}
