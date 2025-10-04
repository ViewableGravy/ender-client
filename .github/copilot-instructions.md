# Ender Client - Haven & Hearth Custom Client

## Project Overview
This is a **modified Java client** for the Haven & Hearth MMORPG game. The codebase is a fork that extends the base game client ("loftar's client") with custom enhancements, automation features, and quality-of-life improvements. The project is known as the "Ender client" (identified by `config.client-id=ender-beta`).

**Base Architecture**: Java 8 game client with OpenGL rendering (JOGL/LWJGL), custom UI widget system, network protocol handling, and resource loading system.

## Critical File Structure

### Core Entry Points
- **`src/haven/MainFrame.java`**: Main entry point (`main()` function), window management, fullscreen handling
- **`src/haven/Session.java`**: Network protocol handler, server connection, message routing
- **`src/haven/GameUI.java`**: Main game UI container, holds all game widgets (map, inventory, menus)
- **`src/haven/Widget.java`**: Base class for ALL UI components using custom widget tree system

### Custom Enhancement Code
- **`src/me/ender/`**: All Ender-specific modifications and enhancements
  - Quality-of-life features, custom overlays, automation helpers
  - `ClientUtils.java`: Common utilities, resource name prettification
  - `minimap/`: Enhanced minimap with auto-markers and minesweeper overlay
  - `gob/`: Game object (Gob) enhancements
- **`src/auto/`**: Bot/automation actions and helpers
- **`src/integrations/`**: Third-party integrations (e.g., mapping client)

### Resource System
- **`src/haven/Resource.java`**: Core resource loading (images, 3D models, audio, data files)
- **`resources/`**: Custom client resources, compiled into `client-res.jar`
  - `resources/java/`: Java code for custom resource layers
  - `resources/src/`: Source assets (images, data)
  - `etc/`: JSON configuration files for features (radar icons, auto-marking, treatments, etc.)

## Build System (Apache Ant)

**Primary Commands** (run with `ant` from project root):
- **`ant`** (default): Full build - compiles code, downloads dependencies, creates JARs, builds `bin/` directory
- **`ant clean`**: Remove all build artifacts (including `lib/ext/` downloads)
- **`ant run`**: Build and launch the client (connects to official server)
- **`ant jar`**: Build just `build/hafen.jar` without resources
- **`ant resources`**: Compile custom resources into `client-res.jar`

**Key Build Details**:
- Java 8 source/target (uses `source="1.8" target="1.8"`)
- External dependencies auto-downloaded to `lib/ext/` from `http://www.havenandhearth.com/java/`
  - JOGL (OpenGL), LWJGL, Steamworks4j
  - Downloaded once, cached locally unless `lib/ext/jogl/has-jogl` removed
- Two output targets:
  - `build/hafen.jar`: Development build (requires local server)
  - `bin/`: Production build with all resources and official server config

## Widget System Architecture

**Widget Tree Pattern**: Custom hierarchical UI system (NOT Swing/AWT, though it uses AWT for window)
- All UI components extend `Widget` with parent/child relationships
- Widgets created dynamically from server messages via `@RName` factories
- **Factory Pattern**: `@RName("name")` annotation + `Factory` interface for server-driven instantiation

```java
@RName("inv")  // Server sends "inv" message to create inventory
public static class $_ implements Factory {
    public Widget create(UI ui, Object[] args) { ... }
}
```

**Key Widget Subclasses**:
- `GameUI`: Main game container
- `MapView`: 3D game world view
- `Inventory`, `Equipory`: Item containers
- `Window`: Draggable windows
- `MenuGrid`: Action menu grid

## Game Object (Gob) System

**Gobs** represent all entities in the game world (players, animals, items, buildings):
- **`src/haven/Gob.java`**: Core game object with position, rendering, attributes
- **Overlay System**: Dynamic visual/functional layers on Gobs (`Gob.Overlay`)
- **GAttrib**: Attribute system for Gob properties (movement, health, etc.)
- **Custom Gob Enhancements** in `src/me/ender/gob/`: Timers, kin tracking, custom info overlays

