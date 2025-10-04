# Building and Running the Ender Client

This guide covers the complete process for building and running the Haven & Hearth Ender client from source.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Building the Client](#building-the-client)
- [Running the Client](#running-the-client)
- [Troubleshooting](#troubleshooting)
- [Development Workflow](#development-workflow)

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 8**
   - The project requires JDK 8 (not just JRE)
   - JRE only includes the runtime; JDK includes the compiler (`javac`) needed for building
   
2. **Apache Ant**
   - Build automation tool used by this project
   - Handles compilation, resource packaging, and dependency management

### Installation (Windows with Scoop)

If you're using [Scoop](https://scoop.sh/) package manager on Windows:

```powershell
# Install JDK 8
scoop install openjdk8-redhat

# Install Apache Ant
scoop install ant
```

**Verify Installation:**
```powershell
# Check Java version (should show JDK 8)
java -version

# Check Ant installation
ant -version
```

### Important: JDK vs JRE

⚠️ **Common Issue**: If you only have JRE installed, the build will fail with errors like:
- "Unable to find a javac compiler"
- "Cannot find javac executable"

**Solution**: Install a full JDK (Java Development Kit), not just JRE (Java Runtime Environment).

## Initial Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ender-client
```

### 2. Set JAVA_HOME Environment Variable

The build process needs to know where your JDK is installed.

**For Scoop installations (temporary, per session):**
```powershell
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
```

**For permanent setup (Windows):**
1. Open System Properties → Advanced → Environment Variables
2. Add a new System Variable:
   - Variable name: `JAVA_HOME`
   - Variable value: Path to your JDK (e.g., `C:\Users\<username>\scoop\apps\openjdk8-redhat\current`)
3. Restart your terminal/IDE

**Verify JAVA_HOME:**
```powershell
echo $env:JAVA_HOME
# Should show path to JDK directory
```

## Building the Client

### Full Build (Recommended for First Time)

The full build downloads dependencies, compiles code, packages resources, and creates the `bin/` directory with everything needed to run:

```powershell
# Set JAVA_HOME (if not set permanently)
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"

# Run full build
ant
```

**What this does:**
- Downloads external dependencies (JOGL, LWJGL, Steamworks) to `lib/ext/`
- Downloads game resources (`builtin-res.jar`, `hafen-res.jar`)
- Compiles all Java source files (398+ files)
- Compiles custom resources from `resources/`
- Creates `build/hafen.jar` (main application)
- Creates `build/client-res.jar` (custom resources)
- Copies everything to `bin/` directory for distribution

**Expected output:**
```
BUILD SUCCESSFUL
Total time: 15-20 seconds (first build may take longer)
```

### Quick Build (Code Changes Only)

If you only changed Java source code and want a faster rebuild:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
ant jar
```

This only recompiles changed `.java` files and updates `build/hafen.jar` (~8 seconds).

⚠️ **Note**: This does NOT update the `bin/` directory. Use for quick testing from `build/` during development only.

### Clean Build

If you encounter unexplained build issues, do a clean build:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
ant clean
ant
```

This removes all build artifacts (including downloaded dependencies) and rebuilds from scratch.

## Running the Client

### Critical: Run from bin/ Directory

⚠️ **Important**: The client MUST be run from the `bin/` directory, not `build/`.

**Why?**
- `build/hafen.jar` contains only compiled code
- `bin/` contains the complete package: code + all game resources
- Missing resources cause errors like "Failed to load resource gfx/loginscr"

### Launch Command

```powershell
java -jar bin\hafen.jar
```

**Alternative (from bin directory):**
```powershell
cd bin
java -jar hafen.jar
```

### Successful Launch

When running correctly, you should see:
1. The Haven & Hearth login screen appears
2. No errors in console
3. No `error.log` file created in `bin/`

## Troubleshooting

### Build Fails: "Cannot find javac compiler"

**Problem**: JAVA_HOME points to JRE instead of JDK, or not set.

**Solution**:
```powershell
# Check if JAVA_HOME is set
echo $env:JAVA_HOME

# Set it to JDK location
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"

# Or install JDK if you only have JRE
scoop install openjdk8-redhat
```

### Runtime Error: "Failed to load resource gfx/loginscr"

**Problem**: Running from `build/` directory instead of `bin/`.

**Solution**:
```powershell
# Run full build first
ant

# Then run from bin/
java -jar bin\hafen.jar
```

### Build Warnings

The build typically shows 16-20 warnings. These are normal and can be ignored:
- Unchecked generic type operations
- Deprecated API usage
- Unused variables/imports

As long as you see `BUILD SUCCESSFUL`, the warnings are cosmetic.

### Check Error Logs

If the client crashes, check for error logs:

**Windows:**
```powershell
# In build/ directory (if run from build/)
cat build\error.log

# In bin/ directory (if run from bin/)
cat bin\error.log

# User config directory
cat $env:USERPROFILE\.haven\error.log
```

### Port Already in Use

If you get port binding errors, another instance may be running:
```powershell
# Windows: Find process using port
netstat -ano | findstr :1870

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

## Development Workflow

### Typical Development Cycle

1. **Make code changes** in `src/`
2. **Quick rebuild** (if only code changed):
   ```powershell
   $env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
   ant jar
   ```
3. **Test** from build directory:
   ```powershell
   java -jar build\hafen.jar
   ```
4. **Full build** when ready to distribute:
   ```powershell
   ant
   ```
5. **Run** from bin directory:
   ```powershell
   java -jar bin\hafen.jar
   ```

### Resource Changes

If you modify resources in `resources/src/`:
```powershell
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
ant resources
```

This rebuilds `client-res.jar` with your custom resources.

### Shortcut: Combined Build and Run

Create a PowerShell script `run.ps1`:
```powershell
#!/usr/bin/env pwsh
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
& "$env:USERPROFILE\scoop\apps\ant\current\bin\ant.bat"
if ($LASTEXITCODE -eq 0) {
    java -jar bin\hafen.jar
}
```

Then run:
```powershell
.\run.ps1
```

### Ant Not in PATH

If `ant` command isn't found, use the full path:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"
& "$env:USERPROFILE\scoop\apps\ant\current\bin\ant.bat"
```

Or add Ant to your PATH permanently.

## Build Targets Reference

The `build.xml` file defines several targets:

| Target | Command | Description | Build Time |
|--------|---------|-------------|------------|
| `deftgt` (default) | `ant` | Full build: downloads deps, compiles, packages, creates bin/ | 15-20s |
| `jar` | `ant jar` | Compile code only, create hafen.jar | 6-8s |
| `resources` | `ant resources` | Compile custom resources to client-res.jar | 3-5s |
| `clean` | `ant clean` | Remove all build artifacts | 1-2s |
| `jars` | `ant jars` | Build both hafen.jar and client-res.jar | 8-10s |
| `bin` | `ant bin` | Create bin/ directory with all files | 1-2s |

## Directory Structure After Build

```
ender-client/
├── build/              # Build output (development)
│   ├── hafen.jar      # Main application JAR
│   ├── client-res.jar # Custom resources
│   ├── classes/       # Compiled .class files
│   └── error.log      # Runtime errors (if any)
│
├── bin/               # Distribution directory (REQUIRED to run)
│   ├── hafen.jar      # Main application
│   ├── client-res.jar # Custom resources
│   ├── builtin-res.jar   # Core game resources
│   ├── hafen-res.jar     # Official game resources
│   ├── jogl-*.jar     # OpenGL libraries
│   ├── lwjgl-*.jar    # LWJGL libraries
│   ├── gluegen-*.jar  # Native bindings
│   ├── steamworks4j.jar  # Steam integration
│   ├── *-natives-*.jar   # Platform-specific natives
│   └── config files
│
├── lib/ext/           # Downloaded external dependencies
│   ├── builtin-res.jar
│   ├── hafen-res.jar
│   └── jogl/, lwjgl/, etc.
│
└── src/               # Source code
```

## Common Build Commands Summary

```powershell
# Set JAVA_HOME (required for every new terminal session)
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"

# First time build
ant

# Quick rebuild after code changes
ant jar

# Full rebuild
ant clean && ant

# Rebuild resources only
ant resources

# Run the client
java -jar bin\hafen.jar
```

## System Requirements

- **OS**: Windows, Linux, or macOS
- **Java**: JDK 8 (OpenJDK or Oracle)
- **RAM**: 2GB minimum, 4GB recommended
- **Disk**: ~500MB for dependencies and build artifacts
- **Graphics**: OpenGL 2.0+ compatible graphics card

## Getting Help

If you encounter issues not covered here:

1. Check `build/error.log` or `bin/error.log` for runtime errors
2. Review Ant build output for compilation errors
3. Verify JAVA_HOME points to a JDK (not JRE)
4. Try a clean build: `ant clean && ant`
5. Check that you're running from `bin/` directory

## Additional Resources

- **Project README**: `README` - General project information
- **Changelog**: `changelog.txt` - Version history and changes
- **License**: `COPYING` - GPL-3 license information
- **Farming Bot Docs**: `feature/farming-bot/` - Feature-specific documentation
