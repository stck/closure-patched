#!/bin/bash

# Script to download required JAR dependencies from Maven Central
# Usage: ./download-libs.sh

set -e  # Exit on any error

# Configuration
LIB_DIR="lib"
MAVEN_CENTRAL_BASE="https://repo1.maven.org/maven2"
MAVEN_SEARCH_API="https://search.maven.org/solrsearch/select"

# Dependency configurations
CLOSURE_GROUP_ID="com.google.javascript"
CLOSURE_ARTIFACT_ID="closure-compiler"
CLOSURE_EXTERNS_ARTIFACT_ID="closure-compiler-externs"

SLF4J_GROUP_ID="org.slf4j"
SLF4J_API_ARTIFACT_ID="slf4j-api"
SLF4J_SIMPLE_ARTIFACT_ID="slf4j-simple"

RHINO_GROUP_ID="org.mozilla"
RHINO_ARTIFACT_ID="rhino"
RHINO_VERSION="1.7R4"

GSON_GROUP_ID="com.google.code.gson"
GSON_ARTIFACT_ID="gson"
GSON_VERSION="2.11.0"

# Fixed SLF4J versions
SLF4J_API_VERSION="1.6.6"
SLF4J_SIMPLE_VERSION="1.7.36"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if curl is available
if ! command -v curl &> /dev/null; then
    print_error "curl is required but not installed. Please install curl first."
    exit 1
fi

# Check if jq is available (for JSON parsing)
if ! command -v jq &> /dev/null; then
    print_warning "jq is not installed. Using alternative JSON parsing method."
    USE_JQ=false
else
    USE_JQ=true
fi

# Create lib directory if it doesn't exist
mkdir -p "$LIB_DIR"

# Function to get latest version for an artifact
get_latest_version() {
    local group_id="$1"
    local artifact_id="$2"
    local version=""
    
    print_status "Querying Maven Central for latest $group_id:$artifact_id version..." >&2
    
    if [ "$USE_JQ" = true ]; then
        version=$(curl -s "$MAVEN_SEARCH_API?q=g:$group_id+AND+a:$artifact_id&core=gav&rows=1&wt=json" | \
            jq -r '.response.docs[0].v' 2>/dev/null || echo "")
    else
        # Fallback method without jq - extract version using grep and sed
        version=$(curl -s "$MAVEN_SEARCH_API?q=g:$group_id+AND+a:$artifact_id&core=gav&rows=1&wt=json" | \
            grep -o '"v":"[^"]*"' | head -1 | sed 's/"v":"\([^"]*\)"/\1/' || echo "")
    fi
    
    if [ -z "$version" ] || [ "$version" = "null" ]; then
        print_error "Failed to retrieve latest version information for $group_id:$artifact_id from Maven Central" >&2
        return 1
    fi
    
    print_success "Latest version found for $group_id:$artifact_id: $version" >&2
    echo "$version"
}

# Function to download a JAR file
download_jar() {
    local group_id="$1"
    local artifact_id="$2"
    local version="$3"
    local classifier="$4"  # Optional classifier (e.g., "sources", "javadoc")
    
    local group_path=$(echo "$group_id" | tr '.' '/')
    local base_url="$MAVEN_CENTRAL_BASE/$group_path/$artifact_id/$version"
    local jar_filename
    
    if [ -n "$classifier" ]; then
        jar_filename="$artifact_id-$version-$classifier.jar"
    else
        jar_filename="$artifact_id-$version.jar"
    fi
    
    local jar_url="$base_url/$jar_filename"
    
    print_status "Downloading $jar_filename..."
    if curl -L -o "$LIB_DIR/$jar_filename" "$jar_url"; then
        print_success "Downloaded $jar_filename to $LIB_DIR/"
        return 0
    else
        print_error "Failed to download $jar_filename"
        return 1
    fi
}

# Get latest versions for dynamic dependencies
print_status "=== Getting Version Information ==="

CLOSURE_VERSION=$(get_latest_version "$CLOSURE_GROUP_ID" "$CLOSURE_ARTIFACT_ID")
if [ $? -ne 0 ]; then
    exit 1
fi

