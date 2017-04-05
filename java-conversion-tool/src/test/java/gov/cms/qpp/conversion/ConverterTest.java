package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.placeholder.DefaultEncoder;
import gov.cms.qpp.conversion.model.*;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import org.jdom2.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class ConverterTest {

	@Test
	public void testNonexistantFile() {
		String regex = Converter.wildCardToRegex("*.xml");
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_simpleFileWild() {
		String regex = Converter.wildCardToRegex("*.xml");
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathFileWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*.xml");
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathAllWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*");
		String expect = ".*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_pathExtraWild() {
		String regex = Converter.wildCardToRegex("path/to/dir/*.xm*");
		String expect = ".*\\.xm.*";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_doubleStar() {
		String regex = Converter.wildCardToRegex("path/to/dir/**");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testWildCardToRegex_tooManyWild() {
		String regex = Converter.wildCardToRegex("path/*/*/*.xml");
		String expect = "";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_wildcard() {
		String regex = Converter.extractDir("path/*/*.xml");
		String expect = "path" + File.separator;
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
		Collection<File> files = Converter.manyPath("src/test/resources/pathTest/*.xml");
		assertNotNull(files);
		assertEquals(3, files.size());

		File aFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(aFile));
		File bFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(bFile));
		File dFile = new File("src/test/resources/pathTest/subdir/d.xml");
		assertTrue(files.contains(dFile));
	}

	@Test
	public void testManyPath_doubleWild() {
		Collection<File> files = Converter.manyPath("src/test/resources/pathTest/*.xm*");
		assertNotNull(files);
		assertEquals(4, files.size());

		File cFile = new File("src/test/resources/pathTest/c.xmm");
		assertTrue(files.contains(cFile));
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
		Collection<File> files = Converter.validArgs(
				new String[]{"src/test/resources/pathTest/a.xml", "src/test/resources/pathTest/subdir/*.xml"});

		assertNotNull(files);
		assertEquals(2, files.size());

		File aFile = new File("src/test/resources/pathTest/a.xml");
		assertTrue(files.contains(aFile));
		File dFile = new File("src/test/resources/pathTest/subdir/d.xml");
		assertTrue(files.contains(dFile));
	}

	@Test
	public void testValidArgs_noFiles() {
		Collection<File> files = Converter.validArgs(new String[]{});

		assertNotNull(files);
		assertEquals(0, files.size());
	}

	@Test
	public void testMultiThreadRun_testSkipValidationToo() {
		long start = System.currentTimeMillis();

		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				"src/test/resources/pathTest/a.xml",
				"src/test/resources/pathTest/subdir/*.xml"});

		long finish = System.currentTimeMillis();

		File aJson = new File("a.qpp.json");
		File dJson = new File("d.qpp.json");

		// a.qpp.json and d.qpp.json will not exist because the a.xml and d.xml
		// file will get validation
		assertTrue( aJson.exists() );
		assertTrue( dJson.exists() );

		aJson.deleteOnExit();
		dJson.deleteOnExit();

		System.out.println("Time to run two thread transform " + (finish - start));
	}

	@Test
	public void testDefaults() throws Exception {
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				"src/test/resources/converter/defaultedNode.xml"});

		File jennyJson = new File("defaultedNode.qpp.json");
		String content = new String( Files.readAllBytes( Paths.get( "defaultedNode.qpp.json" ) ) );

		assertTrue( content.contains("Jenny") );
		jennyJson.deleteOnExit();
	}

	@Test
	public void testSkipDefaults() throws Exception {
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"});

		File jennyJson = new File("defaultedNode.qpp.json");
		String content = new String( Files.readAllBytes( Paths.get( "defaultedNode.qpp.json" ) ) );

		assertFalse( content.contains("Jenny") );
		jennyJson.deleteOnExit();
	}

	@Test
	public void testValidationErrors() throws IOException {

		//set-up
		final String errorFileName = "errantDefaultedNode.err.txt";

		File defaultJson = new File("errantDefaultedNode.qpp.json");
		File defaultError = new File(errorFileName);

		defaultJson.delete();
		defaultError.delete();

		//execute
		Converter.main(new String[]{"src/test/resources/converter/errantDefaultedNode.xml"});

		//assert
		assertThat("The JSON file must not exist", defaultJson.exists(), is(false));
		assertThat("The error file must exist", defaultError.exists(), is(true));

		String errorContent = new String(Files.readAllBytes(Paths.get(errorFileName)));
		assertThat("The error file is missing the specified content", errorContent, containsString("Jenny"));

		//clean-up
		defaultError.deleteOnExit();
	}

	@Test
	@PrepareForTest({LoggerFactory.class})
	public void testInvalidXml() throws IOException {

		//set-up
		mockStatic(LoggerFactory.class);
		Logger logger = mock(Logger.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

		//execute
		Converter.main(new String[]{"src/test/resources/non-xml-file.xml"});

		//assert
		verify(logger).error( eq("The file is not a valid XML document"), any(XmlException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, QppOutputEncoder.class})
	public void testEncodingExceptions() throws Exception {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		QppOutputEncoder encoder = mock( QppOutputEncoder.class );
		whenNew( QppOutputEncoder.class ).withNoArguments().thenReturn( encoder );
		EncodeException ex = new EncodeException( "mocked", new RuntimeException() );
		doThrow( ex ).when( encoder ).encode( any( FileWriter.class ) );

		//execute
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"
		});

		//assert
		verify(logger).error( eq("The file is not a valid XML document"), any(XmlException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testIOEncodingError() throws Exception {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		whenNew( FileWriter.class )
				.withParameterTypes( File.class )
				.withArguments( any( File.class ) )
				.thenThrow( new IOException() );

		//execute
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"
		});

		//assert
		verify(logger).error( eq("The file is not a valid XML document"), any(XmlException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testUnexpectedEncodingError() throws Exception {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		whenNew( FileWriter.class )
				.withParameterTypes( File.class )
				.withArguments( any( File.class ) )
				.thenReturn( null );

		//execute
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"
		});

		//assert
		verify(logger).error( eq("Unexpected exception occurred during conversion"), any(Exception.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testExceptionOnWriterClose() throws Exception {

		//set-up
		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		FileWriter writer = mock( FileWriter.class );
		doThrow( new IOException() ).when( writer ).close();
		whenNew( FileWriter.class )
				.withParameterTypes( File.class )
				.withArguments( any( File.class ) )
				.thenReturn( writer );

		//execute
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"
		});

		//assert
		verify(logger).error( eq("The file is not a valid XML document"), any(Exception.class) );
	}

	@XmlDecoder(templateId = "867.5309")
	public static class JennyDecoder extends DefaultDecoder {
		public JennyDecoder() {
			super("default decoder for Jenny");
		}

		@Override
		protected DecodeResult internalDecode(Element element, Node thisnode) {
			thisnode.putValue("DefaultDecoderFor", "Jenny");
			thisnode.setId("867.5309");
			if (element.getChildren().size() > 1) {
				thisnode.putValue( "problem", "too many children" );
			}
			return DecodeResult.TREE_CONTINUE;
		}
	}

	@Encoder(templateId = "867.5309")
	public static class Jenncoder extends DefaultEncoder {
		public Jenncoder() {
			super("default encoder for Jenny");
		}
	}

	@Validator(templateId = "867.5309", required = true)
	public static class TestDefaultValidator extends QrdaValidator {
		@Override
		protected List<ValidationError> internalValidate(Node node) {
			List<ValidationError> errors = new ArrayList<>();
			Consumer<Node> aggError = n -> {
				if ( n.getValue( "problem" ) != null ){
					errors.add( new ValidationError("Test validation error for Jenny"));
				}
			};
			node.getChildNodes().forEach( aggError );
			return errors;
		}
	}
}
