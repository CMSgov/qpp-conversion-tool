#!/bin/bash

# Code Metrics Generator for QPP Conversion Tool REST API
# Generates Lines of Code and Cyclomatic Complexity reports

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${1:-$(pwd)}"
REST_API_DIR="$PROJECT_ROOT/rest-api"
OUTPUT_DIR="${2:-$PROJECT_ROOT/target/code-metrics}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}QPP Conversion Tool - Code Metrics Generator${NC}"
echo -e "${BLUE}===========================================${NC}"
echo ""

# Check if rest-api directory exists
if [ ! -d "$REST_API_DIR" ]; then
    echo -e "${RED}Error: rest-api directory not found at $REST_API_DIR${NC}"
    echo "Usage: $0 [project-root] [output-dir]"
    exit 1
fi

# Create output directory
mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}Analyzing: $REST_API_DIR/src/main/java${NC}"
echo ""

# Generate LOC report
echo -e "${GREEN}Generating Lines of Code report...${NC}"
if command -v cloc > /dev/null; then
    cloc "$REST_API_DIR/src/main/java" --json > "$OUTPUT_DIR/loc-report.json"
    cloc "$REST_API_DIR/src/main/java" --by-file > "$OUTPUT_DIR/loc-detailed.txt"
    echo "✓ LOC reports generated"
else
    echo -e "${RED}Warning: cloc not installed. Installing...${NC}"
    if command -v apt > /dev/null; then
        sudo apt update && sudo apt install -y cloc
        cloc "$REST_API_DIR/src/main/java" --json > "$OUTPUT_DIR/loc-report.json"
        cloc "$REST_API_DIR/src/main/java" --by-file > "$OUTPUT_DIR/loc-detailed.txt"
        echo "✓ LOC reports generated"
    else
        echo -e "${RED}Error: Cannot install cloc. Please install manually.${NC}"
        exit 1
    fi
fi

# Compile and run cyclomatic complexity analyzer
echo -e "${GREEN}Generating Cyclomatic Complexity report...${NC}"

# Copy the analyzer to temp location if it doesn't exist
if [ ! -f "$OUTPUT_DIR/CyclomaticComplexityCalculator.java" ]; then
    cp "$SCRIPT_DIR/CyclomaticComplexityCalculator.java" "$OUTPUT_DIR/" 2>/dev/null || {
        echo "Creating cyclomatic complexity analyzer..."
        cat > "$OUTPUT_DIR/CyclomaticComplexityCalculator.java" << 'EOF'
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CyclomaticComplexityCalculator {
    
    private static final Pattern COMPLEXITY_PATTERN = Pattern.compile(
        "\\b(if|while|for|case|catch|&&|\\|\\||\\?|do)\\b"
    );
    
    private static final Pattern METHOD_PATTERN = Pattern.compile(
        "(public|private|protected|static|\\s)*\\s+\\w+\\s+\\w+\\s*\\("
    );
    
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java CyclomaticComplexityCalculator <directory>");
            System.exit(1);
        }
        
        Path sourceDir = Paths.get(args[0]);
        CyclomaticComplexityCalculator calculator = new CyclomaticComplexityCalculator();
        calculator.analyzeDirectory(sourceDir);
    }
    
    public void analyzeDirectory(Path dir) throws IOException {
        List<FileComplexity> results = new ArrayList<>();
        int totalComplexity = 0;
        int totalMethods = 0;
        
        try (Stream<Path> paths = Files.walk(dir)) {
            List<Path> javaFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .sorted()
                .toList();
            
            System.out.println("Cyclomatic Complexity Analysis for Java Files");
            System.out.println("============================================");
            System.out.printf("%-60s %10s %10s %10s%n", "File", "Methods", "CC Total", "CC/Method");
            System.out.println("".repeat(95));
            
            for (Path file : javaFiles) {
                FileComplexity complexity = analyzeFile(file);
                results.add(complexity);
                totalComplexity += complexity.totalComplexity;
                totalMethods += complexity.methodCount;
                
                double avgComplexity = complexity.methodCount > 0 ? 
                    (double) complexity.totalComplexity / complexity.methodCount : 0.0;
                
                String relativePath = dir.relativize(file).toString();
                if (relativePath.length() > 55) {
                    relativePath = "..." + relativePath.substring(relativePath.length() - 52);
                }
                
                System.out.printf("%-60s %10d %10d %10.2f%n", 
                    relativePath, complexity.methodCount, complexity.totalComplexity, avgComplexity);
            }
            
            System.out.println("".repeat(95));
            double overallAvgComplexity = totalMethods > 0 ? (double) totalComplexity / totalMethods : 0.0;
            System.out.printf("%-60s %10d %10d %10.2f%n", 
                "TOTALS", totalMethods, totalComplexity, overallAvgComplexity);
        }
    }
    
    private FileComplexity analyzeFile(Path file) throws IOException {
        String content = Files.readString(file);
        String cleanContent = removeCommentsAndStrings(content);
        
        Matcher methodMatcher = METHOD_PATTERN.matcher(cleanContent);
        int methodCount = 0;
        while (methodMatcher.find()) {
            methodCount++;
        }
        
        Matcher complexityMatcher = COMPLEXITY_PATTERN.matcher(cleanContent);
        int complexityPoints = 0;
        while (complexityMatcher.find()) {
            complexityPoints++;
        }
        
        int totalComplexity = methodCount + complexityPoints;
        return new FileComplexity(file, methodCount, totalComplexity);
    }
    
    private String removeCommentsAndStrings(String content) {
        String result = content;
        result = result.replaceAll("//.*", "");
        result = result.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        result = result.replaceAll("\"([^\"\\\\]|\\\\.)*\"", "\"\"");
        result = result.replaceAll("'([^'\\\\]|\\\\.)*'", "''");
        return result;
    }
    
    private static class FileComplexity {
        final Path file;
        final int methodCount;
        final int totalComplexity;
        
        FileComplexity(Path file, int methodCount, int totalComplexity) {
            this.file = file;
            this.methodCount = methodCount;
            this.totalComplexity = totalComplexity;
        }
    }
}
EOF
    }
