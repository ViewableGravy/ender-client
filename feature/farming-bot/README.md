# Farming Bot Feature - Task Index

This directory contains the complete specification and task breakdown for the Autonomous Farming Bot feature for the Ender client.

## 📁 Directory Structure

```
feature/farming-bot/
├── README.md                          # This file - index and navigation
├── 00-OVERVIEW.md                     # Feature overview and architecture
├── ROADMAP.md                         # Project timeline and milestones
├── BRANCHING-STRATEGY.md              # Git workflow and branching guide
├── TESTING-STRATEGY.md                # Testing procedures and QA guidelines
│
├── epic1-field-data-structures/       # Epic 1: 10 stories
│   ├── 1.1-field-data-model.md
│   ├── 1.2-field-type-enums.md
│   ├── 1.3-grid-coordinate-system.md
│   └── ...
│
├── epic2-overlay-system/              # Epic 2: 10 stories
│   ├── 2.1-overlay-layer.md
│   └── ...
│
├── epic3-field-editor-ui/             # Epic 3: 10 stories
├── epic4-tile-detection/              # Epic 4: 12 stories
├── epic5-quantity-bot/                # Epic 5: 13 stories
├── epic6-quality-bot/                 # Epic 6: 14 stories
├── epic7-orchestration/               # Epic 7: 13 stories
└── epic8-polish-monitoring/           # Epic 8: 13 stories
```

## 📋 Core Documentation

### Planning Documents
- **[00-OVERVIEW.md](./00-OVERVIEW.md)** - Feature overview, goals, architecture, and success criteria
- **[ROADMAP.md](./ROADMAP.md)** - Project timeline, milestones, resource allocation, and release strategy

### Workflow & Process
- **[BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md)** - Git branching, commit conventions, PR guidelines
- **[TESTING-STRATEGY.md](./TESTING-STRATEGY.md)** - Testing procedures, QA checklists, automated testing

## 🎯 Epic Breakdown (8 Epics, 95+ Stories)

| Epic | Folder | Story Count | Estimate | Priority |
|------|--------|-------------|----------|----------|
| **Epic 1** | [epic1-field-data-structures/](./epic1-field-data-structures/) | 10 stories | 105 SP (~13 days) | P0 |
| **Epic 2** | [epic2-overlay-system/](./epic2-overlay-system/) | 10 stories | 114 SP (~14 days) | P0 |
| **Epic 3** | [epic3-field-editor-ui/](./epic3-field-editor-ui/) | 10 stories | 156 SP (~20 days) | P0 |
| **Epic 4** | [epic4-tile-detection/](./epic4-tile-detection/) | 12 stories | 180 SP (~23 days) | P0 |
| **Epic 5** | [epic5-quantity-bot/](./epic5-quantity-bot/) | 13 stories | 172 SP (~22 days) | P0 |
| **Epic 6** | [epic6-quality-bot/](./epic6-quality-bot/) | 14 stories | 230 SP (~29 days) | P0 |
| **Epic 7** | [epic7-orchestration/](./epic7-orchestration/) | 13 stories | 190 SP (~24 days) | P1 |
| **Epic 8** | [epic8-polish-monitoring/](./epic8-polish-monitoring/) | 13 stories | 224 SP (~28 days) | P2 |

**Total**: 95 stories, **~1,371 story points**, **~171 working days** (~34 weeks with 1 developer)

## 📝 File Status Tracking

Each story file uses a naming convention to indicate its current status:

| Extension | Status | Meaning |
|-----------|--------|---------|
| `.md` | **Not Started** | Task is defined but work hasn't begun |
| `.in-progress.md` | **In Progress** | Currently being worked on |
| `.done.md` | **Complete** | Implementation finished and merged |

### Workflow Example:
```bash
# Starting work on a story
mv 1.3-grid-coordinate-system.md 1.3-grid-coordinate-system.in-progress.md
git add . && git commit -m "FB-1.3: Start work on grid coordinate system"

# Completing the story
mv 1.3-grid-coordinate-system.in-progress.md 1.3-grid-coordinate-system.done.md
git add . && git commit -m "FB-1.3: Complete grid coordinate system implementation"
```

### Quick Status Check:
```bash
# See all in-progress stories
find feature/farming-bot -name "*.in-progress.md"

# See all completed stories
find feature/farming-bot -name "*.done.md"

# Count completed vs total
echo "Completed: $(find feature/farming-bot -name '*.done.md' | wc -l) / 95"
```

