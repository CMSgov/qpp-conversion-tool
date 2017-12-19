package gov.cms.qpp.conversion;

import com.google.common.collect.ImmutableMap;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class ConversionEntryTest {

	private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	private static final String SKIP_DEFAULTS = "--" + ConversionEntry.SKIP_DEFAULTS;
	private static final String SKIP_VALIDATION = "--" + ConversionEntry.SKIP_VALIDATION;
	private static final String TEMPLATE_SCOPE = "--" + ConversionEntry.TEMPLATE_SCOPE;

	@AfterEach
	void teardown() throws IOException {
		Files.deleteIfExists(Paths.get("defaultedNode.qpp.json"));
	}

	@Test
	void testNonexistantFile() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexSimpleFileWild() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexPathFileWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xml").pattern();
		String expect = ".*\\.xml";
		assertWithMessage("Should be %s", expect).that(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexPathAllWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*").pattern();
		String expect = ".*";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexPathExtraWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xm*").pattern();
		String expect = ".*\\.xm.*";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexDoubleStar() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/**").pattern();
		String expect = ".";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testWildCardToRegexTooManyWild() {
		String regex = ConversionEntry.wildCardToRegex("path/*/*/*.xml").pattern();
		String expect = "";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testExtractDirWildcard() {
		String regex = ConversionEntry.extractDir("path/*/*.xml");
		String expect = "path";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testExtractDirNone() {
		String regex = ConversionEntry.extractDir("*.xml");
		String expect = ".";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testExtractDirRoot() {
		String regex = ConversionEntry.extractDir( File.separator );
		String expect = ".";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testExtractDirUnix() {
		String regex = ConversionEntry.extractDir("path/to/dir/*.xml");
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";
		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testExtractDirWindows() {
		// testing the extraction not the building on windows
		String regex = ConversionEntry.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";

		assertThat(expect).isEqualTo(regex);
	}

	@Test
	void testManyPathXml() {
		Path baseDir = Paths.get("src/test/resources/pathTest/");
		Path aFile = baseDir.resolve("a.xml");
		Path bFile = baseDir.resolve("b.xml");
		Path dFile = baseDir.resolve("subdir/d.xml");

		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest/*.xml");
		assertWithMessage("There should %s files", 3)
				.that(files).containsExactly(aFile, bFile, dFile);
	}

	@Test
	void testManyPathDir() {
		String pathTest = "src/test/resources/pathTest";
		// ensure a directory
		MemberModifier.stub(MemberMatcher.method(ConversionEntry.class, "wildCardToRegex", String.class)).toReturn( Pattern.compile(pathTest) );

		Collection<Path> files = ConversionEntry.manyPath(pathTest);
		assertWithMessage("No files should be found")
				.that(files.size()).isEqualTo(0);
	}

	@Test
	void testManyPathDoubleWild() {
		String filePath = "src/test/resources/pathTest/c.xmm";
		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest/*.xm*");

		assertWithMessage("Should find 4 matching files")
				.that(files.size()).isEqualTo(4);
		assertWithMessage("Matching file %s should have been found", filePath)
				.that(files).contains(Paths.get(filePath));
	}

	@Test
	void testCheckPathXml() {
		Map<String, Integer> scenarios = ImmutableMap.of(
				"src/test/resources/pathTest/*.xml", 3,
				"src/test/resources/pathTest/a.xml", 1,
				"notExist/a.xml", 0,
				"   ", 0
		);

		scenarios.forEach((key, value) -> {
			Collection<Path> files = ConversionEntry.checkPath(key);
			assertWithMessage(key + " should not result in null paths")
					.that(files).isNotNull();
			assertWithMessage("Number of matched files does not meet expectation")
					.that(files.size()).isEqualTo(value);
		});
	}

	@Test
	void testCheckPathNull() {
		Collection<Path> files = ConversionEntry.checkPath("src/test/resources/pathTest/*.xml");
		assertWithMessage("Should find 3 files")
				.that(files.size()).isEqualTo(3);
	}

	@Test
	void testManyPathPathNotFound() {
		Collection<Path> files = ConversionEntry.manyPath("notExist/*.xml");

		assertWithMessage("Should not find any files")
				.that(files).isEmpty();
	}

	@Test
	void testValidArgs() {
		String[] args = { "src/test/resources/pathTest/a.xml", "src/test/resources/pathTest/subdir/*.xml" };
		String found = "src/test/resources/pathTest/subdir/d.xml";
		Collection<Path> files = ConversionEntry.validArgs(args);

		assertWithMessage("Should find 2 files")
				.that(files.size()).isEqualTo(2);
		assertWithMessage("Should find %s and %s", args[0], found)
				.that(files).containsExactly(Paths.get(args[0]), Paths.get(found));
	}

	@Test
	void testValidArgsHelp() {
		Collection<Path> files = ConversionEntry.validArgs(
				new String[] { "-h", "src/test/resources/pathTest/a.xml", "src/test/resources/pathTest/subdir/*.xml" });

		assertWithMessage("help option bails and forces return of empty input collection")
				.that(files).isEmpty();
	}

	@Test
	void testValidArgsNoFiles() {
		Collection<Path> files = ConversionEntry.validArgs(new String[] {});

		assertWithMessage("files should be empty")
				.that(files).isEmpty();
	}

	@Test
	void shouldDenyInvalidTemplateScopes() throws ParseException {
		//when
		CommandLine line = ConversionEntry.cli(new String[] {"-t", QrdaScope.ACI_SECTION.name() + ",MEEP"});
		boolean result = ConversionEntry.shouldContinue(line);

		//then
		assertWithMessage("MEEP is not a valid scope")
				.that(result).isFalse();
	}

	@Test
	void shouldAllowEmptyTemplateScope() throws ParseException {
		//when
		Assertions.assertThrows(MissingArgumentException.class, () -> {
			CommandLine line = ConversionEntry.cli(new String[] {"-t"});
			ConversionEntry.shouldContinue(line);
		});
	}

	@Test
	void shouldAllowValidTemplateScopes() throws ParseException {
		//when
		CommandLine line = ConversionEntry.cli(new String[] {"file.txt", "-t", QrdaScope.ACI_SECTION.name()
				+ ","
				+ QrdaScope.IA_SECTION.name()});
		boolean result = ConversionEntry.shouldContinue(line);

		//then
		assertWithMessage("Both should be valid scopes")
				.that(result).isTrue();
	}

	@Test
	void testValidArgsParseException() throws Exception {
		//when
		Collection<Path> arguments = ConversionEntry.validArgs("-someInvalidArgument");

		//then
		assertThat(arguments).isEmpty();
	}

	//cli
	@Test
	void testHandleSkipDefaults() throws ParseException {
		CommandLine line = ConversionEntry.cli(SKIP_DEFAULTS);
		assertWithMessage("Should have a skip default option")
				.that(line.hasOption(ConversionEntry.SKIP_DEFAULTS))
				.isTrue();
	}

	@Test
	void testHandleSkipDefaultsAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli("-d");
		assertWithMessage("Should have a skip default option")
				.that(line.hasOption(ConversionEntry.SKIP_DEFAULTS))
				.isTrue();
	}

	@Test
	void testHandleSkipValidation() throws ParseException {
		CommandLine line = ConversionEntry.cli(SKIP_VALIDATION);
		assertWithMessage("Should have a skip validation option")
				.that(line.hasOption(ConversionEntry.SKIP_VALIDATION))
				.isTrue();
	}

	@Test
	void testHandleSkipValidationAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli("-v");
		assertWithMessage("Should have a skip validation option")
				.that(line.hasOption(ConversionEntry.SKIP_VALIDATION))
				.isTrue();
	}

	@Test
	void testHandleCombo() throws ParseException {
		CommandLine line = ConversionEntry.cli("-dvt", "meep");
		assertWithMessage("Should have a skip validation option")
				.that(line.hasOption(ConversionEntry.SKIP_VALIDATION)).isTrue();
		assertWithMessage("Should have a skip default option")
				.that(line.hasOption(ConversionEntry.SKIP_DEFAULTS)).isTrue();
		assertThat(line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE))
				.isEqualTo("meep");
	}

	@Test
	void testHandleTemplateScope() throws ParseException {
		CommandLine line = ConversionEntry.cli(TEMPLATE_SCOPE, "meep");

		assertWithMessage("Should have a template scope option")
				.that(line.hasOption(ConversionEntry.TEMPLATE_SCOPE))
				.isTrue();
		assertThat(line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE))
				.isEqualTo("meep");
	}

	@Test
	void testHandleTemplateScopeAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli("-t", "meep");

		assertWithMessage("Should have a template scope option")
				.that(line.hasOption(ConversionEntry.TEMPLATE_SCOPE))
				.isTrue();
		assertThat(line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE))
				.isEqualTo("meep");
	}

	@Test
	void testHandleArguments() throws ParseException {
		CommandLine line = ConversionEntry.cli(
				SKIP_VALIDATION, "file.txt", SKIP_DEFAULTS, "-t", "meep,mawp", "file2.txt");
		List<String> args = line.getArgList();

		assertWithMessage("should be comprised of the 2 files")
				.that(args.size())
				.isEqualTo(2);
		assertThat(args).containsExactly("file.txt", "file2.txt");
	}

	@Test
	void privateConstructorTest() throws Exception {
		Constructor<ConversionEntry> constructor = ConversionEntry.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		ConversionEntry conversionEntry = constructor.newInstance();

		assertWithMessage("Expect to have an instance here ")
				.that(conversionEntry)
				.isInstanceOf(ConversionEntry.class);
	}
}