fi

# Compile and run
cd "$OUTPUT_DIR"
javac CyclomaticComplexityCalculator.java
java CyclomaticComplexityCalculator "$REST_API_DIR/src/main/java" > "$OUTPUT_DIR/complexity-report.txt"
echo "✓ Cyclomatic Complexity report generated"

# Generate summary report
echo -e "${GREEN}Generating summary report...${NC}"

cat > "$OUTPUT_DIR/summary-report.md" << EOF
# Code Metrics Report - QPP Conversion Tool REST API Module

Generated on: $(date)

## Overview
This report provides Lines of Code (LOC) and Cyclomatic Complexity metrics for the \`rest-api\` module of the QPP Conversion Tool project.

## Quick Stats
EOF

# Extract data from JSON report
if [ -f "$OUTPUT_DIR/loc-report.json" ]; then
    TOTAL_FILES=$(grep -o '"nFiles":[0-9]*' "$OUTPUT_DIR/loc-report.json" | head -1 | cut -d: -f2)
    TOTAL_LINES=$(grep -o '"n_lines":[0-9]*' "$OUTPUT_DIR/loc-report.json" | cut -d: -f2)
    CODE_LINES=$(grep -A5 '"Java"' "$OUTPUT_DIR/loc-report.json" | grep '"code"' | cut -d: -f2 | tr -d ',')
    COMMENT_LINES=$(grep -A5 '"Java"' "$OUTPUT_DIR/loc-report.json" | grep '"comment"' | cut -d: -f2 | tr -d ',')
    BLANK_LINES=$(grep -A5 '"Java"' "$OUTPUT_DIR/loc-report.json" | grep '"blank"' | cut -d: -f2 | tr -d ',')
    
    cat >> "$OUTPUT_DIR/summary-report.md" << EOF

### Lines of Code Analysis
- **Total Java Files**: $TOTAL_FILES
- **Total Lines**: $TOTAL_LINES
- **Code Lines**: $CODE_LINES
- **Comment Lines**: $COMMENT_LINES  
- **Blank Lines**: $BLANK_LINES

EOF
fi

# Extract complexity data
if [ -f "$OUTPUT_DIR/complexity-report.txt" ]; then
    TOTAL_METHODS=$(grep "TOTALS" "$OUTPUT_DIR/complexity-report.txt" | awk '{print $2}')
    TOTAL_COMPLEXITY=$(grep "TOTALS" "$OUTPUT_DIR/complexity-report.txt" | awk '{print $3}')
    AVG_COMPLEXITY=$(grep "TOTALS" "$OUTPUT_DIR/complexity-report.txt" | awk '{print $4}')
    
    cat >> "$OUTPUT_DIR/summary-report.md" << EOF
### Cyclomatic Complexity Analysis
- **Total Methods**: $TOTAL_METHODS
- **Total Cyclomatic Complexity**: $TOTAL_COMPLEXITY
- **Average Complexity per Method**: $AVG_COMPLEXITY

## Detailed Reports
- Lines of Code (detailed): \`loc-detailed.txt\`
- Lines of Code (JSON): \`loc-report.json\`
- Cyclomatic Complexity: \`complexity-report.txt\`

---
*Generated by QPP Conversion Tool Code Metrics Generator*
EOF
fi

echo ""
echo -e "${GREEN}✓ Code metrics generation complete!${NC}"
echo -e "${BLUE}Reports saved to: $OUTPUT_DIR${NC}"
echo ""
echo -e "${YELLOW}Available reports:${NC}"
echo "  - summary-report.md      (Combined summary)"  
echo "  - loc-report.json        (Lines of Code - JSON)"
echo "  - loc-detailed.txt       (Lines of Code - Detailed)"
echo "  - complexity-report.txt  (Cyclomatic Complexity)"
echo ""