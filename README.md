# mc-hub-transfer

mc-hub-transfer is a client-side Fabric mod that adds `/transfer`-like behavior for Minecraft clients in pre-1.20.5 environments.

## Support

- Loader: Fabric
- Minecraft: 1.20.1
- Mod version: 0.1.0

Forge, NeoForge, Quilt, and multi-version support are not included in v0.1.0.

## Usage

```text
/transfer example.com
/transfer example.com 25565
```

If the port is omitted, the mod uses `25565`.

This is client-side `/transfer`-like behavior for environments before Minecraft 1.20.5. It disconnects the client from the current server and starts a normal client connection to the requested server.

This is not the same as the official Minecraft 1.20.5+ server transfer packet. The command is handled locally by the client and is not sent to the server.

## License

This project is licensed under the GNU General Public License version 3.0 or later (`GPL-3.0-or-later`). See [LICENSE](LICENSE) for the GPL v3 license text.

You may modify and redistribute this project under the GPL. If you distribute a modified build, you must also make the corresponding modified source code available under `GPL-3.0-or-later`.

For public modified builds, publishing the corresponding modified source code in a public Git repository is the recommended practical way to satisfy this source-availability requirement.
