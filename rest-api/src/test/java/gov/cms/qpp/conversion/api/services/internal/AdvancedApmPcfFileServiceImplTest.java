package gov.cms.qpp.conversion.api.services.internal;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;

import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.helper.AdvancedApmHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.test.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvancedApmPcfFileServiceImplTest {

	private static final String test = "test";

	@InjectMocks
	private AdvancedApmFileServiceImpl objectUnderTest;

	@Mock
	private DbService dbService;

	@Mock
	private StorageService storageService;

	FileStatusUpdateRequest fileStatusUpdateRequest;

	private static Stream<Integer> numberOfMetadata() {
		return Stream.of(1, 4, 26);
	}

	@ParameterizedTest
	@MethodSource("numberOfMetadata")
	void testConversionToPcfFileData(Integer numberOfMetadata) {

		List<Metadata> metadataList = Stream.generate(Metadata::new).limit(numberOfMetadata).collect(Collectors.toList());

		when(dbService.getUnprocessedPcfMetaData(anyString())).thenReturn(metadataList);

		assertThat(objectUnderTest.getUnprocessedPcfFiles(Constants.CPC_ORG)).hasSize(numberOfMetadata);
	}

	@Test
	void testGetQppById() throws IOException {
		String key = "test";
		when(dbService.getMetadataById(key)).thenReturn(buildFakeMetadata(true, false, false));
		when(storageService.getFileByLocationId(key)).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InputStreamResource outcome = objectUnderTest.getQppById(key);

		verify(dbService, times(1)).getMetadataById(key);
		verify(storageService, times(1)).getFileByLocationId(key);

		assertThat(IOUtils.toString(outcome.getInputStream(), StandardCharsets.UTF_8)).isEqualTo("1337");
	}

	@Test
	void testGetQppByIdProcessed() throws IOException {
		String key = "test";
		when(dbService.getMetadataById(key)).thenReturn(buildFakeMetadata(true, true, false));
		when(storageService.getFileByLocationId(key)).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InputStreamResource outcome = objectUnderTest.getQppById(key);

		verify(dbService, times(1)).getMetadataById(key);
		verify(storageService, times(1)).getFileByLocationId(key);

		assertThat(IOUtils.toString(outcome.getInputStream(), StandardCharsets.UTF_8)).isEqualTo("1337");
	}

	@Test
	void testGetQppByIdWithMips() throws IOException {
		String key = "test";
		when(dbService.getMetadataById(key)).thenReturn(buildFakeMetadata(false, false, false));
		when(storageService.getFileByLocationId(key)).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InvalidFileTypeException expectedException = assertThrows(InvalidFileTypeException.class, ()
			-> objectUnderTest.getQppById(key));

		verify(dbService, times(1)).getMetadataById(key);

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.INVALID_FILE);
	}

	@Test
	void testGetQppByIdNoFile() {
		String key = "test";
		when(dbService.getMetadataById(key)).thenReturn(null);
		when(storageService.getFileByLocationId(key)).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
			-> objectUnderTest.getPcfFileById(key));

		verify(dbService, times(1)).getMetadataById(key);

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testGetFileById() throws IOException {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, false, false));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InputStreamResource outcome = objectUnderTest.getPcfFileById("test");

		verify(dbService, times(1)).getMetadataById(anyString());
		verify(storageService, times(1)).getFileByLocationId(anyString());

		assertThat(IOUtils.toString(outcome.getInputStream(), StandardCharsets.UTF_8)).isEqualTo("1337");
	}

	@Test
	void testGetFileByIdWithMips() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(false, false, false));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		InvalidFileTypeException expectedException = assertThrows(InvalidFileTypeException.class, ()
			-> objectUnderTest.getPcfFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.INVALID_FILE);
	}

	@Test
	void testGetFileByIdWithProcessedFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, true, true));
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
			-> objectUnderTest.getPcfFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testGetFileByIdNoFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(null);
		when(storageService.getFileByLocationId("test")).thenReturn(new ByteArrayInputStream("1337".getBytes()));

		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
			-> objectUnderTest.getPcfFileById("test"));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testProcessFileByIdSuccess() {
		Metadata returnedData = buildFakeMetadata(true, false, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);
		when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(returnedData));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		String message = objectUnderTest.updateFileStatus(test, Constants.CPC_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);
		verify(dbService, times(1)).write(returnedData);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_FOUND_PROCESSED);
	}

	@Test
	void testRtiProcessFileByIdSuccess() {
		Metadata returnedData = buildFakeMetadata(true, true, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);
		when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(returnedData));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		String message = objectUnderTest.updateFileStatus(test, Constants.RTI_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);
		verify(dbService, times(1)).write(returnedData);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_FOUND_PROCESSED);
	}

	@Test
	void testProcessFileByIdWrongOrg() {
		Metadata returnedData = buildFakeMetadata(true, true, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		String message = objectUnderTest.updateFileStatus(test, test, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testProcessFileByIdFileNotFound() {
		when(dbService.getMetadataById(anyString())).thenReturn(null);

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
			-> objectUnderTest.updateFileStatus("test", Constants.CPC_ORG, fileStatusUpdateRequest));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testProcessFileByIdWithMipsFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(false, false, false));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		InvalidFileTypeException expectedException = assertThrows(InvalidFileTypeException.class, ()
			-> objectUnderTest.updateFileStatus("test", Constants.CPC_ORG, fileStatusUpdateRequest));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.INVALID_FILE);
	}

	@Test
	void testProcessFileByIdWithProcessedFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, true, false));
		when(dbService.write(any())).thenReturn(CompletableFuture.completedFuture(
			buildFakeMetadata(true, true, false)));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(true);
		String response = objectUnderTest.updateFileStatus("test", Constants.CPC_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(response).isEqualTo(AdvancedApmHelper.FILE_FOUND_PROCESSED);
	}

	@Test
	void testUnprocessFileByIdSuccess() {
		Metadata returnedData = buildFakeMetadata(true, true, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);
		when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(returnedData));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		String message = objectUnderTest.updateFileStatus(test, Constants.CPC_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);
		verify(dbService, times(1)).write(returnedData);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_FOUND_UNPROCESSED);
	}

	@Test
	void testUnprocessFileByIdFileNotFound() {
		when(dbService.getMetadataById(anyString())).thenReturn(null);

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		NoFileInDatabaseException expectedException = assertThrows(NoFileInDatabaseException.class, ()
			-> objectUnderTest.updateFileStatus("test", "meep", fileStatusUpdateRequest));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testUnprocessRtiFileByIdSuccess() {
		Metadata returnedData = buildFakeMetadata(true, true, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);
		when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(returnedData));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		String message = objectUnderTest.updateFileStatus(test, Constants.RTI_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);
		verify(dbService, times(1)).write(returnedData);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_FOUND_UNPROCESSED);
	}

	@Test
	void testUnprocessFileByIdWrongOrg() {
		Metadata returnedData = buildFakeMetadata(true, true, false);
		when(dbService.getMetadataById(anyString())).thenReturn(returnedData);

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		String message = objectUnderTest.updateFileStatus(test, test, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(test);

		assertThat(message).isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testUnprocessFileByIdWithMipsFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(false, false, false));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		InvalidFileTypeException expectedException = assertThrows(InvalidFileTypeException.class, ()
			-> objectUnderTest.updateFileStatus("test", Constants.CPC_ORG, fileStatusUpdateRequest));

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(expectedException).hasMessageThat().isEqualTo(AdvancedApmHelper.INVALID_FILE);
	}

	@Test
	void testUnprocessFileByIdWithProcessedFile() {
		when(dbService.getMetadataById(anyString())).thenReturn(buildFakeMetadata(true, false, false));
		when(dbService.write(any())).thenReturn(CompletableFuture.completedFuture(
			buildFakeMetadata(true, false, false)));

		fileStatusUpdateRequest = new FileStatusUpdateRequest();
		fileStatusUpdateRequest.setProcessed(false);
		String response = objectUnderTest.updateFileStatus("test", Constants.CPC_ORG, fileStatusUpdateRequest);

		verify(dbService, times(1)).getMetadataById(anyString());

		assertThat(response).isEqualTo(AdvancedApmHelper.FILE_FOUND_UNPROCESSED);
	}

	Metadata buildFakeMetadata(boolean isPcf, boolean isCpcProcessed, boolean isRtiProcessed) {
		Metadata metadata = Metadata.create();
		metadata.setPcf(isPcf ? "PCF_26" : null);
		metadata.setCpcProcessed(isCpcProcessed);
		metadata.setRtiProcessed(isRtiProcessed);
		metadata.setSubmissionLocator("test");
		metadata.setQppLocator("test");

		return metadata;
	}
}
