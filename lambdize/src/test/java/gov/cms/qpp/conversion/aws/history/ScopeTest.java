package gov.cms.qpp.conversion.aws.history;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class ScopeTest {
    	private static final String BUCKET = "historical-qrda";
    	private static final String ACCESS = "aws.accessKeyId";
	private static final String SECRET = "aws.secretKey";
	private static boolean runHistoricalTests = true;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() throws IOException {
		Map<String, String> properties = getS3Properties();

		if (runHistoricalTests) {
			System.setProperty(ACCESS, properties.get(ACCESS));
			System.setProperty(SECRET, properties.get(SECRET));
		}
	}

	private static Map<String, String> getS3Properties() {
		Map<String, String> properties = null;
		try {
			Path path = Paths.get("src")
					.resolve("test")
					.resolve("resources")
					.resolve("s3Properties.json");
			properties = JsonHelper.readJson(path, Map.class);
		} catch (Exception e) {
			runHistoricalTests = false;
			System.err.println("You must configure an s3Properties.json file.  Will not run historical tests.");
			e.printStackTrace(System.err);
		}

		if (properties == null || !(properties.containsKey(ACCESS) && properties.containsKey(SECRET))) {
			runHistoricalTests = false;
			String message = String.format("s3Properties.json must contain %s and %s configurations.  Will not run historical tests.", ACCESS, SECRET);
			System.err.println(message);
		}

		return properties;
	}

	@Test
	public void historicalQrdaSansAci() throws IOException {
		if(!runHistoricalTests) {
			System.err.println("Not running historical test");
			return;
		}

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
		ObjectListing listing
				= s3Client.listObjects(BUCKET);
		listing.getObjectSummaries().parallelStream()
				.map(S3ObjectSummary::getKey)
				.map(key -> s3Client.getObject(BUCKET, key))
				.forEach( s3Object -> {

					Converter convert = null;
					TransformationStatus status = TransformationStatus.ERROR;
					try(InputStream stream = s3Object.getObjectContent()) {
						convert = new Converter(stream);
						convert.doValidation(false);

						status = convert.transform();
					} catch (IOException ioe) {
						fail(ioe.getMessage());
					}

					assertThat("The file should have transformed correctly.", status, is(TransformationStatus.SUCCESS));

					InputStream jsonStream = convert.getConversionResult();

					List<Map<?, ?>> aciSections = JsonHelper.readJsonAtJsonPath(jsonStream,
						"$.measurementSets[?(@.category=='aci')]", List.class);
					assertThat("There must not be any ACI sections in historical data.", aciSections, hasSize(0));
				} );
	}

}
