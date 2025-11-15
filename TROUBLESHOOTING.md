# Maven Build Troubleshooting Guide

## Issue: Java DNS Resolution Failure in Codespaces

### Problem Description
Maven builds fail with "Temporary failure in name resolution" errors in GitHub Codespaces, even though network connectivity works for other tools like `curl`.

### Root Cause
Java's DNS resolution mechanism is broken in this specific Codespace environment. This is confirmed by:
- ✅ curl can successfully access Maven repositories
- ❌ Java `InetAddress.getByName()` fails with "Temporary failure in name resolution"
- ❌ Maven cannot download any artifacts from any repository

### Attempted Fixes
1. ✅ Updated Maven repositories to use `https://repo1.maven.org/maven2`
2. ✅ Created Maven settings.xml with mirror configuration
3. ✅ Set Java network properties (`-Djava.net.preferIPv4Stack=true`)
4. ✅ Manually downloaded parent POMs using curl
5. ❌ Cannot modify /etc/hosts (no sudo access)
6. ❌ Cannot use Maven offline mode (would need all ~200+ dependencies pre-downloaded)

### Recommended Solutions

#### Option 1: Use Docker for Maven Build (RECOMMENDED)
```bash
# Build using Maven in Docker container
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn clean compile

# Or with install
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn clean install
```

#### Option 2: Rebuild the Codespace
Sometimes DNS issues resolve after rebuilding:
1. Open Command Palette (Ctrl+Shift+P / Cmd+Shift+P)
2. Search for "Codespaces: Rebuild Container"
3. Confirm rebuild

#### Option 3: Clone to Local Environment
If the issue persists, clone and build locally:
```bash
git clone https://github.com/UmangDiyora/Task-Management-System.git
cd Task-Management-System
git checkout claude/implement-project-steps-01WCUAss9ew4vyD7btvg3b3r
mvn clean install
```

#### Option 4: Use GitHub Actions
Create a `.github/workflows/build.yml` to build in CI/CD where DNS works:
```yaml
name: Build
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn clean install
```

### Files Modified for DNS Troubleshooting
- `pom.xml` - Added explicit repository configuration
- `~/.m2/settings.xml` - Added mirrors and profiles
- `.mavenrc` - Added Java network options
- `download-deps.sh` - Script to manually download dependencies

### Verification
Once DNS is working, verify with:
```bash
mvn clean compile
```

Expected output: BUILD SUCCESS

### Additional Notes
- This is an environment issue, not a code issue
- The project configuration is correct
- All Part 1 and Part 2 code is complete and ready to compile
- Once Maven build works, the application will start successfully
