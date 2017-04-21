package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.stubs.Jenncoder;
import gov.cms.qpp.conversion.stubs.JennyDecoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class ConversionEntryTest {

	private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	private static final String SKIP_DEFAULTS = "-" + ConversionEntry.SKIP_DEFAULTS;
	private static final String SKIP_VALIDATION = "-" + ConversionEntry.SKIP_VALIDATION;
	private static final String TEMPLATE_SCOPE = "-" + ConversionEntry.TEMPLATE_SCOPE;

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("defaultedNode.qpp.json"));
	}

	@Test
	public void testNonexistantFile() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_simpleFileWild() {
		String regex = ConversionEntry.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathFileWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathAllWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*").pattern();
		String expect = ".*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathExtraWild() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/*.xm*").pattern();
		String expect = ".*\\.xm.*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_doubleStar() {
		String regex = ConversionEntry.wildCardToRegex("path/to/dir/**").pattern();
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_tooManyWild() {
		String regex = ConversionEntry.wildCardToRegex("path/*/*/*.xml").pattern();
		String expect = "";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_wildcard() {
		String regex = ConversionEntry.extractDir("path/*/*.xml");
		String expect = "path";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_none() {
		String regex = ConversionEntry.extractDir("*.xml");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_root() {
		String regex = ConversionEntry.extractDir( File.separator );
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_unix() {
		String regex = ConversionEntry.extractDir("path/to/dir/*.xml");
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_windows() {
		// testing the extraction not the building on windows
		String regex = ConversionEntry.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path" + SEPARATOR + "to" + SEPARATOR + "dir";

		assertEquals(expect, regex);
	}

	@Test
	public void testManyPath_xml() {
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
	public void testManyPath_dir() {
		// ensure a directory
		stub(method(ConversionEntry.class, "wildCardToRegex", String.class)).toReturn( Pattern.compile("src/test/resources/pathTest") );

		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest");
		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testManyPath_doubleWild() {
		Collection<Path> files = ConversionEntry.manyPath("src/test/resources/pathTest/*.xm*");
		assertNotNull(files);
		assertEquals(4, files.size());

		Path cFile = Paths.get("src/test/resources/pathTest/c.xmm");
		assertTrue(files.contains(cFile));
	}

	@Test
	public void testCheckPath_xml() {
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
	public void testManyPath_pathNotFound() {
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
	public void testValidArgs_noFiles() {
		Collection<Path> files = ConversionEntry.validArgs(new String[] {});

		assertNotNull(files);
		assertEquals(0, files.size());
	}


	@Test
	public void testDefaults() throws Exception {
		AnnotationMockHelper.mockDecoder("867.5309", JennyDecoder.class);
		AnnotationMockHelper.mockEncoder("867.5309", Jenncoder.class);

		ConversionEntry.main(ConversionEntry.SKIP_VALIDATION,
				"src/test/resources/converter/defaultedNode.xml");

		Path jennyJson = Paths.get("defaultedNode.qpp.json");
		String content = new String(Files.readAllBytes(jennyJson));

		assertTrue(content.contains("Jenny"));
	}

	@Test
	public void testSkipDefaults() throws Exception {
		ConversionEntry.main(ConversionEntry.SKIP_VALIDATION,
				ConversionEntry.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml");

		Path jennyJson = Paths.get("defaultedNode.qpp.json");
		String content = new String(Files.readAllBytes(jennyJson));

		assertFalse(content.contains("Jenny"));
	}

	//cli
	@Test
	public void testHandleSkipDefaults() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {SKIP_DEFAULTS});
		assertTrue("Should have a skip default option", line.hasOption(ConversionEntry.SKIP_DEFAULTS));
	}

	@Test
	public void testHandleSkipValidation() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {SKIP_VALIDATION});
		assertTrue("Should have a skip validation option", line.hasOption(ConversionEntry.SKIP_VALIDATION));
	}

	@Test
	public void testHandleTemplateScope() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {TEMPLATE_SCOPE, "meep"});
		assertTrue("Should have a template scope option", line.hasOption(ConversionEntry.TEMPLATE_SCOPE));
		assertEquals("Should be 'meep'", "meep", line.getOptionValue(ConversionEntry.TEMPLATE_SCOPE));
	}

	@Test
	public void testHandleArguments() throws ParseException {
		CommandLine line = ConversionEntry.cli(new String[] {SKIP_VALIDATION, "file.txt", TEMPLATE_SCOPE, "meep,mawp", "file2.txt"});
		List<String> args = line.getArgList();

		assertEquals("should be comprised of the 2 files", 2, args.size());
		assertEquals("file.txt", args.get(0));
		assertEquals("file2.txt", args.get(1));
	}

}