---

## Development Phases

### Phase 1: Foundation (Weeks 1-4)
Build field management, overlay, and UI systems.
- ✅ Epic 1: Field Data Structures
- ✅ Epic 2: Custom Overlay System
- ✅ Epic 3: Field Editor & Configuration UI

**Deliverable**: Users can define, visualize, and configure fields

---

## 🚀 Getting Started

### For Developers

1. **Read the Architecture**
   - Start with [00-OVERVIEW.md](./00-OVERVIEW.md) for system architecture
   - Review [BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md) for Git workflow

2. **Pick a Story**
   - Navigate to an epic folder (e.g., `epic1-field-data-structures/`)
   - Choose a story that's not `.in-progress.md` or `.done.md`
   - Mark it as in-progress: `mv story.md story.in-progress.md`

3. **Implement**
   - Follow acceptance criteria in the story file
   - Refer to technical notes and code examples
   - Create files listed in "Files to Create/Modify"

4. **Test**
   - Consult [TESTING-STRATEGY.md](./TESTING-STRATEGY.md)
   - Run manual tests from the story's "Testing Notes"
   - Add unit tests if applicable

5. **Complete**
   - Mark story as done: `mv story.in-progress.md story.done.md`
   - Commit with story ID: `git commit -m "FB-X.Y: Story title"`
   - See [BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md) for commit conventions

### For Project Managers
1. Review [ROADMAP.md](./ROADMAP.md) for timeline and resource allocation
2. Review [00-OVERVIEW.md](./00-OVERVIEW.md) for scope and success criteria
3. Import stories from epic folders into Jira/Trello
4. Track progress using file status (`.done.md` count / total stories)

### For QA/Testers
1. Read [TESTING-STRATEGY.md](./TESTING-STRATEGY.md) for comprehensive test procedures
2. Review acceptance criteria in each story for test cases
3. Consult epic folders for integration test scenarios
4. Report bugs against specific story IDs (e.g., "FB-1.3")

---

## Development Phases

### Phase 1: Foundation (Weeks 1-4)
Build field management, overlay, and UI systems.
- ✅ Epic 1: Field Data Structures
- ✅ Epic 2: Custom Overlay System
- ✅ Epic 3: Field Editor & Configuration UI

**Deliverable**: Users can define, visualize, and configure fields

---

### Phase 2: Core Bot Logic (Weeks 5-9)
Implement autonomous farming for quantity and quality fields.
- ✅ Epic 4: Tile Detection & Basic Actions
- ✅ Epic 5: Quantity Field Bot
- ✅ Epic 6: Quality Field Bot

**Deliverable**: Fully autonomous single-field farming bots

---

### Phase 3: Multi-Field System (Weeks 10-11)
Enable multi-field management with intelligent scheduling.
- ✅ Epic 7: Multi-Field Orchestration & Scheduler

**Deliverable**: System managing 10+ fields simultaneously

---

### Phase 4: Production Ready (Weeks 12-13)
Polish, optimize, document, and release.
- ✅ Epic 8: Polish, Monitoring & External Preparation

**Deliverable**: Production v1.0 release

---

## Story Naming Convention

Stories are named with the pattern: `FB-{Epic}.{Story}`

**Examples**:
- `FB-1.1` = Epic 1, Story 1 (Create Field Data Model)
- `FB-5.8` = Epic 5, Story 8 (Implement Harvesting Logic)
- `FB-7.2` = Epic 7, Story 2 (Create Farming Scheduler Core)

**File names**: `{Epic}.{Story}-{short-description}.md`
- `1.1-field-data-model.md`
- `5.8-harvesting-logic.md`
- `7.2-scheduler-core.md`

---

## Story Point Estimates

**Note**: 1 Story Point ≈ 2 hours of work

| Points | Complexity | Typical Duration |
|--------|-----------|------------------|
| 2-3 | Trivial | 4-6 hours (half day) |
| 5 | Simple | 10 hours (1-1.5 days) |
| 8 | Moderate | 16 hours (2 days) |
| 13 | Complex | 26 hours (3+ days) |
| 21 | Very Complex | 42 hours (5+ days) |
| 34 | Extra Complex | 68 hours (1+ weeks) |

