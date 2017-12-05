package gov.cms.qpp.conversion.api.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

class UnprocessedCpcFileDataTest {

	private static Stream<String> uuidProvider() {
		return Stream.of("000099f2-1f9e-4261-8d60-e4bc294386d7", "006ce223-cbf2-4509-9159-a93524662985");
	}

	private static Stream<String> submissionLocatorProvider() {
		return Stream.of("00001111-1234-4321-5678-abc1230987poiu", "123ew123-ab12-cd34-1337-b09845673543");
	}

	private static Stream<String> fileNameProvider() {
		return Stream.of("valid.xml", "aci_moof.xml");
	}

	private static Stream<String> apmIdProvider() {
		return Stream.of("T02789", "KF5RGI");
	}

	private static Stream<Date> createdDateProvider() {
		return Stream.of(new Date(954982730L), new Date(23546L));
	}

	private static Stream<Boolean> overallSuccessProvider() {
		return Stream.of(Boolean.TRUE, Boolean.FALSE);
	}

	private static Stream<Metadata> metadataProvider() {
		return uuidProvider()
			.flatMap(uuidLocator -> fileNameProvider()
				.flatMap(submissionLocator -> submissionLocatorProvider()
					.flatMap(fileName -> apmIdProvider()
						.flatMap(apmId -> createdDateProvider()
							.flatMap(createdDate -> overallSuccessProvider()
								.map(overallSuccess -> {
			Metadata metadata = new Metadata();
			metadata.setUuid(uuidLocator);
			metadata.setSubmissionLocator(submissionLocator);
			metadata.setFileName(fileName);
			metadata.setApm(apmId);
			metadata.setCreatedDate(createdDate);
			metadata.setOverallStatus(overallSuccess);
			return metadata;
		}))))));
	}

	@ParameterizedTest
	@MethodSource("metadataProvider")
	void testConstructor(Metadata metadata) {

		UnprocessedCpcFileData cpcFileData = new UnprocessedCpcFileData(metadata);

		assertThat(cpcFileData.getFileDataId()).isEqualTo(metadata.getUuid());
		assertThat(cpcFileData.getFileLocationId()).isEqualTo(metadata.getSubmissionLocator());
		assertThat(cpcFileData.getFilename()).isEqualTo(metadata.getFileName());
		assertThat(cpcFileData.getApm()).isEqualTo(metadata.getApm());
		assertThat(cpcFileData.getConversionDate()).isEqualTo(metadata.getCreatedDate().toString());
		assertThat(cpcFileData.getValidationSuccess()).isEqualTo(metadata.getOverallStatus());
	}
}