## Network Protocol

**Session-based protocol**:
- `Session.java`: Manages connection, handles `PMessage`/`RMessage` packets
- `Connection.java`: Low-level socket handling, message queueing
- Message types defined as constants: `MSG_SESS`, `MSG_REL`, `MSG_OBJDATA`, etc.
- Protocol version: `PVER = 29`

## Resource Loading

**Dual-source resource system**:
1. **Remote resources**: Downloaded from `haven.resurl` (game server)
2. **Local/custom resources**: Bundled in `client-res.jar` and `builtin-res.jar`

Resources identified by name paths (e.g., `"gfx/kritter/horse/stallion"`) and version numbers.

## Configuration System

**Multi-layer config** (`Config.java`, `CFG.java`):
- JAR properties: `etc/ansgar-config.properties` bundled in JAR
- User preferences: Stored in user home directory (`~/.haven/`)
- Command-line args via `Config.cmdline()`
- **CFG class**: Type-safe config values with persistence (e.g., `CFG.SHOW_MINESWEEPER_OVERLAY`)

## xTended Menu System (Custom Actions)

The **xTended menu** (`paginae/add/*`) is a critical custom feature system providing enhanced actions and toggles. All xTended menu items are defined in `MenuGrid.initCustomPaginae()` and backed by the `Action` enum.

### Key Menu Categories:

**Automation/Bot Actions** (`paginae/add/auto/*`):
- `AGGRO_ONE_PVE`/`AGGRO_ONE_PVP`: Auto-target nearest creature/player
- `AGGRO_ALL`: Aggro all nearby non-party creatures
- `BOT_MOUNT_HORSE`: Whistle and mount nearest domestic horse
- `BOT_PICK_ALL_HERBS`: Auto-pickup herbs/mussels/clay in configurable radius
- `BOT_CIRCLE_WALK`: Walk in small circular motion

**Gob Info Toggles** (`paginae/add/info/*`):
- `TOGGLE_GOB_INFO_PLANTS`: Show crop growth stages
- `TOGGLE_GOB_INFO_TREE_GROWTH`/`TREE_CONTENT`: Tree maturity/contents
- `TOGGLE_GOB_INFO_HEALTH`: Object health overlays
- `TOGGLE_GOB_INFO_QUALITY`: Quality info display
- `TOGGLE_GOB_INFO_BARREL`/`CHEESE`/`SIGN`: Container-specific info
- All use `GobInfoOpts.InfoPart` enum for state tracking

**Quick Access** (`paginae/add/*`):
- `OPEN_QUICK_CRAFT`: Searchable crafting list
- `OPEN_QUICK_BUILD`: Searchable building list
- `OPEN_QUICK_ACTION`: Searchable action list
- `OPEN_CRAFT_DB`/`OPEN_ALCHEMY_DB`: Database windows

**Equipment Actions** (`paginae/add/equip/*`):
- `EQUIP_BOW`/`EQUIP_SPEAR`: Auto-equip two-handed weapons
- `EQUIP_SWORD_N_BOARD`: Auto-equip sword + shield

**Utility Actions**:
- `FUEL_SMELTER_9`/`FUEL_SMELTER_12`: Auto-fuel with coal
- `FUEL_OVEN_4`: Auto-fuel with branches
- `CLEAR_PLAYER_DAMAGE`/`CLEAR_ALL_DAMAGE`: Reset damage overlays
- `TOGGLE_TIMERS`: Show/hide timer panel

### Adding New xTended Menu Items:

1. **Define action in `Action.java`**:
   ```java
   MY_ACTION(gui -> myImplementation(gui), "Display Name", "Tooltip description")
   ```

2. **Register in `MenuGrid.initCustomPaginae()`**:
   ```java
   makeLocal("paginae/add/my_category/my_action", Action.MY_ACTION);
   // OR with toggle state indicator:
   makeLocal("paginae/add/my_toggle", Action.MY_ACTION, () -> CFG.MY_SETTING.get());
   ```

