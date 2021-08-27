package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Util {
    public static Stream<Path> getXml(Path directory) {
        try {
            return Files.list(directory).filter(Util::isXml);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean isXml(Path path) {
        return path.toString().endsWith(".xml");
    }
}
