#!/bin/bash

# Build script for Sencha Closure Compressor
# Usage: ./build.sh

set -e  # Exit on any error

# Configuration
LIB_DIR="lib"
OUT_DIR="out"
SRC_DIR="src"
INPUT_DIR="input"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[BUILD]${NC} $1"
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

# Check if Java compiler is available
if ! command -v javac &> /dev/null; then
    print_error "javac is required but not installed. Please install Java Development Kit (JDK)."
    exit 1
fi

# Show Java version
print_status "Using Java compiler version:"
javac -version

# Create output directory if it doesn't exist
mkdir -p "$OUT_DIR"

# Check if required JAR files exist
REQUIRED_JARS=()
CLASSPATH=""

# Function to add JAR to classpath if it exists
add_jar_to_classpath() {
    local jar_pattern="$1"
    local description="$2"
    local required="$3"
    
    local jar_file=$(ls $LIB_DIR/$jar_pattern 2>/dev/null | head -1)
    if [ -f "$jar_file" ]; then
        print_status "Found $description: $(basename $jar_file)"
        if [ -n "$CLASSPATH" ]; then
            CLASSPATH="$CLASSPATH:$jar_file"
        else
            CLASSPATH="$jar_file"
        fi
        return 0
    else
        if [ "$required" = "true" ]; then
            print_error "Required JAR not found: $jar_pattern"
            return 1
        else
            print_warning "Optional JAR not found: $jar_pattern"
            return 0
        fi
    fi
}

print_status "=== Checking Dependencies ==="

# Check for required JAR files
add_jar_to_classpath "closure-compiler-v*.jar" "Closure Compiler" true || exit 1
add_jar_to_classpath "../$INPUT_DIR/sencha.jar" "Sencha Framework" true || exit 1
add_jar_to_classpath "slf4j-api-*.jar" "SLF4J API" true || exit 1
add_jar_to_classpath "rhino-*.jar" "Rhino JavaScript Engine" true || exit 1
add_jar_to_classpath "gson-*.jar" "Google Gson" true || exit 1

# Check for optional JAR files
add_jar_to_classpath "closure-compiler-externs-v*.jar" "Closure Compiler Externs" false
add_jar_to_classpath "slf4j-simple-*.jar" "SLF4J Simple Implementation" false

print_status "=== Compilation ==="
print_status "Classpath: $CLASSPATH"

# Find all Java source files
JAVA_FILES=$(find "$SRC_DIR" -name "*.java" -type f)
JAVA_COUNT=$(echo "$JAVA_FILES" | wc -l)

print_status "Found $JAVA_COUNT Java source files to compile:"
echo "$JAVA_FILES" | sed 's/^/  /'

print_status "Compiling all Java sources..."

# Compile all Java sources
if javac -cp "$CLASSPATH" -d "$OUT_DIR" $JAVA_FILES; then
    print_success "Compilation completed successfully!"
    
    # List compiled files
    print_status "Generated class files:"
    find "$OUT_DIR" -name "*.class" -exec ls -la {} \;
    
    # Show directory structure
    print_status "Output directory structure:"
    if command -v tree &> /dev/null; then
        tree "$OUT_DIR"
    else
        find "$OUT_DIR" -type f | sort
    fi
    
    print_success "Build completed! Classes are available in $OUT_DIR/"
    
else
    print_error "Compilation failed!"
    exit 1
fi

# Optional: Create a simple JAR file
print_status "=== Creating JAR (Optional) ==="
JAR_NAME="closure-compressor.jar"
if command -v jar &> /dev/null; then
    print_status "Creating JAR file: $JAR_NAME"
    cd "$OUT_DIR"
    if jar cf "../$JAR_NAME" com/; then
        cd ..
        print_success "JAR created: $JAR_NAME ($(ls -lh $JAR_NAME | awk '{print $5}'))"
    else
        cd ..
        print_warning "Failed to create JAR file"
    fi
else
    print_warning "jar command not available, skipping JAR creation"
fi

echo ""
echo "Build Summary:"
echo "  Sources: $JAVA_COUNT Java files in $SRC_DIR/"
echo "  Output: $OUT_DIR/"
echo "  Classpath: $CLASSPATH"
if [ -f "$JAR_NAME" ]; then
    echo "  JAR: $JAR_NAME"
fi
echo ""
echo "Usage examples:"
echo "  # Use ClosureCompressor:"
echo "  java -cp \"$CLASSPATH:$OUT_DIR\" com.sencha.tools.compressors.closure.ClosureCompressor"
echo ""
echo "  # Use AST utilities:"
echo "  java -cp \"$CLASSPATH:$OUT_DIR\" com.sencha.tools.compiler.ast.AstUtil" 