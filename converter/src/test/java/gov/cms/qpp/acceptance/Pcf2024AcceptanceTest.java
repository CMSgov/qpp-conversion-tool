package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.acceptance.Util.getXml;
import gov.cms.qpp.CacheBuilder;
import gov.cms.qpp.model.CacheType;

public class Pcf2024AcceptanceTest {
    private static final Path BASE = Path.of("src/test/resources/pcf/acceptance2024");
    private static final Path SUCCESS = BASE.resolve("success");
    private static final Path SUCCESS_WARNING = BASE.resolve("warning");
    private static final Path FAILURE = BASE.resolve("failure");
    private static final ApmEntityIds apmEntityIds = CacheBuilder.getEntityIds(CacheType.ApmEntityIds);

    static Stream<Path> successData() {
        return getXml(SUCCESS);
    }

    static Stream<Path> failureData() {
        return getXml(FAILURE);
    }

    static Stream<Path> successWithWarningsData() {
        return getXml(SUCCESS_WARNING);
    }

    @ParameterizedTest
    @MethodSource("failureData")
    void testPcfFileFailures(Path entry) {
        Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));
        Assertions.assertThrows(TransformException.class, converter::transform);
    }

    @ParameterizedTest
    @MethodSource("successData")
    void testPcfValidFiles(Path entry) {
        AllErrors errors = null;
        List<Detail> warnings = null;

        Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

        try {
            converter.transform();
        } catch (TransformException failure) {
            errors = failure.getDetails();
            warnings = failure.getConversionReport().getWarnings();
        }

        assertThat(errors).isNull();
        assertThat(warnings).isNull();
    }

    @ParameterizedTest
    @MethodSource("successWithWarningsData")
    void testWarningFiles(Path entry) {
        AllErrors errors = null;

        Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

        try {
            converter.transform();
        } catch (TransformException failure) {
            errors = failure.getDetails();
        }

        assertThat(errors).isNull();
        assertThat(converter.getReport().getWarnings()).isNotNull();
        assertThat(converter.getReport().getWarnings()).isNotEmpty();
    }
}
