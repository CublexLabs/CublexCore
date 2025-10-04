package io.github.devbd1.cublexcore.utilities;

import org.bukkit.command.CommandSender;
import org.lushplugins.chatcolorhandler.ChatColorHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    public void check(CommandSender sender, String owner, String name, String version) {
        String latestTag = fetchLatestReleaseTag(owner, name);
        if (latestTag == null) {
            ChatColorHandler.sendMessage(sender, "§eCouldn't get the version info.");
        } else {
            // latestTag sample: "v1.2.3" or "1.2.3"
            // compare with version; basic semver comparison
            if (isOutdated(version, latestTag)) {
                ChatColorHandler.sendMessage(sender, "§eThere is a new version: §6" + latestTag + "§ePlease update!");
            } else {
                ChatColorHandler.sendMessage(sender, "§2You are running the latest version for §6" + name + "§2.");
            }
        }
    }

    /**
     * Fetches latest release tag from GitHub API.
     */
    public String fetchLatestReleaseTag(String owner, String repo) {
        try {
            URL url = new URL("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github+json");
            conn.setRequestProperty("User-Agent", "CublexCore-VersionChecker");  // User-Agent may be required for GitHub API req

            int status = conn.getResponseCode();
            if (status != 200) {
                conn.disconnect();
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();

            String json = response.toString();
            // "tag_name":"v1.2.3" kısmını ayıkla
            String target = "\"tag_name\":\"";
            int idx = json.indexOf(target);
            if (idx == -1) return null;
            int start = idx + target.length();
            int end = json.indexOf("\"", start);
            if (end == -1) return null;
            return json.substring(start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Simple version comparison: It returns true if version < latestTag.
     * version and latestTag must be in “1.2.3” or “v1.2.3” format.
     */
    public boolean isOutdated(String localVersion, String latestTag) {
        String v1 = localVersion.startsWith("v") ? localVersion.substring(1) : localVersion;
        String v2 = latestTag.startsWith("v") ? latestTag.substring(1) : latestTag;

        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int len = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < len; i++) {
            int num1 = i < parts1.length ? parseIntSafe(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseIntSafe(parts2[i]) : 0;
            if (num1 < num2) return true;
            else if (num1 > num2) return false;
        }
        return false; // Your version number is equal or higher
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
