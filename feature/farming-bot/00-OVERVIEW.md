# Farming Bot - Feature Overview

## Executive Summary
This feature implements a fully autonomous farming bot for the Ender client that can manage crop cultivation across multiple fields without player intervention. The system supports both quality-focused breeding operations and quantity-focused mass production, with intelligent crop quality management, automated planting/harvesting cycles, and multi-field orchestration.

## Project Goals
1. **Autonomous Operation**: Enable completely AFK farming with minimal setup
2. **Quality Management**: Intelligently breed crops to increase quality over time
3. **Quantity Production**: Mass-produce crops for consumption/trade
4. **Flexible Configuration**: Support various field shapes, crop types, and strategies
5. **Multi-Field Support**: Manage multiple fields simultaneously with smart scheduling
6. **External Monitoring**: Prepare architecture for future external visualization tools

## Key Features
- **Custom Overlay System**: Grid-based field definition without requiring in-game markers
- **Shape Support**: Rectangles and circles (with subdivision into quadrants/segments)
- **Quality Breeding**: Automatic quality tracking and selective replanting (+0 to +5 quality range)
- **Quantity Farming**: Relaxed quality requirements (-4 to +5) for mass production
- **Smart Inventory**: Multi-chest support for input/output with quality-based sorting
- **Field States**: Track tilling, planting, growing, and harvest-ready states
- **Auto-Maintenance**: Handle field preparation, watering, and tool management
- **Safety Features**: Pause on obstacles, notifications for critical events, resume capability

## Technical Architecture

### Core Components
1. **Field Management System** (`src/auto/farming/`)
   - Field definition, storage, and rendering
   - Grid-based coordinate system
   - Shape primitives (rectangle, circle with subdivisions)

2. **Bot Engine** (`src/auto/farming/bot/`)
   - Quality field bot (selective breeding)
   - Quantity field bot (mass production)
   - Multi-field scheduler
   - State machine for field lifecycle

3. **Inventory System** (`src/auto/farming/inventory/`)
   - Chest detection and linking
   - Seed/crop quality sorting
   - Tool management
   - Resource tracking

4. **UI Components** (`src/auto/farming/ui/`)
   - Farming configuration window
   - Field editor with overlay
   - Statistics and monitoring panel
   - xTended menu integration

5. **Data Persistence** (`etc/farming.json5`)
   - Field definitions
   - Crop configurations
   - Quality thresholds
   - Chest assignments

### Integration Points
- **Widget System**: New farming config window extending `Window` class
- **MapView Overlay**: Custom renderer similar to minesweeper overlay
- **xTended Menu**: New `paginae/add/farming/*` actions
- **Existing Bot Actions**: Leverage pathfinding, auto-pickup patterns from `auto/Actions.java`
- **Gob System**: Crop detection and quality inspection via `Gob` attributes

## Development Phases

### Phase 1: Foundation & UI (Epics 1-3)
- Field data structures and overlay system
- Configuration UI and field editor
- xTended menu integration
- Data persistence

### Phase 2: Tile Operations (Epic 4)
- Tile state detection
- Basic farming actions (till, plant, harvest)
- Pathfinding integration
- Tool management

### Phase 3: Bot Logic (Epics 5-6)
- Quantity field bot implementation
- Quality field bot with breeding logic
- Crop quality detection and comparison
- Inventory management

### Phase 4: Multi-Field System (Epic 7)
- Field scheduler
- Priority queue system
- Safety and error handling
- Pause/resume mechanisms

### Phase 5: Polish & Monitoring (Epic 8)
- Statistics tracking
- Notification system
- Performance optimization
- External monitoring preparation

## Success Criteria
- [ ] Bot can autonomously farm a single rectangular field from tilling to harvest
- [ ] Quality fields maintain or increase average crop quality over 10 cycles
- [ ] Quantity fields maximize throughput with minimal downtime
- [ ] Bot handles 5+ simultaneous fields without performance issues
- [ ] No crashes or stuck states during 24-hour continuous operation
- [ ] Configuration persists correctly across client restarts
- [ ] Overlay renders clearly without interfering with gameplay
- [ ] Notifications alert user to critical issues (storage full, seeds depleted)

## Dependencies
- Existing pathfinding system (`auto/Actions.java`)
- Gob attribute system for crop inspection
- MapView rendering pipeline
- Widget/Window UI framework
- JSON configuration system (Gson)

## Risks & Mitigations
1. **Game Mechanics Unknown**: Farming mechanics may differ from assumptions
   - *Mitigation*: Early prototype with single field to validate mechanics
   
2. **Performance**: Overlay rendering may impact FPS on large fields
   - *Mitigation*: Implement culling and LOD for distant fields
   
3. **Pathfinding Failures**: Bot may get stuck on obstacles
   - *Mitigation*: Implement robust error detection and recovery
   
4. **Quality Detection**: May not be able to read crop quality accurately
   - *Mitigation*: Research existing crop inspection code early in Phase 2

## Future Enhancements (Out of Scope)
- External monitoring dashboard (web/desktop app)
- Machine learning for optimal planting patterns
- Cross-field quality optimization
- Automatic field creation and expansion
- Integration with other bot systems (animal husbandry, crafting)
- Market integration for automatic selling

## Team Roles
- **Lead Developer**: Overall architecture and core systems
- **UI Developer**: Field editor, config window, overlay rendering
- **Bot Logic Developer**: Quality/quantity bot algorithms
- **Integration Developer**: xTended menu, pathfinding, inventory systems
- **QA/Testing**: Validation, performance testing, edge cases

## Timeline Estimate
- **Phase 1**: 2-3 weeks
- **Phase 2**: 1-2 weeks  
- **Phase 3**: 2-3 weeks
- **Phase 4**: 1-2 weeks
- **Phase 5**: 1-2 weeks
- **Total**: 7-12 weeks for full implementation

## References
- Ring of Brodgar Wiki: https://ringofbrodgar.com/
- Existing Bot Actions: `src/auto/Actions.java`
- Minesweeper Overlay: `src/me/ender/minimap/MineSweeper.java`
- xTended Menu System: `src/haven/MenuGrid.java` (initCustomPaginae)
- Gob Info System: `src/me/ender/gob/GobInfoOpts.java`
