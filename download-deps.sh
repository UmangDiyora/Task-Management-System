#!/bin/bash
set -e

echo "Downloading Maven dependencies manually using curl..."

# Base URL
MAVEN_CENTRAL="https://repo1.maven.org/maven2"
M2_REPO="$HOME/.m2/repository"

# Function to download a file
download_artifact() {
    local group_id=$1
    local artifact_id=$2
    local version=$3
    local packaging=${4:-jar}

    # Convert group.id to group/id path
    local group_path=$(echo $group_id | tr '.' '/')
    local artifact_path="$group_path/$artifact_id/$version"
    local filename="$artifact_id-$version.$packaging"

    local url="$MAVEN_CENTRAL/$artifact_path/$filename"
    local dest_dir="$M2_REPO/$artifact_path"
    local dest_file="$dest_dir/$filename"

    # Create directory
    mkdir -p "$dest_dir"

    # Download if not exists
    if [ ! -f "$dest_file" ]; then
        echo "Downloading: $group_id:$artifact_id:$version:$packaging"
        curl -sS -L -o "$dest_file" "$url" || {
            echo "Failed to download $url"
            return 1
        }
        echo "✓ Downloaded $filename"
    else
        echo "✓ Already exists: $filename"
    fi
}

# Download Spring Boot parent POMs
echo "=== Downloading Spring Boot Parent POMs ==="
download_artifact "org.springframework.boot" "spring-boot-starter-parent" "3.2.0" "pom"
download_artifact "org.springframework.boot" "spring-boot-dependencies" "3.2.0" "pom"
download_artifact "org.springframework.boot" "spring-boot-parent" "3.2.0" "pom"

echo ""
echo "✅ Parent POMs downloaded successfully!"
echo "Now try running: mvn clean compile -o (offline mode)"
