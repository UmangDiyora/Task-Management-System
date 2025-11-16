#!/usr/bin/env bash
# Small helper to configure Java 17 for this user/session.
# Usage: ./scripts/setup-java.sh [--persist]
# --persist will append exports to ~/.bashrc (asks for confirmation)

set -euo pipefail
JDK_PATHS=("/usr/lib/jvm/java-17-openjdk-amd64" "/usr/lib/jvm/java-17-openjdk" "/usr/lib/jvm/java-17-openjdk-amd64/" )
# Also include SDKMAN-managed JDK locations (user or system install)
JDK_PATHS+=("$HOME/.sdkman/candidates/java/current" "/usr/local/sdkman/candidates/java/current" "${SDKMAN_CANDIDATES_DIR:-}/java/current")
FOUND=""
for p in "${JDK_PATHS[@]}"; do
  if [ -d "$p" ]; then
    FOUND="$p"
    break
  fi
done

if [ -z "$FOUND" ]; then
  echo "Could not find a JDK 17 installation in the usual locations."
  echo "Please install OpenJDK 17 (apt: openjdk-17-jdk) or set JAVA_HOME manually."
  exit 1
fi

echo "Using JDK at: $FOUND"
export JAVA_HOME="$FOUND"
export PATH="$JAVA_HOME/bin:$PATH"

if [ "${1:-}" = "--persist" ]; then
  echo "This will append JAVA_HOME and PATH export lines to ~/.bashrc. Continue? [y/N]"
  read -r ans
  if [[ "$ans" =~ ^[Yy]$ ]]; then
    if ! grep -q "JAVA_HOME=$FOUND" ~/.bashrc 2>/dev/null; then
      printf "\n# Use OpenJDK 17 for this workspace\nexport JAVA_HOME=%s\nexport PATH=\"$JAVA_HOME/bin:\$PATH\"\n" "$FOUND" >> ~/.bashrc
      echo "Appended export lines to ~/.bashrc"
    else
      echo "~/.bashrc already contains JAVA_HOME for this path"
    fi
  else
    echo "Aborted persistence. You can run this script without --persist to configure current shell only."
  fi
fi

echo "java version: $(java -version 2>&1 | head -n 1)"
echo "javac version: $(javac -version 2>&1)"

echo "Done. If you used --persist, open a new shell or run: source ~/.bashrc" 
