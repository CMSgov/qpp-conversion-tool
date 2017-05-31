package gov.cms.qpp.conversion;

public class ConversionFileWriterWrapperTest {

//	@Test
//	public void testJsonCreation() throws IOException {
//		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(Paths.get("src/test/resources/qrda_bad_denominator.xml"));
//
//		converterWrapper.transform();
//
//		assertThat("A non-zero return value was expected.", returnValue, is(not(TransformationStatus.SUCCESS)));
//
//		InputStream errorResultsStream = converter.getConversionResult();
//		String errorResults = IOUtils.toString(errorResultsStream, StandardCharsets.UTF_8);
//
//		assertThat("The error results must have the source identifier.", errorResults, containsString("sourceIdentifier"));
//		assertThat("The error results must have some error text.", errorResults, containsString("errorText"));
//		assertThat("The error results must have an XPath.", errorResults, containsString("path"));
//	}

//	@Test
//	@PrepareForTest({Converter.class, ObjectMapper.class})
//	public void testJsonStreamFailure() throws Exception {
//		//mock
//		whenNew(ObjectMapper.class).withNoArguments().thenThrow(new JsonGenerationException("test exception", (JsonGenerator)null));
//
//		//run
//		Converter converter = new Converter(XmlUtils.fileToStream(Paths.get("src/test/resources/qrda_bad_denominator.xml")));
//		TransformationStatus returnValue = converter.transform();
//
//		//assert
//		assertThat("A failure was expected.", returnValue, is(not(TransformationStatus.SUCCESS)));
//		String expectedExceptionJson = "{ \"exception\": \"JsonProcessingException\" }";
//		InputStream errorResultsStream = converter.getConversionResult();
//		String errorResults = IOUtils.toString(errorResultsStream, StandardCharsets.UTF_8);
//
//		assertThat("An exception creating the JSON should have been thrown resulting in a basic error JSON being returned.",
//			expectedExceptionJson, is(errorResults));
//	}
}