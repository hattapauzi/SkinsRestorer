# Agent specs (this fork)

Companion Forge 1.12.2 work. Not opened as an upstream issue.

| Doc | What |
|---|---|
| [Design](specs/2026-09-21-forge-1.12.2-companion-design.md) | Why a nested `forge-1.12/` project, one platform seam, non-goals |
| [Plan A — bootstrap](plans/2026-09-21-forge-1.12.2-bootstrap.md) | Gradle project, login restore, skin apply packets |
| [Plan B — commands](plans/2026-09-21-forge-1.12.2-commands.md) | Cloud → Forge `ICommand` |
| [Plan C — GUI / network](plans/2026-09-21-forge-1.12.2-gui-and-network.md) | Chest GUI, skulls, `sr:messagechannel` |

Implement A → B → C. Do not `include()` `forge-1.12` in the parent Gradle build.
