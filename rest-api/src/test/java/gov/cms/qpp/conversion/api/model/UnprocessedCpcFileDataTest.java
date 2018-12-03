package gov.cms.qpp.conversion.api.model;

import java.time.Instant;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class UnprocessedCpcFileDataTest {

	private static Stream<String> uuidProvider() {
		return Stream.of("000099f2-1f9e-4261-8d60-e4bc294386d7", "006ce223-cbf2-4509-9159-a93524662985");
	}

	private static Stream<String> fileNameProvider() {
		return Stream.of("valid.xml", "aci_moof.xml");
	}

	private static Stream<String> apmIdProvider() {
		return Stream.of("T02789", "KF5RGI");
	}

	private static Stream<Instant> createdDateProvider() {
		Instant firstDateInstant = Instant.parse("2017-01-01T08:00:00.846Z");
		Instant secondDateInstant = Instant.parse("2018-01-01T08:00:00.846Z");
		return Stream.of(firstDateInstant, secondDateInstant);
	}

	private static Stream<Boolean> overallSuccessProvider() {
		return Stream.of(Boolean.TRUE, Boolean.FALSE);
	}

	private static Stream<Metadata> metadataProvider() {
		return uuidProvider()
			.flatMap(uuidLocator -> fileNameProvider()
				.flatMap(fileName -> apmIdProvider()
					.flatMap(apmId -> createdDateProvider()
						.flatMap(createdDate -> overallSuccessProvider()
							.map(overallSuccess -> {
			Metadata metadata = new Metadata();
			metadata.setUuid(uuidLocator);
			metadata.setFileName(fileName);
			metadata.setApm(apmId);
			metadata.setCreatedDate(createdDate);
			metadata.setOverallStatus(overallSuccess);
			return metadata;
		})))));
	}

	@ParameterizedTest
	@MethodSource("metadataProvider")
	void testConstructor(Metadata metadata) {

		UnprocessedCpcFileData cpcFileData = new UnprocessedCpcFileData(metadata);

		assertThat(cpcFileData.getFileId()).isEqualTo(metadata.getUuid());
		assertThat(cpcFileData.getFilename()).isEqualTo(metadata.getFileName());
		assertThat(cpcFileData.getApm()).isEqualTo(metadata.getApm());
		assertThat(cpcFileData.getConversionDate()).isEqualTo(metadata.getCreatedDate().toString());
		assertThat(cpcFileData.getValidationSuccess()).isEqualTo(metadata.getOverallStatus());
	}

	@Test
	@DisplayName("should give a string representation of its state")
	void testToString() {
		UnprocessedCpcFileData data = new UnprocessedCpcFileData(new Metadata());
		String strung = data.toString();
		assertThat(strung).matches(".*fileId.*filename.*apm.*conversionDate.*validationSuccess.*purpose.*");
	}
}
