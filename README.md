# Chat Optimizer

A Fabric mod for Minecraft 1.21.1 that improves the built-in chat with quality-of-life features.

All settings are configurable through a tabbed screen accessible from **Options → Chat Settings → Chat Optimizer**.

## Features

**Timestamps** — Adds a timestamp to every chat message. Choose between 12-hour and 24-hour format, pick a color, and select square brackets, round brackets, or no brackets.

**Duplicate Collapsing** — When the same message is sent multiple times in a row, they are merged into a single line with a repeat count (e.g. `x4`) instead of flooding your chat.

**Chat Filtering** — Block messages from specific players or containing specific keywords. Manage entries from the Filters tab in the config screen.

**Chat Logging** — Saves all incoming chat messages to a dated log file at `.minecraft/logs/chat/chat-YYYY-MM-DD.log`. Disabled by default.

**Message Search** — Press a configurable keybind while chat is open to bring up a search bar that filters visible messages in real time.

**History Trimming** — Caps the number of entries stored in your chat history (the up-arrow recall list) to keep it from growing indefinitely.

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.19.2+ |
| Fabric API | 0.104.0+1.21.1+ |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest `chatoptimizer-1.0.0.jar` from [Modrinth](https://modrinth.com/mod/chat-optimizer)
4. Place both JARs in your `.minecraft/mods` folder

## Building from Source

```bash
git clone https://github.com/arnavtejasvi/even-better-chat.git
cd even-better-chat
./gradlew build
```

The compiled JAR will be at `build/libs/chatoptimizer-1.0.0.jar`.

## License

MIT
