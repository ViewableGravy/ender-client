# Branching Strategy - Farming Bot Feature

## Overview
This document outlines the Git branching strategy for the Farming Bot feature development. The strategy balances parallel work, code review, and feature isolation while keeping the repository organized.

## Branch Naming Conventions

### Feature Branch
**Pattern**: `feature/farming-bot`  
**Purpose**: Main development branch for the entire farming bot feature  
**Lifespan**: Created at project start, merged to `master` when Epic 8 complete  
**Protection**: Require code review before merging to master

### Story Branches (Optional)
**Pattern**: `feature/farming-bot/FB-X.Y-short-description`  
**Examples**:
- `feature/farming-bot/FB-1.1-field-data-model`
- `feature/farming-bot/FB-3.5-field-editor-canvas`
- `feature/farming-bot/FB-6.8-quality-breeding-logic`

**Purpose**: Isolate work on individual stories that require multiple commits  
**When to use**:
- Story has 13+ story points (complex work)
- Story will take multiple days
- Multiple developers working on related stories (avoid conflicts)
- Story requires experimentation/iteration

**When NOT to use**:
- Simple stories (≤5 SP) that can be done in 1-2 commits
- Quick bug fixes or documentation updates

## Workflow

### Option 1: Direct Commit to Feature Branch (Simple Stories)
For simple stories (≤8 SP, 1-2 commits):

```bash
# Work directly on feature branch
git checkout feature/farming-bot
git pull origin feature/farming-bot

# Make changes for story FB-1.2
# ... edit files ...

# Commit with story ID prefix
git add .
git commit -m "FB-1.2: Implement field type enums

- Add FieldType enum (QUALITY, QUANTITY)
- Add FieldShape enum (RECTANGLE, CIRCLE)
- Add FieldState enum with overlay colors
- Include display names and descriptions"

git push origin feature/farming-bot
```

**Commit Message Format**:
```
FB-X.Y: <Brief description>

- Bullet point 1
- Bullet point 2
- Bullet point 3
```

### Option 2: Story Branch (Complex Stories)
For complex stories (13+ SP, multiple commits):

```bash
# Create story branch from feature branch
git checkout feature/farming-bot
git pull origin feature/farming-bot
git checkout -b feature/farming-bot/FB-1.3-grid-coordinate-system

# Work in iterations
# Iteration 1: Basic rectangle grid
git add src/auto/farming/model/FieldGrid.java
git commit -m "FB-1.3: Add basic rectangular grid support"

# Iteration 2: Circular grid
git add src/auto/farming/model/FieldGrid.java
git commit -m "FB-1.3: Add circular grid boundary checking"

# Iteration 3: Grid utilities
git add src/auto/farming/util/GridUtils.java
git commit -m "FB-1.3: Add grid coordinate conversion utilities"

# Push story branch
git push origin feature/farming-bot/FB-1.3-grid-coordinate-system

# Create pull request: FB-1.3 story branch → feature/farming-bot
# After review and approval, merge to feature branch
git checkout feature/farming-bot
git merge --no-ff feature/farming-bot/FB-1.3-grid-coordinate-system
git push origin feature/farming-bot

# Delete story branch after merge
git branch -d feature/farming-bot/FB-1.3-grid-coordinate-system
git push origin --delete feature/farming-bot/FB-1.3-grid-coordinate-system
```

## Epic Milestones

### Epic Completion
At the end of each epic, create a milestone tag:

```bash
git checkout feature/farming-bot
git tag -a epic1-complete -m "Epic 1: Field Data Structures - Complete"
git push origin epic1-complete
```

**Tag naming**: `epic1-complete`, `epic2-complete`, etc.

## Pull Request Guidelines

### Story Branch → Feature Branch PRs
**Title**: `[FB-X.Y] Story Title`  
**Example**: `[FB-1.3] Create Grid-Based Coordinate System`

**PR Description Template**:
```markdown
## Story
FB-X.Y: Story Title

## Changes
- Summary of changes
- Files modified/created

## Testing
- How to test the changes
- Manual test results

## Checklist
- [ ] All acceptance criteria met
- [ ] Code compiles without errors
- [ ] Javadoc complete
- [ ] Manual testing passed
- [ ] No compiler warnings
```

### Feature Branch → Master PR
**Title**: `[Feature] Farming Bot - Complete`  
**When**: After Epic 8 complete and full system tested

