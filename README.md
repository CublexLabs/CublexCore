[![Wiki: 1.0](https://img.shields.io/badge/Wiki-Cublex_Labs-blue.svg)]([https://creativecommons.org/licenses/by/4.0/](https://labs.cublex.net/docs/category/common-in-all-plugins))
# Cub - The Core

**The Modular API Foundation for Cublex Plugin Ecosystem.**
Cub.jar is a robust and extensible API plugin designed to serve as the shared core for Cub plugin ecosystem. It centralizes localization, logging, and more — enabling other plugins to integrate seamlessly.

---

## ✨ 🧪 Features

- 🌍 **Native Localization**: Supports YAML-based i18n with `{placeholders}`, automatic fallback to the `{default-language}`
- 🧾 **Centralized Log&Debug System**: Missing keys, fallback events, and in-game log viewing
- 🧱 **Mini-Message Support**: Defaults, server name, and more in `config.yml`
- 📌 **Placeholders**: Dynamic placeholders like `{player}`, `{world}`, `{coins}`, etc. and static placeholders from config.yml like `{server-name}`, `{discord}`, etc.

---

## 🔧 Config Example

```yaml
# Static variables. These cannot be changed during runtime. You must stop the server to make changes here
server-name: cublex.net
fallback-language: en # it is used when the player's client locale is not supported
logging-enabled: false # enable/disable logging into a separate file under /plugins/Corlex folder (universal)
restful-enabled: false # enable/disable REST API on your server. Check https://dev.cublex.net/wiki/corlex/rest
debug-mode: false # enable/disable DEBUG mode

# Variables (as placeholders) that are used in the localization strings
prefix: "#ffc13b[Cub] &r"
test-prefix: "&c[Cub] &r"
discord: "https://discord.gg/cublex" # Chat and announcements
telegram: "https://t.me/mcublex" # Announcements and replies
```

---

## 🧪 Example API Usage

```java
CorlexAPI api = Bukkit.getServicesManager().load(CorlexAPI.class);
api.send(player, "corlex.welcome", Map.of("world", "Wilderness"));
```
Because corlex.welcome has {prefix}, {player} and {world} placeholders. First 2 is filled by the program, and the last one given by the call.
```
corlex:
  welcome: "{prefix}Welcome, {player}! You are in {world}."
```
---

## 📦 Roadmap

- /cub log recent command
- Client-side item-lore support for extreme localization
- GUI API (paged layouts, close/back buttons, border styles)
- Auto-merging of locale files on update
- Plugin usage metrics
- Announcement API (Game, Discord)
- AI module
- Use arguments in the permission-error message. Interaction or command error
- Format commands help into clickable TextComponents
- Paginate the output of help command for more commands
- Auto-suggest when the user types an invalid command
- Add force-default-language boolean

---

## 📣 Need Help?

This plugin is designed to be extended by other plugins. If you’re building something with Cub, open an issue or contact the developer for help or contributions.

---

> Work smarter, build harder.

NOTES ON GUI MODULE:
1- if 6 lines, have a border in slots [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53]
2- if 6 lines and multi page, have a border, and slots 50, 49 and 48 are for Previous page, Close this page, Next page buttons.
3- if 6 lines and single page, slot 49 is Close this page button
4- if 5, 4 or 2 lines ERROR
5- if 3 lines, have option to have border or not. single page or multi page. slots 23, 22, 21 are buttons.
6- if single line and single page, no border, all 9 slots are custom



