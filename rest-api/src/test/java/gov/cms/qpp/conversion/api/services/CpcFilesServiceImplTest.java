package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CpcFilesServiceImplTest {

	@InjectMocks
	private CpcFilesServiceImpl objectUnderTest;

	@Mock
	private DbService dbService;

	private static Stream<Integer> numberOfMetadata() {
		return Stream.of(1, 4, 26);
	}

	@ParameterizedTest
	@MethodSource("numberOfMetadata")
	void testConversionToCpcFileData(Integer numberOfMetadata) {

		List<Metadata> metadataList = Stream.generate(Metadata::new).limit(numberOfMetadata).collect(Collectors.toList());

		when(dbService.getUnprocessedCpcPlusMetaData()).thenReturn(metadataList);

		assertThat(objectUnderTest.getUnprocessedCpcPlusFiles()).hasSize(numberOfMetadata);
	}
}
