package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConversionEntryTest {

	private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	private static final String SKIP_DEFAULTS = "--" + ConversionEntry.SKIP_DEFAULTS;
	private static final String SKIP_VALIDATION = "--" + ConversionEntry.SKIP_VALIDATION;
	private static final String TEMPLATE_SCOPE = "--" + ConversionEntry.TEMPLATE_SCOPE;
	private PrintStream stdout;

	@Before
	public void setup() throws Exception {
		stdout = System.out;
	}

	@After
	public void teardown() throws IOException {
		Files.deleteIfExists(Paths.get("defaultedNode.qpp.json"));
		System.setOut(stdout);
	}

	@Test
	public void testNonexistantFile() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexSimpleFileWild() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexPathFileWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexPathAllWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*").pattern();
		String expect = ".*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexPathExtraWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xm*").pattern();
		String expect = ".*\\.xm.*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexDoubleStar() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/**").pattern();
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegexTooManyWild() {
		String regex = ConversionEntry.wildCardToRegex("path/*/*/*.xml").pattern();
		String expect = "";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDirWildcard() {
		String regex = ConversionEntry.extractDir("path/*/*.xml");
		String expect = "path";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDirNone() {
		String regex = ConversionEntry.extractDir("*.xml");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDirRoot() {
		String regex = ConversionEntry.extractDir( File.separator );
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDirUnix() {
		String regex = ConversionEntry.extractDir("path/to/dir/*.xml");
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDirWindows() {
		// testing the extraction not the building on windows
		String regex = ConversionEntry.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";

		assertEquals(expect, regex);
	}

	@Test
	public void testManyPathXml() {
		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		assertEquals(3, files.size());

		Path aFile = Paths.get("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(aFile));
		Path bFile = Paths.get("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(bFile));
		Path dFile = Paths.get("src/test/resources/pathTest/subdir/d.xml");
		assertTrue(files.contains(dFile));
	}

	@Test
	@PrepareForTest({ConversionEntry.class})
	public void testManyPathDir() {
		String pathTest = "src/test/resources/pathTest";
		// ensure a directory
		MemberModifier.stub(MemberMatcher.method(ConversionEntry.class, "wildCardToRegex", String.class)).toReturn( Pattern.compile(pathTest) );

		Collection<Path> files = ConversionEntry.manyPath(pathTest);
		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testManyPathDoubleWild() {
		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest/*.xm*");
		assertNotNull(files);
		assertEquals(4, files.size());

		Path cFile = Paths.get("src/test/resources/pathTest/c.xmm");
		assertTrue(files.contains(cFile));
	}

	@Test
	public void testCheckPathXml() {
		Collection<Path> files = ConversionEntry.checkPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		assertEquals(3, files.size());

		Collection<Path> file = ConversionEntry.checkPath("src/test/resources/pathTest/a.xml");
		assertNotNull(file);
		assertEquals(1, file.size());

		Collection<Path> none = ConversionEntry.checkPath("notExist/a.xml");
		assertNotNull(none);
		assertEquals(0, none.size());

		Collection<Path> nill = ConversionEntry.checkPath(null);
		assertNotNull(nill);
		assertEquals(0, nill.size());

		Collection<Path> blank = ConversionEntry.checkPath("   ");
		assertNotNull(blank);
		assertEquals(0, blank.size());
	}

	@Test
	public void testManyPathPathNotFound() {
		Collection<Path> files = ConversionEntry.manyPath("notExist/*.xml");

		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testValidArgs() {
		Collection<Path> files = ConversionEntry.validArgs(
				new String[] { "src/test/resources/pathTest/a.xml", "src/test/resources/pathTest/subdir/*.xml" });

		assertNotNull(files);
		assertEquals(2, files.size());

		Path aFile = Paths.get("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(aFile));
		Path dFile = Paths.get("src/test/resources/pathTest/subdir/d.xml");
		assertTrue(files.contains(dFile));
	}

	@Test
	public void testValidArgsHelp() {
		Collection<Path> files = ConversionEntry.validArgs(
				new String[] { "-h", "src/test/resources/pathTest/a.xml", "src/test/resources/pathTest/subdir/*.xml" });

		assertNotNull(files);
		assertTrue("help option bails and forces return of empty input collection", files.isEmpty());
	}

	@Test
	public void testValidArgsNoFiles() {
		Collection<Path> files = ConversionEntry.validArgs(new String[] {});

		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void shouldDenyInvalidTemplateScopes() throws ParseException {
		//setup
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos1));

		//when
		CommandLine line = ConversionEntry.cli(new String[] {"-t", QrdaScope.ACI_SECTION.name() + ",MEEP"});
		boolean result = ConversionEntry.shouldContinue(line);

		//then
		assertFalse("MEEP is not a valid scope", result);
		assertThat(baos1.toString(), containsString(ConversionEntry.INVALID_TEMPLATE_SCOPE));
	}

	@Test(expected = MissingArgumentException.class)
	public void shouldAllowEmptyTemplateScope() throws ParseException {
		//when
		CommandLine line = ConversionEntry.cli(new String[] {"-t"});
		ConversionEntry.shouldContinue(line);
	}

	@Test
	public void shouldAllowValidTemplateScopes() throws ParseException {
		//when
		CommandLine line = ConversionEntry.cli(new String[] {"file.txt", "-t", QrdaScope.ACI_SECTION.name()
				+ ","
				+ QrdaScope.IA_SECTION.name()});
		boolean result = ConversionEntry.shouldContinue(line);

		//then
		assertTrue("Both should be valid scopes", result);
	}

	@Test
	@PrepareForTest({DefaultParser.class, ConversionEntry.class})
	public void testValidArgsParseException() throws Exception {
		//setup
		DefaultParser mockParser = PowerMockito.mock(DefaultParser.class);
		PowerMockito.when(mockParser.parse(ArgumentMatchers.any(Options.class), ArgumentMatchers.any(String[].class))).thenThrow(new ParseException("mock error"));
		PowerMockito.whenNew(DefaultParser.class).withNoArguments().thenReturn(mockParser);

		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos1));

		//when
		ConversionEntry.validArgs(new String[] {});

		//then
		assertThat(baos1.toString(), containsString(ConversionEntry.CLI_PROBLEM));
	}

	//cli
	@Test
	public void testHandleSkipDefaults() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {SKIP_DEFAULTS});
		assertTrue("Should have a skip default option", line.hasOption(ConversionEntry.SKIP_DEFAULTS));
	}

	@Test
	public void testHandleSkipDefaultsAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {"-d"});
		assertTrue("Should have a skip default option", line.hasOption(ConversionEntry.SKIP_DEFAULTS));
	}

	@Test
	public void testHandleSkipValidation() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {SKIP_VALIDATION});
		assertTrue("Should have a skip validation option", line.hasOption(ConversionEntry.SKIP_VALIDATION));
	}

	@Test
	public void testHandleSkipValidationAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {"-v"});
		assertTrue("Should have a skip validation option", line.hasOption(ConversionEntry.SKIP_VALIDATION));
	}

	@Test
	public void testHandleCombo() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {"-dvt", "meep"});
		assertTrue("Should have a skip validation option", line.hasOption(ConversionEntry.SKIP_VALIDATION));
		assertTrue("Should have a skip default option", line.hasOption(ConversionEntry.SKIP_DEFAULTS));
		assertEquals("Should be 'meep'", "meep", line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE));
	}

	@Test
	public void testHandleTemplateScope() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {TEMPLATE_SCOPE, "meep"});
		assertTrue("Should have a template scope option", line.hasOption(ConversionEntry.TEMPLATE_SCOPE));
		assertEquals("Should be 'meep'", "meep", line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE));
	}

	@Test
	public void testHandleTemplateScopeAbbreviated() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {"-t", "meep"});
		assertTrue("Should have a template scope option", line.hasOption(ConversionEntry.TEMPLATE_SCOPE));
		assertEquals("Should be 'meep'", "meep", line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE));
	}

	@Test
	public void testHandleArguments() throws ParseException {
		CommandLine line = ConversionEntry.cli(
				new String[]{SKIP_VALIDATION, "file.txt", SKIP_DEFAULTS, "-t", "meep,mawp", "file2.txt"});
		List<String> args = line.getArgList();

		assertEquals("should be comprised of the 2 files", 2, args.size());
		assertEquals("file.txt", args.get(0));
		assertEquals("file2.txt", args.get(1));
	}
	@Test
	public void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<ConversionEntry> constructor = ConversionEntry.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		ConversionEntry conversionEntry = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);
		Assert.assertThat("Expect to have an instance here ", conversionEntry, instanceOf(ConversionEntry.class));
	}
}
