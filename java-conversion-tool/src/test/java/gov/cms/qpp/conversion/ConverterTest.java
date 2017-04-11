package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.placeholder.DefaultEncoder;
import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import org.jdom2.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.method;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.stub;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
public class ConverterTest {

	private static final String SEPERATOR = FileSystems.getDefault().getSeparator();

	@Test
	public void testNonexistantFile() {
		String regex = Converter.wildCardToRegex("*.xml").pattern();
		String expect = ".*\\.xml";
		assertEquals(expect, regex);
	}

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
	public void testExtractDir_wildcard() {
		String regex = Converter.extractDir("path/*/*.xml");
		String expect = "path";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_none() {
		String regex = Converter.extractDir("*.xml");
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_root() {
		String regex = Converter.extractDir( File.separator );
		String expect = ".";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_unix() {
		String regex = Converter.extractDir("path/to/dir/*.xml");
		String expect = "path" + SEPERATOR + "to" + SEPERATOR + "dir";
		assertEquals(expect, regex);
	}

	@Test
	public void testExtractDir_windows() {
		// testing the extraction not the building on windows
		String regex = Converter.extractDir("path\\to\\dir\\*.xml");
		// this test is running on *nix so expect this path while testing
		String expect = "path" + SEPERATOR + "to" + SEPERATOR + "dir";

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
	@PrepareForTest({Converter.class})
	public void testManyPath_dir() {
		// ensure a directory
		stub(method(Converter.class, "wildCardToRegex", String.class)).toReturn( Pattern.compile("src/test/resources/pathTest") );

		Collection<Path> files = Converter.manyPath("src/test/resources/pathTest");
		assertNotNull(files);
		assertEquals(0, files.size());
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
	@PrepareForTest(QppXmlDecoder.class)
	public void testDefaults() throws Exception {
		AnnotationMockHelper.mockDecoder("867.5309", JennyDecoder.class);
		AnnotationMockHelper.mockEncoder("867.5309", Jenncoder.class);

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
	@PrepareForTest({Converter.class, QrdaValidator.class})
	public void testValidationErrors() throws Exception {

		//mocking
		AnnotationMockHelper.mockDecoder("867.5309", JennyDecoder.class);
		QrdaValidator mockQrdaValidator = AnnotationMockHelper.mockValidator("867.5309", TestDefaultValidator.class, true);
		PowerMockito.whenNew(QrdaValidator.class).withNoArguments().thenReturn(mockQrdaValidator);

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
		verify(logger).error( eq("The file is not a valid XML document"), any(XmlException.class));
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, Files.class})
	public void testIOEncodingError() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

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
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( null );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		//execute
		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				Converter.SKIP_DEFAULTS,
				"src/test/resources/converter/defaultedNode.xml"
		});

		//assert
		verify(logger).error( eq("Unexpected exception occurred during conversion"), any(NullPointerException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testExceptionOnWriterClose() throws Exception {

		//set-up
		BufferedWriter writer = mock( BufferedWriter.class );
		doThrow( new IOException() ).when( writer ).close();
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( writer );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

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
	public void testValidationErrorWriterInstantiation() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( new IOException() );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		//execute
		Converter.main(new String[]{"src/test/resources/converter/defaultedNode.xml"});

		//assert
		verify(logger).error( eq("Could not write to file: {}" ),
				eq( "defaultedNode.err.txt" ), any(String.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testValidationErrorWriterInstantiationNull() throws Exception {

		//set-up
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toThrow( null );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		//execute
		Converter.main(new String[]{"src/test/resources/converter/defaultedNode.xml"});

		//assert
		verify(logger).error( eq("Unexpected exception occurred during conversion"), any(NullPointerException.class) );
	}

	@Test
	@PrepareForTest({LoggerFactory.class, Converter.class, FileWriter.class})
	public void testExceptionOnWriteValidationErrors() throws Exception {

		//set-up
		BufferedWriter writer = mock( BufferedWriter.class );
		doThrow( new IOException() ).when( writer ).write( anyString() );
		stub(method(Files.class, "newBufferedWriter", Path.class, OpenOption.class)).toReturn( writer );

		mockStatic( LoggerFactory.class );
		Logger logger = mock( Logger.class );
		when( LoggerFactory.getLogger( any(Class.class) ) ).thenReturn( logger );

		//execute
		Converter.main(new String[] {"src/test/resources/converter/defaultedNode.xml"});

		//assert
		verify(logger).error( eq("Could not write to file: {}" ),
				eq("defaultedNode.err.txt"),
				any(IOException.class) );
	}

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

	public static class Jenncoder extends DefaultEncoder {
		public Jenncoder() {
			super("default encoder for Jenny");
		}
	}

	public static class TestDefaultValidator extends NodeValidator {
		@Override
		protected void internalValidateSingleNode(final Node node) {
			if ( node.getValue( "problem" ) != null ){
				this.addValidationError( new ValidationError("Test validation error for Jenny"));
			}
		}

		@Override
		protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {}
	}
}
