package gov.cms.qpp.conversion;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jayway.jsonpath.PathNotFoundException;
import gov.cms.qpp.conversion.aws.history.HistoricalTestRunner;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static gov.cms.qpp.conversion.ConversionEntry.checkFlags;
import static gov.cms.qpp.conversion.ConversionEntry.cli;
import static gov.cms.qpp.conversion.ConversionEntry.validatedScope;
import static com.amazonaws.util.IOUtils.copy;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

@RunWith(HistoricalTestRunner.class)
public class ScopeTest {
	private static final String BUCKET = "qrda-history";

	@Test(expected=PathNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void historicalQrdaSansAci() throws IOException, ParseException {
		String[] args = {"-t", "ACI_SECTION", "-b"};
		validatedScope(checkFlags(cli(args)));

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
						status = convert.transform();
						InputStream result = convert.getConversionResult();
						copy(result, System.out);
					} catch (IOException ioe) {
						fail(ioe.getMessage());
					}

					assertThat("The file should have transformed correctly.", status, is(TransformationStatus.SUCCESS));

					InputStream jsonStream = convert.getConversionResult();

					//no scope means no transformation content
					List<Map<?, ?>> aciSections = JsonHelper.readJsonAtJsonPath(jsonStream,
						"$.scope", List.class);
				} );
	}
}
