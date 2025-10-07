import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Simple Cyclomatic Complexity Calculator for Java files
 * Calculates McCabe's Cyclomatic Complexity by counting decision points
 */
public class CyclomaticComplexityCalculator {
    
    // Keywords that add complexity
    private static final Pattern COMPLEXITY_PATTERN = Pattern.compile(
        "\\b(if|while|for|case|catch|&&|\\|\\||\\?|do)\\b"
    );
    
    // Pattern to identify method declarations
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
            
            System.out.println("\nSummary:");
            System.out.println("========");
            System.out.println("Total Java files: " + results.size());
            System.out.println("Total methods: " + totalMethods);
            System.out.println("Total cyclomatic complexity: " + totalComplexity);
            System.out.printf("Average complexity per method: %.2f%n", overallAvgComplexity);
            
            // Complexity categories
            long lowComplexity = results.stream().mapToLong(f -> f.totalComplexity).filter(c -> c <= 10).count();
            long mediumComplexity = results.stream().mapToLong(f -> f.totalComplexity).filter(c -> c > 10 && c <= 20).count();
            long highComplexity = results.stream().mapToLong(f -> f.totalComplexity).filter(c -> c > 20).count();
            
            System.out.println("\nComplexity Distribution (by file):");
            System.out.println("Low complexity (1-10): " + lowComplexity + " files");
            System.out.println("Medium complexity (11-20): " + mediumComplexity + " files");  
            System.out.println("High complexity (>20): " + highComplexity + " files");
        }
    }
    
    private FileComplexity analyzeFile(Path file) throws IOException {
        String content = Files.readString(file);
        
        // Remove comments and strings to avoid false positives
        String cleanContent = removeCommentsAndStrings(content);
        
        // Count methods
        Matcher methodMatcher = METHOD_PATTERN.matcher(cleanContent);
        int methodCount = 0;
        while (methodMatcher.find()) {
            methodCount++;
        }
        
        // Count complexity-adding constructs
        Matcher complexityMatcher = COMPLEXITY_PATTERN.matcher(cleanContent);
        int complexityPoints = 0;
        while (complexityMatcher.find()) {
            complexityPoints++;
        }
        
        // Base complexity: 1 per method
        int totalComplexity = methodCount + complexityPoints;
        
        return new FileComplexity(file, methodCount, totalComplexity);
    }
    
    private String removeCommentsAndStrings(String content) {
        // Simple removal of single-line comments, multi-line comments, and strings
        String result = content;
        
        // Remove single-line comments
        result = result.replaceAll("//.*", "");
        
        // Remove multi-line comments
        result = result.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        
        // Remove string literals
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