---

## Priority Levels

| Priority | Description | SLA |
|----------|-------------|-----|
| **P0** | Blocker - Must have for MVP | Complete before dependent epics |
| **P1** | High - Important for v1.0 | Include in release if time permits |
| **P2** | Medium - Nice to have | Can defer to v1.1 |
| **P3** | Low - Future enhancement | Defer to v2.0 |

---

## Epic Dependencies

```
Epic 1 (Foundation)
  ├─> Epic 2 (Overlay)
  ├─> Epic 3 (UI)
  └─> Epic 4 (Actions)
        ├─> Epic 5 (Quantity Bot)
        └─> Epic 6 (Quality Bot)
              └─> Epic 7 (Scheduler)
                    └─> Epic 8 (Polish)
```

**Critical Path**: Epic 1 → 4 → 5 → 7 → 8 (minimum 7.5 weeks)

---

## 📖 Documentation Reference

### Planning & Strategy
- **[00-OVERVIEW.md](./00-OVERVIEW.md)** - Feature architecture, components, goals, success criteria
- **[ROADMAP.md](./ROADMAP.md)** - Timeline, sprints, resource allocation, release strategy
- **[BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md)** - Git workflow, commit conventions, PR templates
- **[TESTING-STRATEGY.md](./TESTING-STRATEGY.md)** - Test procedures, QA checklists, automation guide

### Epic Folders (Individual Stories)
- **[epic1-field-data-structures/](./epic1-field-data-structures/)** - Core data models and persistence
- **[epic2-overlay-system/](./epic2-overlay-system/)** - Visual field overlay rendering
- **[epic3-field-editor-ui/](./epic3-field-editor-ui/)** - Field configuration interface
- **[epic4-tile-detection/](./epic4-tile-detection/)** - Tile scanning and basic bot actions
- **[epic5-quantity-bot/](./epic5-quantity-bot/)** - Autonomous quantity farming logic
- **[epic6-quality-bot/](./epic6-quality-bot/)** - Quality breeding automation
- **[epic7-orchestration/](./epic7-orchestration/)** - Multi-field scheduling system
- **[epic8-polish-monitoring/](./epic8-polish-monitoring/)** - UI polish, stats, external API

---

## 🔧 Development Workflow

### Daily Workflow
```bash
# 1. Update feature branch
git checkout feature/farming-bot
git pull origin feature/farming-bot

# 2. Pick a story from an epic folder
cd epic1-field-data-structures
ls *.md  # Find stories not in-progress or done

# 3. Mark story as in-progress
mv 1.1-field-data-model.md 1.1-field-data-model.in-progress.md
git add . && git commit -m "FB-1.1: Start field data model implementation"

# 4. Implement the story
# ... code changes ...

# 5. Test (see TESTING-STRATEGY.md)
ant clean && ant  # Rebuild
# Manual tests from story file

# 6. Commit (simple story - direct to feature branch)
git add .
git commit -m "FB-1.1: Implement FarmField data model

- Add FarmField.java with all required properties
- Implement validation, equals, hashCode
- Add Javadoc documentation"

git push origin feature/farming-bot

# 7. Mark story as done
mv 1.1-field-data-model.in-progress.md 1.1-field-data-model.done.md
git add . && git commit -m "FB-1.1: Complete field data model"
git push origin feature/farming-bot
```

### Complex Story Workflow (13+ SP)
```bash
# Create story branch for complex work
git checkout -b feature/farming-bot/FB-1.3-grid-coordinate-system

# Make iterative commits
git commit -m "FB-1.3: Add rectangular grid support"
git commit -m "FB-1.3: Add circular grid boundary detection"
git commit -m "FB-1.3: Add coordinate conversion utilities"

# Push and create PR
git push origin feature/farming-bot/FB-1.3-grid-coordinate-system
# Create PR on GitHub/GitLab: story branch → feature/farming-bot

# After review and approval, merge
git checkout feature/farming-bot
git merge --no-ff feature/farming-bot/FB-1.3-grid-coordinate-system
git push origin feature/farming-bot
```

See [BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md) for complete workflow details.

---

## 📊 Progress Tracking

