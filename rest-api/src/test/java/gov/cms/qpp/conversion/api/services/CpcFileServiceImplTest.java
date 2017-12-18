package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CpcFileServiceImplTest {

	@InjectMocks
	private CpcFileServiceImpl objectUnderTest;

	@Mock
	private DbService dbService;

	@Mock
	private StorageService storageService;

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

	@Test
	void testGetFileById() throws IOException {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, false));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InputStreamResource outcome = objectUnderTest.getFileById("test");

		verify(dbService, times(1)).getMetadataById(anyString());
		verify(storageService, times(1)).getFileByLocationId(anyString());

		assertThat(IOUtils.toString(outcome.getInputStream(), Charset.defaultCharset())).isEqualTo("1337");
	}

	@Test
	void testGetFileByIdWithMips() throws IOException {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(false, false));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
				-> objectUnderTest.getFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}

	@Test
	void testGetFileByIdWithProcessedFile() throws IOException {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, true));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
				-> objectUnderTest.getFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}

	@Test
	void testGetFileByIdNoFile() throws IOException {
		when(dbService.getMetadataById(anyString())).thenReturn(null);
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
				-> objectUnderTest.getFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}

	Metadata buildFakeMetadata(boolean isCpc, boolean isCpcProcessed) {
		Metadata metadata = new Metadata();
		metadata.setCpc(isCpc);
		metadata.setCpcProcessed(isCpcProcessed);
		metadata.setSubmissionLocator("test");

		return metadata;
	}
}
