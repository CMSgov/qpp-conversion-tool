package gov.cms.qpp.conversion;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jayway.jsonpath.PathNotFoundException;

import gov.cms.qpp.conversion.aws.history.HistoricalTestRunner;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.util.JsonHelper;
import gov.cms.qpp.conversion.util.NamedInputStream;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import net.minidev.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amazonaws.util.IOUtils.copy;
import static gov.cms.qpp.conversion.ConversionEntry.checkFlags;
import static gov.cms.qpp.conversion.ConversionEntry.cli;
import static gov.cms.qpp.conversion.ConversionEntry.validatedScope;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(HistoricalTestRunner.class)
public class ScopeTest {

	private static final String BUCKET = "qrda-history";

	@Test(expected=PathNotFoundException.class)
	public void historicalAciSectionScope() throws Exception {
		//setup
		setup("-t", "ACI_SECTION", "-b");

		//expect
		iterateBucketContents(convertEm("$.scope", TransformationStatus.SUCCESS));
	}

	@Test(expected=PathNotFoundException.class)
	public void historicalIaSectionScope() throws Exception {
		//setup
		setup("-t", "IA_SECTION", "-b");

		//expect
		iterateBucketContents(convertEm("$.scope", TransformationStatus.SUCCESS));
	}

	@Test(expected=PathNotFoundException.class)
	public void historicalIAMeasurePerformedScope() throws Exception {
		//setup
		setup("-t", "IA_MEASURE", "-b");

		//expect
		iterateBucketContents(convertEm("$.scope", TransformationStatus.SUCCESS));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void historicalClinicalDocumentScope() throws Exception {
		//setup
		setup("-t", "CLINICAL_DOCUMENT", "-b");

		//when
		List<JSONArray> results =
				iterateBucketContents(
						convertEm("$.errorSources[*].validationErrors[*]", TransformationStatus.ERROR));

		//then
		results.stream()
				.flatMap(Collection::stream)
				.forEach( r -> {
					Map<String, String> result = (Map<String, String>) r;
					assertEquals(result.get("errorText"),
							"Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child");
					assertTrue(result.get("path").contains("ClinicalDocument"));
				});
	}

	//setup
	private static void setup(String... args) throws Exception {
		validatedScope(checkFlags(cli(args)));
		resetRegistries();
	}

	private static void resetRegistries() throws Exception {
		updateRegistry(QppXmlDecoder.class, "DECODERS");
		updateRegistry(QrdaValidator.class, "VALIDATORS");
		updateRegistry(QppOutputEncoder.class, "ENCODERS");
	}

	private static void updateRegistry(Class<?> location, String registryField) throws Exception {
		Field registry = location.getDeclaredField(registryField);
		registry.setAccessible(true);
		((Registry<?, ?>) registry.get(null)).load();
	}

	/**
	 * Iterate over qrda history bucket contents and apply specified conversion.
	 *
	 * @param conversion the conversion to be applied
	 * @return a list of results
	 */
	@SuppressWarnings("unchecked")
	private <T> T iterateBucketContents(Function<S3Object, List<?>> conversion) {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
		ObjectListing listing
				= s3Client.listObjects(BUCKET);
		return (T) listing.getObjectSummaries().parallelStream()
				.map(S3ObjectSummary::getKey)
				.map(key -> s3Client.getObject(BUCKET, key))
				.map(conversion)
				.collect(Collectors.toList());
	}

	/**
	 * A template for conversions to be applied to the historical qrda bucket's contents.
	 *
	 * @param jsonPath a json path used to extract contents from conversion results
	 * @param expectedStatus expected outcome of respective conversions
	 * @return a conversion function
	 */
	private Function<S3Object, List<?>> convertEm(String jsonPath, TransformationStatus expectedStatus) {
		return s3Object -> {
			Converter convert = null;
			TransformationStatus status = TransformationStatus.ERROR;
			try(InputStream stream = new NamedInputStream(s3Object.getObjectContent(), s3Object.getKey())) {
				convert = new Converter(stream);
				status = convert.transform();
				InputStream result = convert.getConversionResult();
				copy(result, System.out);
			} catch (IOException ioe) {
				fail(ioe.getMessage());
			}

			assertThat("The transformation resulted as expected. i.e. " + expectedStatus,
					status, is(expectedStatus));

			List<?> retrieved = null;
			try(InputStream jsonStream = convert.getConversionResult()){
				retrieved = JsonHelper.readJsonAtJsonPath(jsonStream,
						jsonPath, List.class);
			} catch (IOException ex) {
				fail(ex.getMessage());
			}

			return retrieved;
		};
	}
}
