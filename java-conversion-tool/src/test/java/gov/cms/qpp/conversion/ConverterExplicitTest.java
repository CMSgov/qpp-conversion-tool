package gov.cms.qpp.conversion;

public class ConverterExplicitTest {
	private static String good = "../qrda-files/valid-QRDA-III.xml";
	private static String bad = "../qrda-files/not-a-QDRA-III-file.xml";
	private static String ugly = "../qrda-files/QRDA-III-without-required-measure.xml";


//	@Test
//	public void testStreamConversion() throws IOException {
//		Path path = Paths.get(good);
//		Converter converter = new Converter(Files.newInputStream(path));
//		TransformationStatus status = converter.transform();
//		String postName = converter.getOutputFile("meep").toString();
//		InputStream result = converter.getConversionResult();
//
//		assertEquals("no problems", status, TransformationStatus.SUCCESS);
//		assertNotNull("the conversion has a result", result);
//		assertEquals("expected conversion output name", "meep.qpp.json", postName);
//	}

//	@Test
//	public void testStreamConversionError() throws IOException {
//		Path path = Paths.get(ugly);
//		Converter converter = new Converter(Files.newInputStream(path));
//		TransformationStatus status = converter.transform();
//		String postName = converter.getOutputFile("meep").toString();
//		InputStream result = converter.getConversionResult();
//
//		assertEquals("transformation errors", status, TransformationStatus.ERROR);
//		assertNotNull("the conversion has a result", result);
//		assertEquals("expected conversion output name", "meep.err.json", postName);
//	}

//	@Test
//	public void testStreamConversionNonRecoverable() throws IOException {
//		Path path = Paths.get(bad);
//		Converter converter = new Converter(Files.newInputStream(path));
//		TransformationStatus status = converter.transform();
//
//		assertEquals("can't cope", status, TransformationStatus.NON_RECOVERABLE);
//	}

//	@Test(expected = XmlInputFileException.class)
//	public void testStreamConversionEncodeException() throws IOException, EncodeException {
//		JsonOutputEncoder encoderSpy = spy(new QppOutputEncoder());
//		doThrow(new EncodeException("meep", new RuntimeException()))
//				.when(encoderSpy).encode();
//
//		Path path = Paths.get(good);
//		Converter converterSpy = spy(new Converter(Files.newInputStream(path)));
//		doReturn(encoderSpy).when(converterSpy).getEncoder();
//
//		converterSpy.transform();
//		converterSpy.getConversionResult();
//	}
}
