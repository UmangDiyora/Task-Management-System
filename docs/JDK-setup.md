# JDK 17 setup for this project

This project requires Java 17 or newer (Spring Boot 3.2+). To avoid build failures, follow one of these options:

- Install OpenJDK 17 (Ubuntu):

  ```bash
  sudo apt-get update
  sudo apt-get install -y openjdk-17-jdk
  ```

- Use SDKMAN:

  ```bash
  curl -s "https://get.sdkman.io" | bash
  source "$HOME/.sdkman/bin/sdkman-init.sh"
  sdk install java 17.0.16-open
  sdk use java 17.0.16-open
  ```

- Use the helper script (configures current shell or persists to `~/.bashrc`):

  ```bash
  chmod +x scripts/setup-java.sh
  ./scripts/setup-java.sh        # configures current shell
  ./scripts/setup-java.sh --persist  # also appends to ~/.bashrc
  ```

- If you prefer a per-project tool, add a `.sdkmanrc` file or configure your devcontainer to install JDK 17.

Why we added a guard

The build now includes the Maven Enforcer plugin which verifies the Java version early and fails with a clear message if the JDK is incompatible. This prevents confusing compiler errors from downstream plugins.
