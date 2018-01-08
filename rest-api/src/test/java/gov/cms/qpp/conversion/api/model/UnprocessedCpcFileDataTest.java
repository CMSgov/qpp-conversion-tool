package gov.cms.qpp.conversion.api.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.google.common.truth.Truth.assertThat;

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

	private static Stream<LocalDateTime> createdDateProvider() {
		Instant firstDateInstant = Instant.parse("2017-01-01T08:00:00.846Z");
		Instant secondDateInstant = Instant.parse("2018-01-01T08:00:00.846Z");
		return Stream.of(LocalDateTime.ofInstant(firstDateInstant, ZoneOffset.UTC),
				LocalDateTime.ofInstant(secondDateInstant, ZoneOffset.UTC));
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
}
