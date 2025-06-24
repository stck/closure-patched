#!/bin/bash

# Patch creation script for Sencha Closure Compiler
# Usage: ./create-patch.sh

set -e  # Exit on any error

# Configuration
LIB_DIR="lib"
OUT_DIR="out"
PATCH_DIR="patch"
INPUT_DIR="input"
TEMP_DIR="temp"
SENCHA_JAR="sencha.jar"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[PATCH]${NC} $1"
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

# Check if required tools are available
check_dependencies() {
    print_status "Checking required tools..."
    
    local missing_tools=()
    
    for tool in java javac jar unzip curl jq; do
        if ! command -v $tool &> /dev/null; then
            missing_tools+=($tool)
        fi
    done
    
    if [ ${#missing_tools[@]} -ne 0 ]; then
        print_error "Missing required tools: ${missing_tools[*]}"
        print_error "Please install the missing tools and try again."
        exit 1
    fi
    
    print_success "All required tools are available"
}

# Clean previous build artifacts
clean_directories() {
    print_status "=== Step 0: Cleaning directories ==="
    
    for dir in "$LIB_DIR" "$OUT_DIR" "$PATCH_DIR" "$TEMP_DIR"; do
        if [ -d "$dir" ]; then
            print_status "Cleaning $dir/"
            rm -rf "$dir"
        fi
    done
    
    # Remove any leftover JAR files
    rm -f closure-compressor.jar
    
    print_success "Directories cleaned"
}

# Download dependencies
download_dependencies() {
    print_status "=== Step 1: Downloading dependencies ==="
    
    if [ ! -f "download-libs.sh" ]; then
        print_error "download-libs.sh not found!"
        exit 1
    fi
    
    if [ ! -x "download-libs.sh" ]; then
        chmod +x download-libs.sh
    fi
    
    ./download-libs.sh
    print_success "Dependencies downloaded"
}

# Extract and update META-INF
update_meta_inf() {
    print_status "=== Step 2: Updating META-INF ==="
    
    if [ ! -f "$INPUT_DIR/$SENCHA_JAR" ]; then
        print_error "$INPUT_DIR/$SENCHA_JAR not found!"
        exit 1
    fi
    
    # Create temp directory
    mkdir -p "$TEMP_DIR"
    
    # Extract META-INF from sencha.jar
    print_status "Extracting META-INF from $INPUT_DIR/$SENCHA_JAR..."
    cd "$TEMP_DIR"
    unzip -q "../$INPUT_DIR/$SENCHA_JAR" "META-INF/*" || true
    cd ..
    
    if [ -d "$TEMP_DIR/META-INF" ]; then
        # Create out directory structure
        mkdir -p "$OUT_DIR"
        
        # Copy META-INF to out directory
        cp -r "$TEMP_DIR/META-INF" "$OUT_DIR/"
        print_success "META-INF extracted to $OUT_DIR/"
        
        # Update classpath in MANIFEST.MF with new closure compiler versions
        if [ -f "$OUT_DIR/META-INF/MANIFEST.MF" ] && [ -f "$LIB_DIR/dependencies.properties" ]; then
            print_status "Updating MANIFEST.MF with new closure compiler versions..."
            
            # Read current versions
            closure_compiler_version=$(grep "^closure-compiler\.version=" "$LIB_DIR/dependencies.properties" | cut -d'=' -f2)
            closure_compiler_externs_version=$(grep "^closure-compiler-externs\.version=" "$LIB_DIR/dependencies.properties" | cut -d'=' -f2)
            
            print_status "Updating with Closure Compiler: ${closure_compiler_version:-unknown}"
            print_status "Updating with Closure Externs: ${closure_compiler_externs_version:-unknown}"
            
            # Create new MANIFEST.MF with updated classpath
            if [ -n "${closure_compiler_version}" ] && [ -n "${closure_compiler_externs_version}" ]; then
                # Read original manifest
                manifest_content=$(cat "$OUT_DIR/META-INF/MANIFEST.MF")
                
                # Create temporary file for new manifest
                temp_manifest="$TEMP_DIR/MANIFEST.MF.new"
                
                # Process line by line and replace lines 6 and 7 with new closure compiler paths
                line_num=0
                while IFS= read -r line; do
                    line_num=$((line_num + 1))
                    if [ $line_num -eq 6 ]; then
                        echo " b/closure-compiler-externs-${closure_compiler_externs_version}.jar lib/closure-compiler-${closure_compiler_version:0:7}" >> "$temp_manifest"
                    elif [ $line_num -eq 7 ]; then
                        echo " ${closure_compiler_version:7}.jar lib/commons-codec-1.17.0.jar lib/commons-collections4-4.5.0-M2" >> "$temp_manifest"
                    else
                        echo "$line" >> "$temp_manifest"
                    fi
                done < "$OUT_DIR/META-INF/MANIFEST.MF"
                
                # Replace original with updated manifest
                mv "$temp_manifest" "$OUT_DIR/META-INF/MANIFEST.MF"
                print_success "MANIFEST.MF updated with new closure compiler paths"
            fi
        fi
    else
        print_warning "No META-INF found in $INPUT_DIR/$SENCHA_JAR, creating minimal structure"
        mkdir -p "$OUT_DIR/META-INF"
        echo "Manifest-Version: 1.0" > "$OUT_DIR/META-INF/MANIFEST.MF"
        echo "Created-By: Sencha Closure Compiler Patcher" >> "$OUT_DIR/META-INF/MANIFEST.MF"
    fi
    
    print_success "META-INF updated"
}

# Build Java classes
build_classes() {
    print_status "=== Step 3: Building Java classes ==="
    
    if [ ! -f "build.sh" ]; then
        print_error "build.sh not found!"
        exit 1
    fi
    
    if [ ! -x "build.sh" ]; then
        chmod +x build.sh
    fi
    
    ./build.sh
    print_success "Java classes built"
}

# Create patch structure
create_patch() {
    print_status "=== Step 4: Creating patch structure ==="
    
    # Create patch directories
    mkdir -p "$PATCH_DIR/lib"
    
    # Copy sencha.jar to patch folder
    print_status "Copying $INPUT_DIR/$SENCHA_JAR to $PATCH_DIR/"
    cp "$INPUT_DIR/$SENCHA_JAR" "$PATCH_DIR/"
    
    print_success "Patch structure created"
}

# Replace contents in patched sencha.jar
update_sencha_jar() {
    print_status "=== Step 5: Updating patched sencha.jar ==="
    
    if [ ! -d "$OUT_DIR" ]; then
        print_error "Output directory $OUT_DIR not found!"
        exit 1
    fi
    
    print_status "Extracting original sencha.jar content..."
    
    # Extract the original JAR to a temporary directory
    mkdir -p "$TEMP_DIR/jar_content"
    cd "$TEMP_DIR/jar_content"
    unzip -q "../../$PATCH_DIR/$SENCHA_JAR"
    cd ../..
    
    print_status "Copying updated classes and META-INF..."
    
    # Copy all compiled content from OUT_DIR (including META-INF and compiled classes)
    cp -r "$OUT_DIR"/* "$TEMP_DIR/jar_content/"
    
    print_status "Creating updated sencha.jar..."
    
    # Remove any existing META-INF that might have been in the original jar to ensure our custom one is used
    rm -rf "$TEMP_DIR/jar_content/META-INF"
    
    # Copy our custom META-INF with updated closure compiler paths
    if [ -d "$OUT_DIR/META-INF" ]; then
        cp -r "$OUT_DIR/META-INF" "$TEMP_DIR/jar_content/"
        print_status "Custom META-INF with updated closure compiler paths applied"
    fi
    
    # Create the new JAR with custom manifest
    cd "$TEMP_DIR/jar_content"
    if [ -f "META-INF/MANIFEST.MF" ]; then
        # Use jar cfm to create JAR with custom manifest
        jar cfm "../sencha_patch.jar" "META-INF/MANIFEST.MF" . 
    else
        jar cf "../sencha_patch.jar" .
    fi
    cd ../..
    
    # Replace the original with updated version
    mv "$TEMP_DIR/sencha_patch.jar" "$PATCH_DIR/$SENCHA_JAR"
    
    # Verify META-INF exists in the final JAR
    if unzip -l "$PATCH_DIR/$SENCHA_JAR" | grep -q "META-INF/"; then
        print_success "Sencha.jar updated with compiled classes and META-INF preserved"
    else
        print_warning "META-INF might not be properly included in the JAR"
    fi
}

# Copy closure compiler JARs
copy_closure_jars() {
    print_status "=== Step 6: Copying Closure Compiler JARs ==="
    
    # Find and copy closure compiler JARs
    local closure_jar=$(find "$LIB_DIR" -name "closure-compiler-v*.jar" | head -1)
    local externs_jar=$(find "$LIB_DIR" -name "closure-compiler-externs-v*.jar" | head -1)
    
    if [ -f "$closure_jar" ]; then
        print_status "Copying $(basename $closure_jar)..."
        cp "$closure_jar" "$PATCH_DIR/lib/"
    else
        print_error "Closure compiler JAR not found!"
        exit 1
    fi
    
    if [ -f "$externs_jar" ]; then
        print_status "Copying $(basename $externs_jar)..."
        cp "$externs_jar" "$PATCH_DIR/lib/"
    else
        print_warning "Closure compiler externs JAR not found, skipping..."
    fi
    
    print_success "Closure Compiler JARs copied"
}

# Create patch information
create_patch_info() {
    print_status "Creating patch information..."
    
    # Create README for the patch
    cat > "$PATCH_DIR/README.md" << 'EOF'
# Sencha Closure Compiler Patch

This patch updates the Sencha framework with the latest Google Closure Compiler.

## Contents

- `sencha.jar` - Updated Sencha framework with patched Closure Compiler integration
- `lib/closure-compiler-*.jar` - Latest Google Closure Compiler
- `lib/closure-compiler-externs-*.jar` - Latest Closure Compiler externs

## Installation

1. Backup your original `sencha.jar`
2. Replace your `sencha.jar` with the patched version
3. Add the Closure Compiler JARs from `lib/` to your classpath

## Version Information

EOF
    
    # Add version info if available
    if [ -f "$LIB_DIR/dependencies.properties" ]; then
        echo "Generated on: $(date)" >> "$PATCH_DIR/README.md"
        echo "" >> "$PATCH_DIR/README.md"
        echo "```" >> "$PATCH_DIR/README.md"
        cat "$LIB_DIR/dependencies.properties" >> "$PATCH_DIR/README.md"
        echo "```" >> "$PATCH_DIR/README.md"
    fi
    
    # Copy dependencies info
    if [ -f "$LIB_DIR/dependencies.properties" ]; then
        cp "$LIB_DIR/dependencies.properties" "$PATCH_DIR/"
    fi
    
    print_success "Patch information created"
}

# Cleanup temporary files
cleanup() {
    print_status "Cleaning up temporary files..."
    
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
    
    print_success "Cleanup completed"
}

# Generate patch summary
generate_summary() {
    print_status "=== Patch Summary ==="
    
    echo ""
    echo "Patch created successfully in: $PATCH_DIR/"
    echo ""
    echo "Contents:"
    if command -v tree &> /dev/null; then
        tree "$PATCH_DIR"
    else
        find "$PATCH_DIR" -type f | sort | sed 's/^/  /'
    fi
    echo ""
    
    # Calculate file sizes
    echo "File sizes:"
    find "$PATCH_DIR" -type f -exec ls -lh {} \; | awk '{print "  " $9 " (" $5 ")"}'
    echo ""
    
    # Total size
    local total_size=$(du -sh "$PATCH_DIR" | awk '{print $1}')
    echo "Total patch size: $total_size"
    echo ""
    
    print_success "Patch creation completed!"
    echo ""
    echo "Next steps:"
    echo "  1. Test the patch in your development environment"
    echo "  2. Deploy the patched sencha.jar to your production environment"
    echo "  3. Ensure the Closure Compiler JARs are in your classpath"
}

# Main execution
main() {
    print_status "Starting Sencha Closure Compiler patch creation..."
    echo ""
    
    check_dependencies
    clean_directories
    download_dependencies
    update_meta_inf
    build_classes
    create_patch
    update_sencha_jar
    copy_closure_jars
    create_patch_info
    cleanup
    generate_summary
}

# Error handling
trap 'print_error "Script failed at line $LINENO"; cleanup; exit 1' ERR

# Run main function
main "$@"

