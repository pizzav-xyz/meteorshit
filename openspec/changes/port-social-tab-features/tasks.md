## 0. Verify Meteor Addon API Surface (do this FIRST)

- [x] 0.1 Decompile/inspect current Meteor Client 26.2-SNAPSHOT `System<T>` and `Systems` classes to confirm whether addons can register systems that `Systems.get()` returns, and whether addon systems hook into Meteor's save/load cycle
- [x] 0.2 Decompile/inspect current `Tabs` class to confirm whether addons can register a custom `Tab` (resolves the social-tab UI surface question)
- [x] 0.3 Decompile/inspect current `Command`, `ChatUtils`, `NbtUtils`, `PlayerHeadUtils`, `MeteorExecutor`, `Http`, `Friends`, `Config` classes for signature changes since MC 1.21.8 and addon-visibility
- [x] 0.4 Record findings; if any API differs materially from assumptions, update design.md decisions before proceeding to porting tasks
- [ ] 0.5 Commit: `git commit -m "docs: verify Meteor addon API surface for social port"`

## 1. Addon Project Setup (blocked until task 0 complete)

- [x] 1.1 Copy `meteor-addon-template` structure into a new addon project directory (or confirm `meteor-addon-template/` itself is to be repurposed as the addon root — confirm with user before proceeding)
- [x] 1.2 Update `gradle/libs.versions.toml` mod-version, package metadata as needed
- [x] 1.3 Rename base package from `com.example.addon` to `com.example.addon.social` (or chosen final package) and update `fabric.mod.json` entrypoint accordingly
- [x] 1.4 Update `fabric.mod.json` id, name, description, authors to reflect the social addon
- [ ] 1.5 Verify the template builds unmodified first (`./gradlew build`) before porting any code
- [ ] 1.6 Commit: `git commit -m "build: scaffold social addon project from template"`

## 2. Port Alt Tracker (requires task 0 findings)

- [x] 2.1 Create `AltAccount` class (port from fork, adjust imports/package per task 0 findings, verify NBT API signatures)
- [x] 2.2 Create `AltTracker` system class (port add/remove/merge/query/display logic)
- [x] 2.3 Wire `AltTracker` instantiation and load/save lifecycle into `AddonTemplate.onInitialize()` per resolved persistence approach from task 0.1
- [x] 2.4 Port `AltCommand` (link/unlink/list subcommands) and register via `Commands.add(...)`
- [x] 2.5 Port `OnlineAltsCommand` and register via `Commands.add(...)`
- [x] 2.6 Write unit tests for `AltAccount` (merge, display text formatting, equals/hashCode) and `AltTracker` (add/remove/promote-main/query) using plain JUnit (no Minecraft runtime needed for pure logic paths)
- [ ] 2.7 Commit: `git commit -m "feat: port AltTracker system and alt/online-alts commands"`

## 3. Port Scary People (requires task 0 findings)

- [x] 3.1 Create `ScaryPerson` class (port from fork, adjust imports/package per task 0 findings)
- [x] 3.2 Create `ScaryPeople` system class (port add/remove/query/list logic)
- [x] 3.3 Wire `ScaryPeople` instantiation and load/save lifecycle into `AddonTemplate.onInitialize()` per resolved persistence approach from task 0.1
- [x] 3.4 Port `ScaryPeopleCommand` (add/remove/list subcommands) and register via `Commands.add(...)`
- [x] 3.5 Write unit tests for `ScaryPerson` (equals/hashCode/compareTo) and `ScaryPeople` (add/remove/duplicate rejection/query)
- [ ] 3.6 Commit: `git commit -m "feat: port ScaryPeople system and scary-people command"`

## 4. Port Blacklisted People (requires task 0 findings)