CLOSURE_EXTERNS_VERSION=$(get_latest_version "$CLOSURE_GROUP_ID" "$CLOSURE_EXTERNS_ARTIFACT_ID")
if [ $? -ne 0 ]; then
    print_warning "Could not get externs version, will skip externs download"
    CLOSURE_EXTERNS_VERSION=""
fi

# Use fixed SLF4J versions instead of latest
print_status "Using fixed SLF4J versions: API $SLF4J_API_VERSION, Simple $SLF4J_SIMPLE_VERSION"

# Download main artifacts
print_status "=== Downloading Dependencies ==="

# Download Closure Compiler
print_status "Downloading Closure Compiler..."
if ! download_jar "$CLOSURE_GROUP_ID" "$CLOSURE_ARTIFACT_ID" "$CLOSURE_VERSION"; then
    print_error "Failed to download main closure-compiler JAR"
    exit 1
fi

# Download Closure Compiler Externs if version was found
if [ -n "$CLOSURE_EXTERNS_VERSION" ]; then
    if ! download_jar "$CLOSURE_GROUP_ID" "$CLOSURE_EXTERNS_ARTIFACT_ID" "$CLOSURE_EXTERNS_VERSION"; then
        print_warning "Failed to download closure-compiler-externs, but continuing..."
    fi
fi

# Download SLF4J dependencies with fixed versions
print_status "Downloading SLF4J dependencies..."
if ! download_jar "$SLF4J_GROUP_ID" "$SLF4J_API_ARTIFACT_ID" "$SLF4J_API_VERSION"; then
    print_warning "Failed to download SLF4J API, but continuing..."
fi
if ! download_jar "$SLF4J_GROUP_ID" "$SLF4J_SIMPLE_ARTIFACT_ID" "$SLF4J_SIMPLE_VERSION"; then
    print_warning "Failed to download SLF4J Simple implementation, but continuing..."
fi

# Download Rhino (specific version)
print_status "Downloading Rhino $RHINO_VERSION..."
if ! download_jar "$RHINO_GROUP_ID" "$RHINO_ARTIFACT_ID" "$RHINO_VERSION"; then
    print_error "Failed to download Rhino JAR"
    exit 1
fi

# Download Gson (specific version)
print_status "Downloading Gson $GSON_VERSION..."
if ! download_jar "$GSON_GROUP_ID" "$GSON_ARTIFACT_ID" "$GSON_VERSION"; then
    print_error "Failed to download Gson JAR"
    exit 1
fi

# List downloaded files
print_status "Downloaded files in $LIB_DIR:"
ls -la "$LIB_DIR"/*.jar 2>/dev/null || print_warning "No JAR files found in $LIB_DIR"

# Create a properties file with version info
cat > "$LIB_DIR/dependencies.properties" << EOF
# Project Dependencies Version Information
# Generated on $(date)
closure-compiler.version=$CLOSURE_VERSION
closure-compiler-externs.version=${CLOSURE_EXTERNS_VERSION:-"N/A"}
slf4j-api.version=$SLF4J_API_VERSION
slf4j-simple.version=$SLF4J_SIMPLE_VERSION
rhino.version=$RHINO_VERSION
gson.version=$GSON_VERSION
downloadDate=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
EOF

print_success "Version information saved to $LIB_DIR/dependencies.properties"

# Summary message
DOWNLOADED_DEPS=()
DOWNLOADED_DEPS+=("Closure Compiler ($CLOSURE_VERSION)")
if [ -n "$CLOSURE_EXTERNS_VERSION" ]; then
    DOWNLOADED_DEPS+=("Externs ($CLOSURE_EXTERNS_VERSION)")
fi
DOWNLOADED_DEPS+=("SLF4J API ($SLF4J_API_VERSION)")
DOWNLOADED_DEPS+=("SLF4J Simple ($SLF4J_SIMPLE_VERSION)")
DOWNLOADED_DEPS+=("Rhino ($RHINO_VERSION)")
DOWNLOADED_DEPS+=("Gson ($GSON_VERSION)")

# Join array elements with ", "
DEPS_STRING=$(IFS=", "; echo "${DOWNLOADED_DEPS[*]}")
print_status "Done! Downloaded: $DEPS_STRING to $LIB_DIR/"

echo ""
echo "Next steps:"
echo "  1. Ensure sencha.jar is available in input/ directory"
echo "  2. Run: ./build.sh to compile all Java sources"