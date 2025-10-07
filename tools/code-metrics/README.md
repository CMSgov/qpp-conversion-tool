# Code Metrics Tools for QPP Conversion Tool

This directory contains tools for analyzing code metrics (Lines of Code and Cyclomatic Complexity) for the REST API module.

## Tools Included

### 1. CyclomaticComplexityCalculator.java
A Java-based analyzer that calculates McCabe's Cyclomatic Complexity for Java source files.

**Features:**
- Counts decision points: if, while, for, case, catch, &&, ||, ?, do
- Calculates complexity per method and per file
- Provides detailed reporting with complexity distribution
- Removes comments and strings to avoid false positives

**Usage:**
```bash
javac CyclomaticComplexityCalculator.java
java CyclomaticComplexityCalculator <source-directory>
```

### 2. generate-code-metrics.sh
A comprehensive script that generates both LOC and Cyclomatic Complexity reports.

**Features:**
- Generates JSON and detailed LOC reports using cloc
- Calculates cyclomatic complexity using the Java analyzer
- Creates a combined summary report in Markdown format
- Handles dependencies (installs cloc if needed)

**Usage:**
```bash
./generate-code-metrics.sh [project-root] [output-directory]
```

## Quick Start

From the project root directory:

```bash
# Generate metrics for rest-api module
./tools/code-metrics/generate-code-metrics.sh . target/code-metrics

# View the generated reports
cat target/code-metrics/summary-report.md
```

## Integration with Maven

Add to your `pom.xml` to run metrics during build:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <executable>bash</executable>
        <arguments>
            <argument>tools/code-metrics/generate-code-metrics.sh</argument>
            <argument>.</argument>
            <argument>target/code-metrics</argument>
        </arguments>
    </configuration>
    <executions>
        <execution>
            <id>generate-code-metrics</id>
            <phase>verify</phase>
            <goals>
                <goal>exec</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Output Files

The tools generate the following reports:

- `summary-report.md` - Combined overview with key metrics
- `loc-report.json` - Detailed LOC data in JSON format
- `loc-detailed.txt` - Line-by-line LOC breakdown
- `complexity-report.txt` - Detailed cyclomatic complexity analysis

## Metrics Interpretation

### Lines of Code (LOC)
- **Code Lines**: Actual executable code (excluding comments and blanks)
- **Comment Lines**: Documentation and comments
- **Blank Lines**: Empty lines for formatting
- **Comment Ratio**: Percentage of non-blank lines that are comments

### Cyclomatic Complexity
Based on McCabe's Cyclomatic Complexity metric:

- **1-10**: Low complexity, easy to maintain
- **11-20**: Moderate complexity, requires attention
- **21+**: High complexity, difficult to maintain and test

**Formula**: CC = Decision Points + 1 (per method)

## Best Practices

### For Development Teams
1. Run metrics regularly (weekly/monthly)
2. Set complexity thresholds in CI/CD pipelines
3. Focus code reviews on high-complexity areas
4. Refactor methods with CC > 10

### For Project Managers
1. Track metrics trends over time
2. Allocate refactoring time for high-complexity areas
3. Use metrics to estimate maintenance costs
4. Include complexity reduction in sprint planning

## Dependencies

- **Java 17+**: For running the complexity analyzer
- **cloc**: For LOC analysis (auto-installed by script)
- **bash**: For running the generation script

## Customization

### Adding New Complexity Keywords
Edit `CyclomaticComplexityCalculator.java` and modify the `COMPLEXITY_PATTERN`:

```java
private static final Pattern COMPLEXITY_PATTERN = Pattern.compile(
    "\\b(if|while|for|case|catch|&&|\\|\\||\\?|do|your_keyword)\\b"
);
```

### Excluding Files/Directories
Modify the `generate-code-metrics.sh` script to add exclusions to the cloc command:

```bash
cloc "$REST_API_DIR/src/main/java" --exclude-dir=generated --json > "$OUTPUT_DIR/loc-report.json"
```

## Troubleshooting

### Common Issues

1. **"cloc not found"**: The script will automatically install cloc on Ubuntu/Debian systems
2. **"Java not found"**: Ensure Java 17+ is installed and in PATH
3. **Permission denied**: Make sure the script is executable (`chmod +x generate-code-metrics.sh`)

### Manual Installation of cloc

```bash
# Ubuntu/Debian
sudo apt install cloc

# CentOS/RHEL
sudo yum install cloc

# macOS
brew install cloc
```