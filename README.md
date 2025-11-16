# Task Management System

This project requires Java 17 (OpenJDK 17) to build and run.

If your environment uses an older Java version, the build will fail with a message from the Maven Enforcer plugin. Follow the instructions in `docs/JDK-setup.md` to install or configure JDK 17.

Quick start:

- Install JDK 17 (or use the helper script): `./scripts/setup-java.sh`
- Build: `mvn -B clean install`
- Run: `mvn spring-boot:run`

See `docs/JDK-setup.md` for more installation options and troubleshooting tips.
