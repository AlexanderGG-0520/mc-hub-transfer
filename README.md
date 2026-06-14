# mc-hub-transfer

mc-hub-transfer is a dedicated-server-side Fabric mod that adds `/hub`.

Singleplayer and integrated servers are not supported. If the mod is loaded
outside a dedicated server environment, functional behavior is disabled with a
clear message.

## Support

- Loader: Fabric
- Minecraft: 26.1.2
- Mod version: 0.1.0
- Environment: dedicated server only

## Config

The config file is stored at:

```text
config/mc-hub-transfer.json
```

The file is created with defaults on dedicated server startup if it does not
already exist. Invalid config values fail clearly instead of silently falling
back to broken values.

Example:

```json
{
  "hub": {
    "java": {
      "host": "play.alec-ofc.com",
      "port": 25565
    },
    "bedrock": {
      "host": "play.alec-ofc.com",
      "port": 19132
    }
  },
  "javaTransferFailureMessage": "Unable to transfer you to the Java hub at {endpoint}. Please try again later.",
  "bedrockReconnectMessage": "Bedrock players must reconnect to {endpoint}; automatic transfer is not supported."
}
```

Hosts must not be blank, ports must be in `1..65535`, and messages must not be
blank.

Messages support these placeholders:

- `{host}`
- `{port}`
- `{endpoint}`

## Usage

```text
/hub
```

Java players are transferred to the configured Java hub using Minecraft's
server-side `transfer` command.

Bedrock players detected through Floodgate are shown the configured Bedrock
reconnect message instead, because automatic transfer is not supported for that
path. If Floodgate is not installed, players are treated as Java players.

## Admin Commands

Config management requires the built-in admin command permission.

```text
/hubtransfer config reload
/hubtransfer config show
/hubtransfer config set javaHost <host>
/hubtransfer config set javaPort <port>
/hubtransfer config set bedrockHost <host>
/hubtransfer config set bedrockPort <port>
/hubtransfer config set javaTransferFailureMessage <message>
/hubtransfer config set bedrockReconnectMessage <message>
/hubtransfer config save
```

Setter commands update the active runtime config immediately. `/hub` uses those
new values without a restart. Runtime changes are not written to disk until an
admin runs `/hubtransfer config save`; `/hubtransfer config reload` replaces the
active runtime config with the file contents.

## License

This project is licensed under the GNU General Public License version 3.0 or later (`GPL-3.0-or-later`). See [LICENSE](LICENSE) for the GPL v3 license text.

You may modify and redistribute this project under the GPL. If you distribute a modified build, you must also make the corresponding modified source code available under `GPL-3.0-or-later`.

For public modified builds, publishing the corresponding modified source code in a public Git repository is the recommended practical way to satisfy this source-availability requirement.
