package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinderTest {

    @Test
    void testFindsQppXmlFile() throws IOException {
        // Create temp directory and a sample QPP XML file
        Path tempDir = Files.createTempDirectory("qpp_test_dir");
        Path qppFile = Files.createFile(tempDir.resolve("sample_qpp.xml"));

        // Match only XML files
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
        Finder finder = new Finder(matcher, false);

        // Simulate visiting the file
        finder.visitFile(qppFile, Files.readAttributes(qppFile, BasicFileAttributes.class));

        // Assert that the QPP XML file was found
        assertEquals(1, finder.getFoundFiles().size());

        // Cleanup
        Files.deleteIfExists(qppFile);
        Files.deleteIfExists(tempDir);
    }
}