- [x] 4.1 Create `BlacklistedPerson` class (port from fork, adjust imports/package per task 0 findings)
- [x] 4.2 Create `BlacklistedPeople` system class (port add/remove/query/list logic)
- [x] 4.3 Wire `BlacklistedPeople` instantiation and load/save lifecycle into `AddonTemplate.onInitialize()` per resolved persistence approach from task 0.1
- [x] 4.4 Port `BlacklistedPeopleCommand` (add/remove/list subcommands, including `blacklist`/`bl` aliases) and register via `Commands.add(...)`
- [x] 4.5 Write unit tests for `BlacklistedPerson` and `BlacklistedPeople` (mirroring scary-people test coverage)
- [ ] 4.6 Commit: `git commit -m "feat: port BlacklistedPeople system and blacklist command"`

## 5. Port Social Colors Utility (requires task 0 findings)

- [x] 5.1 Create `SocialColorUtils` class with default colors and priority-ordered `getPlayerSocialColor` methods, adapted to reference the addon's own `AltTracker`/`ScaryPeople`/`BlacklistedPeople` instances (BetterTab coupling removed per design.md)
- [x] 5.2 Expose configurable colors via addon-owned settings (a dedicated `Module` with color settings) since direct `BetterTab` modification is out of scope
- [x] 5.3 Write unit tests for color priority resolution (self > scary > blacklisted > alt > friend > team > default) and null-safety
- [ ] 5.4 Commit: `git commit -m "feat: port SocialColorUtils with addon-owned color settings"`

## 6. Port Social Management UI (single path, determined by task 0.2)

- [x] 6.1 Based on task 0.2 findings, pick ONE path: if `Tabs.add()` is exposed to addons, port `SocialTab`/`SocialScreen` (adjust imports, wire to addon-owned `ScaryPeople`/`BlacklistedPeople` instances); otherwise implement the social management screen as a Module settings screen or keybind-accessed screen. No conditional branching at implementation time — one concrete path.
- [ ] 6.2 Verify add/remove/list controls function against the addon's ported systems
- [ ] 6.3 Verify async info refresh (name/head texture resolution) triggers correctly after add
- [ ] 6.4 Commit: `git commit -m "feat: port social management UI (${path chosen})"`

## 7. Integration and Verification (MANDATORY gates — change is NOT complete until all pass)

- [x] 7.1 Wire all systems' save calls to persist reliably (every mutating command call triggers save, per design.md risk mitigation) rather than relying solely on shutdown hook
- [x] 7.2 Run `./gradlew build` and confirm the addon JAR builds cleanly
- [x] 7.3 Run all unit tests (`./gradlew test`) and confirm they pass
- [ ] 7.4 **MANDATORY: Load the built JAR in a real Minecraft client with Meteor Client installed and join an actual server or LAN world.** Manually verify `.alt link/unlink/list`, `.online-alts`, `.scarypeople add/remove/list`, `.blacklist add/remove/list` against real players — no mocked network calls.
- [ ] 7.5 **MANDATORY: Exercise the live Mojang HTTP path.** While in-game, add a player to AltTracker/ScaryPeople/BlacklistedPeople and confirm the async name+head-texture resolution completes and displays correctly — this is the real web-request requirement for the HTTP layer (api.mojang.com / sessionserver.mojang.com).
- [ ] 7.6 Manually verify the social management UI add/remove interactions in the live client
- [ ] 7.7 Manually verify NBT persistence survives a client restart (add entries, restart, confirm they reload)
- [ ] 7.8 Fix any issues found during the mandatory verification gates before the change is considered complete
- [ ] 7.9 Commit: `git commit -m "fix: resolve issues from mandatory live-client verification"`

## 8. Documentation

- [ ] 8.1 Update the addon's README.md with setup instructions and a feature list (alt tracking, scary people, blacklist, social UI)
- [ ] 8.2 Note in README any known limitation (e.g., no native BetterTab tab-list recoloring without a mixin, per design.md risk)
- [ ] 8.3 Commit: `git commit -m "docs: add social addon README and known limitations"`