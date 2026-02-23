Unmanned Online Tick Stasis (无人在线时刻暂停)

A lightweight server-side mod for Minecraft Forge 1.20.1 that automatically freezes the game world when no players are online and resumes seamlessly when a player joins, helping save server resources and maintain consistent gameplay.

Key Features

- Automatic Pause/Resume: Pauses block updates, entity movements, redstone devices, in-game time and more when the last player leaves; resumes instantly on player join.

- Fully Configurable: Toggle pause function, set post-logout pause delay, and whitelist allowed console commands (supports wildcards).

- State Preservation: Records and restores world time, entity positions and motion states (optional).

- Multilingual Support: Includes English (en_us) and Chinese (zh_cn).

- Lightweight & Compatible: Server-side only (no client installation needed); works for Minecraft 1.20.1 (Forge 47.x.x) and 1.20.x.

How It Works

Uses Mixin to intercept the world tick loop, canceling ticks when no players are online (after configured delay). Monitors player events and filters console commands against the whitelist.

Configuration

Stored in serverconfig/unmannedonlinetickstasis-server.toml (or config/ for dedicated servers); editable manually or via /reload (with permissions).

- enablePause (boolean, default: true): Toggle the pause feature.

- delaySeconds (int, default: 0): Pause delay after last player leaves.

- commandWhitelist (string list): Allowed console commands (supports * wildcard).

Example: enablePause = true; delaySeconds = 30; commandWhitelist = ["say", "list", "stop", "help*"]

Installation

1. Install Minecraft Forge 1.20.1 (recommended: 47.4.10+).

2. Place the mod .jar in the server’s mods folder.

3. Start the server (no extra setup needed).

Note: Server-side only; clients don’t need installation.

Dependencies

Minecraft Forge 1.20.1 ([47, 48)), Minecraft 1.20.x
