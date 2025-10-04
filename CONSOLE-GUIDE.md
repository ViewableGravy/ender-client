# In-Game Console Guide

## Accessing the Console

**Key**: Press **`:`** (colon) to open the console

The console will appear at the bottom of the screen with a `:` prompt.

## Using the Console

1. **Open**: Press `:` (colon)
2. **Type command**: Enter your command and arguments
3. **Execute**: Press `Enter`
4. **Cancel**: Press `Esc` or `Backspace` on empty line
5. **History**: Use `Up/Down` arrow keys to navigate command history

## Farming Bot Test Commands

### Create Rectangular Test Field

```
:farmtest <gridX> <gridY> [name]
```

**Example:**
```
:farmtest 100 200 MyTestField
```

Creates a rectangular test field at grid coordinates (100, 200) with the name "MyTestField".

**Notes:**
- Grid coordinates are shown in the coordinate display (upper-left corner)
- Name is optional (defaults to "Test Field")
- Fields are 10x10 grid units by default

### Create Circular Test Field

```
:farmtestcircle <gridX> <gridY> <radius> [name]
```

**Example:**
```
:farmtestcircle 150 250 5 CircleField
```

Creates a circular test field centered at grid (150, 250) with radius 5 grid units.

**Notes:**
- Radius is in grid units (1 grid unit = 11 world units)
- Name is optional (defaults to "Test Circle")

### Clear All Test Fields

```
:farmclear
```

Removes all test fields from the farming manager. Use this to clean up your tests.

## Testing Workflow

1. **Log into the game** and wait for the world to load
2. **Check your coordinates** in the upper-left corner (shows World and Grid coords)
3. **Open console** by pressing `:`
4. **Create a test field** at your current location:
   ```
   :farmtest 100 200 TestField1
   ```
5. **Toggle overlay** to see the field rendered:
   - Open the menu (gear icon or default key)
   - Find "Farming Overlay" in custom actions
   - Click to toggle on/off
6. **Create more fields** to test different shapes and locations:
   ```
   :farmtestcircle 120 220 8 CircleTest
   ```
7. **Verify rendering** - Fields should appear as colored outlines on the map
8. **Clean up** when done:
   ```
   :farmclear
   ```

## Other Useful Console Commands

### Built-in Commands

- `:afk` - Set AFK status
- `:act <action> [args...]` - Perform an action
- `:belt f` - Switch to F-key belt
- `:belt n` - Switch to number key belt

## Tips

- **Use Tab completion** if available
- **Command history** persists across sessions
- **Case sensitive** - commands are lowercase
- **Arguments** are space-separated
- **Quotes** may be needed for names with spaces (untested)

## Troubleshooting

### Console won't open
- Make sure you're in the game world (not login screen)
- Try pressing Shift+: if : alone doesn't work
- Check that another UI element doesn't have focus

### Command not found
- Check spelling - commands are case-sensitive
- Make sure you rebuilt the client after adding commands
- Try `:help` or type invalid command to see available commands

### Test fields not visible
- Make sure the farming overlay is toggled ON
- Check that you're using grid coordinates, not world coordinates
- Verify fields were created with `:farmclear` then recreate

### Coordinates not showing
- Wait for map data to load ("Loading map data..." message will clear)
- The coordinate display appears in the upper-left corner
- If still not visible, there may be a rendering issue

## Example Testing Session

```
# 1. Open console
:

# 2. Create a rectangular field at current location
:farmtest 95.5 103.2 RectTest

# 3. Create a circle nearby
:farmtestcircle 100 110 7.5 CircleTest

# 4. Toggle overlay on (use menu)
# Fields should now be visible as colored outlines

# 5. Clean up when done
:farmclear
```

### Farming Configuration Window
```
:farmconfig
```
Opens the farming configuration window where you can manage fields, crop settings, and storage.

## Advanced: Adding Custom Commands

To add your own console commands, edit `GameUI.java` in the `cmdmap` initialization block:

```java
cmdmap.put("mycommand", new Console.Command() {
    public void run(Console cons, String[] args) {
        // Your command implementation
        cons.out.println("Command executed!");
    }
});
```

Then rebuild with `ant jar` and `ant bin`.
