package gov.cms.qpp.conversion;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class ConverterTest {

	@Test
	public void testWildCardToRegex_simpleFileWild() {
		String regex  = Converter.wildCardToRegex("*.xml");
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}
	
	@Test
	public void testWildCardToRegex_pathFileWild() {
		String regex  = Converter.wildCardToRegex("path/to/dir/*.xml");
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathAllWild() {
		String regex  = Converter.wildCardToRegex("path/to/dir/*");
		String expect = ".*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathExtraWild() {
		String regex  = Converter.wildCardToRegex("path/to/dir/*.xm*");
		String expect = ".*\\.xm.*";
		assertEquals(expect, regex);
	}
	
	@Test
	public void testWildCardToRegex_doubleStar() {
		String regex  = Converter.wildCardToRegex("path/to/dir/**");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_tooManyWild() {
		String regex  = Converter.wildCardToRegex("path/*/*/*.xml");
		String expect = "";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_none() {
		String regex  = Converter.extractDir("*.xml");
		String expect = ".";
		assertEquals(expect, regex);
	}
	@Test
	public void testExtractDir_unix() {
		String regex  = Converter.extractDir("path/to/dir/*.xml");
		String expect = "path/to/dir/";
		assertEquals(expect, regex);
	}
	@Test
	public void testExtractDir_windows() {
		// testing the extraction not the building on windows
		String regex  = Converter.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path/to/dir/"; 
		
		assertEquals(expect, regex);
	}
	
	@Test
	public void testManyPath_xml() {
		Collection<File> files = Converter.manyPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		assertEquals(3, files.size());
		
		File aFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue( files.contains(aFile) );
		File bFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue( files.contains(bFile) );
		File dFile = new File("src/test/resources/pathTest/subdir/d.xml");
		assertTrue( files.contains(dFile) );
	}
	@Test
	public void testManyPath_doubleWild() {
		Collection<File> files = Converter.manyPath("src/test/resources/pathTest/*.xm*");
		assertNotNull(files);
		assertEquals(4, files.size());
		
		File cFile = new File("src/test/resources/pathTest/c.xmm");
		assertTrue( files.contains(cFile) );
	}
	
	
	@Test
	public void testCheckPath_xml() {
		Collection<File> files = Converter.checkPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		assertEquals(3, files.size());
		
		Collection<File> file = Converter.checkPath("src/test/resources/pathTest/a.xml");
		assertNotNull(file);
		assertEquals(1, file.size());

		Collection<File> none = Converter.checkPath("notExist/a.xml");
		assertNotNull(none);
		assertEquals(0, none.size());
		
		Collection<File> nill = Converter.checkPath(null);
		assertNotNull(nill);
		assertEquals(0, nill.size());
		
		Collection<File> blank = Converter.checkPath("   ");
		assertNotNull(blank);
		assertEquals(0, blank.size());
	}
	
	@Test
	public void testManyPath_pathNotFound() {
		Collection<File> files = Converter.manyPath("notExist/*.xml");
		
		assertNotNull(files);
		assertEquals(0, files.size());
	}
	
	
	@Test
	public void testValidArgs() {
		Collection<File> files = Converter.validArgs(new String[] {
				"src/test/resources/pathTest/a.xml", 
				"src/test/resources/pathTest/subdir/*.xml"
			});
		
		assertNotNull(files);
		assertEquals(2, files.size());

		File aFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue( files.contains(aFile) );
		File dFile = new File("src/test/resources/pathTest/subdir/d.xml");
		assertTrue( files.contains(dFile) );
	}
	
	@Test
	public void testValidArgs_noFiles() {
		Collection<File> files = Converter.validArgs(new String[] {});
		
		assertNotNull(files);
		assertEquals(0, files.size());
	}
	
	@Test
	public void testMultiThreadRun() {
		long start = System.currentTimeMillis();
		
		Converter.main(new String[] {
				"src/test/resources/pathTest/a.xml", 
				"src/test/resources/pathTest/subdir/*.xml"
			});
		
		long finish = System.currentTimeMillis();
		
		File aJson = new File("a.qpp.json");
		File dJson = new File("d.qpp.json");
		
		assertTrue( aJson.exists() );
		assertTrue( dJson.exists() );
		
		aJson.deleteOnExit();
		dJson.deleteOnExit();
		
		System.out.println("Time to run two thread transform " + (finish-start));
	}
}