## Code Review Requirements

### For Story Branches
- **Required**: At least 1 reviewer approval
- **Reviewers**: Lead developer or peer
- **Focus**: Code quality, acceptance criteria, testing

### For Feature Branch → Master
- **Required**: All team members review
- **Reviewers**: Lead dev, QA, product owner
- **Focus**: Full integration, documentation, user acceptance

## Working with File Status

### Mark File Status
Update file extension to track work status:

```bash
# Starting work on a story
cd feature/farming-bot/epic1-field-data-structures/
mv 1.3-grid-coordinate-system.md 1.3-grid-coordinate-system.in-progress.md
git add .
git commit -m "FB-1.3: Start work on grid coordinate system"

# Completing story
mv 1.3-grid-coordinate-system.in-progress.md 1.3-grid-coordinate-system.done.md
git add .
git commit -m "FB-1.3: Complete grid coordinate system implementation"
```

**File states**:
- `.md` = Not started
- `.in-progress.md` = Currently working
- `.done.md` = Completed and merged

## Conflict Resolution

### If Conflicts Occur
```bash
# Update feature branch with latest changes
git checkout feature/farming-bot
git pull origin feature/farming-bot

# Rebase story branch onto updated feature branch
git checkout feature/farming-bot/FB-X.Y-story-name
git rebase feature/farming-bot

# Resolve conflicts manually
# ... edit conflicting files ...

git add .
git rebase --continue
git push origin feature/farming-bot/FB-X.Y-story-name --force-with-lease
```

## Emergency Hotfix

If a critical bug is found in master during feature development:

```bash
# Create hotfix branch from master
git checkout master
git pull origin master
git checkout -b hotfix/critical-bug-description

# Fix and commit
git add .
git commit -m "Hotfix: Fix critical bug description"

# Merge to master
git checkout master
git merge --no-ff hotfix/critical-bug-description
git push origin master

# Merge hotfix into feature branch to avoid regression
git checkout feature/farming-bot
git merge master
git push origin feature/farming-bot

# Delete hotfix branch
git branch -d hotfix/critical-bug-description
```

## Best Practices

1. **Commit Often**: Small, focused commits are easier to review and revert
2. **Pull Before Push**: Always `git pull` before pushing to avoid conflicts
3. **Descriptive Messages**: Use story ID prefix (FB-X.Y) in all commits
4. **One Story Per Commit/Branch**: Don't mix multiple stories in one commit
5. **Clean Up Branches**: Delete story branches after merging
6. **Test Before Commit**: Ensure code compiles and basic tests pass
7. **Review Your Own Code**: Use `git diff` before committing

## Branch Protection Rules (GitHub/GitLab)

### `master` branch
- Require pull request reviews (2 approvals)
- Require status checks to pass
- Restrict push access
- Require signed commits (optional)

### `feature/farming-bot` branch
- Require pull request reviews (1 approval)
- Allow force push by admins only
- Require linear history (no merge commits for stories)

## Example Timeline

```
Week 1-2: Epic 1
  ├─ FB-1.1 → direct commit to feature/farming-bot
  ├─ FB-1.2 → direct commit to feature/farming-bot
  ├─ FB-1.3 → story branch (complex)
  ├─ FB-1.4 → story branch (complex)
  └─ Tag: epic1-complete

Week 3-4: Epic 2
  ├─ FB-2.1 → direct commit
  ├─ FB-2.5 → story branch (complex)
  └─ Tag: epic2-complete

...

Week 30: Feature Complete
  └─ PR: feature/farming-bot → master (full team review)
```

## Tools & Tips

### Useful Git Aliases
```bash
# Add to ~/.gitconfig
[alias]
  fb = "!f() { git checkout -b feature/farming-bot/FB-$1; }; f"
  fbc = "!git checkout feature/farming-bot"
  st = status -sb
  lg = log --oneline --graph --all --decorate
```

Usage:
```bash
git fb 1.3-grid-system  # Creates feature/farming-bot/FB-1.3-grid-system
git fbc                 # Switches to feature/farming-bot
```

### VS Code Git Integration
- Use GitLens extension for inline blame and history
- Use Git Graph extension to visualize branches
- Configure auto-fetch to stay updated

---

**Questions?** Contact the lead developer or refer to the main README.
