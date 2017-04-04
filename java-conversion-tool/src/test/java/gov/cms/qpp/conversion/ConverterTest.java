package gov.cms.qpp.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Test;

public class ConverterTest {

	@Test
	public void testWildCardToRegex_simpleFileWild() {
		String regex = Converter.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathFileWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathAllWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*").pattern();
		String expect = ".*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathExtraWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*.xm*").pattern();
		String expect = ".*\\.xm.*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_doubleStar() {
		String regex = Converter.wildCardToRegex("path/to/dir/**").pattern();
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_tooManyWild() {
		String regex = Converter.wildCardToRegex("path/*/*/*.xml").pattern();
		String expect = "";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_none() {
		String regex = Converter.extractDir("*.xml");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_unix() {
		String regex = Converter.extractDir("path/to/dir/*.xml");
		String expect = "path" + File.separator + "to" + File.separator + "dir" + File.separator;
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_windows() {
		// testing the extraction not the building on windows
		String regex = Converter.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path" + File.separator + "to" + File.separator + "dir" + File.separator;

		assertEquals(expect, regex);
	}

	@Test
	public void testManyPath_xml() {
		Collection<Path> files = Converter.manyPath("src/test/resources/pathTest/*.xml");
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
	public void testManyPath_doubleWild() {
		Collection<Path> files = Converter.manyPath("src/test/resources/pathTest/*.xm*");
		assertNotNull(files);
		assertEquals(4, files.size());

		Path cFile = Paths.get("src/test/resources/pathTest/c.xmm");
		assertTrue(files.contains(cFile));
	}

	@Test
	public void testCheckPath_xml() {
		Collection<Path> files = Converter.checkPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		//[src\test\resources\pathTest\a.xml, src\test\resources\pathTest\b.xml, src\test\resources\pathTest\subdir\d.xml]
		System.out.println(files);
		assertEquals(3, files.size());

		Collection<Path> file = Converter.checkPath("src/test/resources/pathTest/a.xml");
		assertNotNull(file);
		assertEquals(1, file.size());

		Collection<Path> none = Converter.checkPath("notExist/a.xml");
		assertNotNull(none);
		assertEquals(0, none.size());

		Collection<Path> nill = Converter.checkPath(null);
		assertNotNull(nill);
		assertEquals(0, nill.size());

		Collection<Path> blank = Converter.checkPath("   ");
		assertNotNull(blank);
		assertEquals(0, blank.size());
	}

	@Test
	public void testManyPath_pathNotFound() {
		Collection<Path> files = Converter.manyPath("notExist/*.xml");

		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testValidArgs() {
		Collection<Path> files = Converter.validArgs(
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
		Collection<Path> files = Converter.validArgs(new String[] {});

		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testMultiThreadRun_testSkipValidationToo() throws IOException {
		long start = System.currentTimeMillis();

		Converter.main(new String[] { Converter.SKIP_VALIDATION,
				"src/test/resources/pathTest/a.xml",
				"src/test/resources/pathTest/subdir/*.xml" });

		long finish = System.currentTimeMillis();

		Path aJson = Paths.get("a.qpp.json");
		Path dJson = Paths.get("d.qpp.json");

		// a.qpp.json and d.qpp.json will not exist because the a.xml and d.xml
		// file will get validation
		assertTrue( Files.exists(aJson) );
		assertTrue( Files.exists(dJson) );

		Files.delete(aJson);
		Files.delete(dJson);

		System.out.println("Time to run two thread transform " + (finish - start));
	}

}