3. **Create resource icon** in `resources/src/local/paginae/add/` (image files)

4. **For complex actions**: Implement in `src/auto/Actions.java` or `src/me/ender/`

### Custom Pagina Architecture:

- `CustomPagina`: Extends `MenuGrid.Pagina` for client-side menu items
- `CustomPagButton`: Handles rendering (including toggle state overlay) and interaction
- `CustomPaginaAction`: Functional interface for action execution
- Toggle states show visual indicator (ON/OFF overlay) with audio feedback

## Code Style (from README)

- **Indentation**: 4 spaces
- **Tabs**: Advance to next column divisible by 8 (classical tab stops)
- **Line endings**: LF (Unix-style)
- **Naming**: Avoid camelCase when possible (preference for underscores)

## Common Patterns

**Resource name matching**: Use `ResName.java` constants and regex patterns (e.g., `ResName.BARREL_WITH_CONTENTS`)
**JSON config loading**: Via Gson, files in `etc/` packaged into JARs (radar.json, automark.json5, etc.)
**Reactor pattern**: RxJava 1.x for reactive updates (`haven.rx.Reactor`)
**Custom rendering**: Use `Gob.SetupMod` or custom `Sprite` implementations
**Action enum pattern**: All custom actions defined in `Action.java` enum with functional interface `Do`
**Toggle indicators**: Use `makeLocal()` with `Supplier<Boolean>` for visual toggle state on menu items

## Development Workflow

1. **Make code changes** in `src/`
2. **Run `ant`** to build (or `ant jar` for quick code-only rebuild)
3. **Test with `ant run`** or execute `java -jar bin/hafen.jar`
4. **Merge upstream changes**: Regularly merge from "loftar's" base client (see changelog)
5. **Clean builds**: If encountering unexplained issues, try `ant clean` then `ant`

### Feature Development & Story Tracking

**Story Completion Pattern**:
- User stories are tracked in `feature/farming-bot/epic*/` directories as markdown files
- When a story is **completed**, rename the file from `.md` to `.done.md` (e.g., `2.3-implement-field-boundary-rendering.md` → `2.3-implement-field-boundary-rendering.done.md`)
- The `.done.md` suffix indicates the story meets all acceptance criteria and definition of done
- This pattern allows filtering completed vs. in-progress stories easily

**Story File Naming Convention**:
- Format: `<epic>.<story>-<description>.md` (e.g., `2.3-implement-field-boundary-rendering.md`)
- Completed: `<epic>.<story>-<description>.done.md`
- Stories contain: Story ID, Epic, Story Points, Priority, Status, Description, Acceptance Criteria, Technical Notes, Definition of Done

**Branch Strategy** (from feature/farming-bot directory):
- `feature/farming-bot`: Main feature branch
- `feature/farming-bot-epic<N>`: Epic-specific branches (e.g., `feature/farming-bot-epic1`, `feature/farming-bot-epic2`)
- Epic branches are merged to main feature branch when complete but **NOT deleted** (preserved for reference)
- See `BRANCHING-STRATEGY.md` for details

## External Dependencies

**Libraries** (auto-downloaded):
- JOGL: OpenGL bindings
- LWJGL: Alternative rendering/input
- Steamworks4j: Steam integration
- Gson: JSON parsing (in `lib/gson-2.8.6.jar`)
- RxJava 1.1.5: Reactive programming

## Key Gotchas

- **Java version**: Must compile with Java 8 compatibility
- **Resource versioning**: Resources cached by name+version; version changes trigger reloads
- **Widget lifecycle**: Widgets added to UI tree trigger `added()`, removed trigger `destroy()`
- **Thread safety**: UI operations must run on UI thread; use `Defer.later()` for async work
- **Message parsing**: Be careful with `Message.eom()` checks when parsing network messages
