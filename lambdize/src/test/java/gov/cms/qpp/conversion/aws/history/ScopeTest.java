package gov.cms.qpp.conversion.aws.history;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

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

@RunWith(HistoricalTestRunner.class)
public class ScopeTest {
	private static final String BUCKET = "qrda-history";

	@Test
	@Ignore
	public void historicalQrdaSansAci() throws IOException {
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