### Check Status
```powershell
# Count completed stories per epic
Get-ChildItem -Path epic* -Recurse -Filter "*.done.md" | Group-Object Directory

# Overall progress
$total = 95
$done = (Get-ChildItem -Path epic* -Recurse -Filter "*.done.md").Count
Write-Host "Progress: $done / $total stories ($([math]::Round($done/$total*100, 1))%)"

# Find in-progress work
Get-ChildItem -Path epic* -Recurse -Filter "*.in-progress.md"
```

### Bash/Linux
```bash
# Count completed stories
find epic* -name "*.done.md" | wc -l

# Overall progress
echo "Progress: $(find epic* -name '*.done.md' | wc -l) / 95"

# Find in-progress work
find epic* -name "*.in-progress.md"
```

---

## Using This Documentation

### For Project Managers
1. Start with [ROADMAP.md](./ROADMAP.md) for timeline and milestones
2. Review [00-OVERVIEW.md](./00-OVERVIEW.md) for scope and success criteria
3. Import stories from epic folders into Jira/Trello
4. Track progress against sprint goals in roadmap

### For Developers
1. Read [00-OVERVIEW.md](./00-OVERVIEW.md) for architecture overview
2. Read [BRANCHING-STRATEGY.md](./BRANCHING-STRATEGY.md) for Git workflow
3. Pick stories from epic folders (not `.in-progress.md` or `.done.md`)
4. Follow acceptance criteria and technical notes in each story
5. Reference files to create/modify listed in each story

### For QA/Testers
1. Read [TESTING-STRATEGY.md](./TESTING-STRATEGY.md) for test procedures
2. Review acceptance criteria in each story for test cases
3. Consult epic folders for integration test scenarios
4. Report bugs against specific story IDs (e.g., "FB-1.3")

### For Stakeholders
1. [00-OVERVIEW.md](./00-OVERVIEW.md) - Understand feature value and goals
2. [ROADMAP.md](./ROADMAP.md) - Track timeline and milestones
3. Epic folders - Review deliverables and success criteria

---

## Story File Structure

Each story file contains:
- **Story ID and metadata**: Points, priority, epic, status
- **Description**: Brief summary of what to implement
- **Acceptance Criteria**: Checklist of requirements
- **Technical Notes**: Implementation details, code examples
- **Files to Create/Modify**: Exact file paths
- **Dependencies**: Other stories that must be complete first
- **Testing Notes**: How to manually test
- **Definition of Done**: Completion checklist
- **Dependencies & Blocks**: What's needed and what this enables
- **Technical Debt**: Known limitations and future improvements
- **Testing Strategy**: Approach for validating the epic

Each story contains:
- **Story Points**: Effort estimate
- **Priority**: P0-P3 classification
- **Acceptance Criteria**: Specific, testable requirements
- **Technical Notes**: Implementation guidance and examples
- **Files to Create/Modify**: Specific file paths for implementation

---

## Getting Started

### Week 1 Action Items
1. ✅ Review all documentation (you're here!)
2. ⬜ Set up project tracking (Trello/Jira) with all stories
3. ⬜ Assign team members to epics
4. ⬜ Schedule Sprint 1 planning meeting
5. ⬜ Begin Epic 1, Story 1.1 (Field Data Model)

### First Sprint Goals (Sprint 1: Weeks 1-2)
- Complete Epic 1 (all 10 stories)
- Start Epic 2 (stories 2.1-2.5)
- Deliverable: Field data model with serialization working

---

## Questions or Issues?

If you encounter:
- **Ambiguous requirements**: Flag in story comments, discuss with team
- **Technical blockers**: Escalate to Lead Developer
- **Scope questions**: Consult with Project Manager
- **Dependencies unclear**: Review epic dependency graph in roadmap

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Oct 4, 2025 | Initial complete specification |

---

## Contributing

When adding new stories or epics:
1. Follow existing naming conventions (FB-{Epic}.{Story})
2. Include all required sections (acceptance criteria, technical notes, etc.)
3. Update this index and roadmap
4. Assign story points and priority
5. Document dependencies

---

**Total Documentation**: 10 files, ~50,000 words, 95+ stories  
**Estimated Project Duration**: ~34 weeks with 1 developer, ~11-17 weeks with 3-4 developers  
**Total Story Points**: ~1,371 points (1 SP ≈ 2 hours)  
**Team Size**: 3-4 developers + 1 QA (recommended)  

Ready to build an amazing autonomous farming bot! 🚜🌾
