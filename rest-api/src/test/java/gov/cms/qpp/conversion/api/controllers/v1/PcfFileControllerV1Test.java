package gov.cms.qpp.conversion.api.controllers.v1;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;
import gov.cms.qpp.conversion.api.services.AdvancedApmFileService;
import gov.cms.qpp.test.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PcfFileControllerV1Test {
	private List<UnprocessedFileData> expectedUnprocessedPcfFileDataList;

	@InjectMocks
	PcfFileControllerV1 pcfFileControllerV1;

	@Mock
	AdvancedApmFileService advancedApmFileService;

	FileStatusUpdateRequest fileStatusUpdateRequest;

	@BeforeEach
	void setUp() {
		expectedUnprocessedPcfFileDataList = createMockedUnprocessedDataList();
		fileStatusUpdateRequest = new FileStatusUpdateRequest();
	}

	@AfterEach
	void turnOffFeatureFlag() {
		System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);
	}

	@Test
	void testUpdateFileWithNullBodyMarksAsProcessed() {
		pcfFileControllerV1.updateFile("mock", Constants.CPC_ORG,null);
		verify(advancedApmFileService).updateFileStatus("mock", Constants.CPC_ORG, null);
	}

	@Test
	void testGetUnprocessedFileList() {
		when(advancedApmFileService.getUnprocessedPcfFiles(anyString())).thenReturn(expectedUnprocessedPcfFileDataList);

		ResponseEntity<List<UnprocessedFileData>> qppResponse = pcfFileControllerV1.getUnprocessedPcfPlusFiles(Constants.CPC_ORG);

		verify(advancedApmFileService).getUnprocessedPcfFiles(Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE);

		assertThat(qppResponse.getBody()).isEqualTo(expectedUnprocessedPcfFileDataList);
	}

	@Test
	void testGetFileById() throws IOException {
		InputStreamResource valid = new InputStreamResource(new ByteArrayInputStream("1234".getBytes()));
		when(advancedApmFileService.getPcfFileById(anyString())).thenReturn(valid);

		ResponseEntity<InputStreamResource> response = pcfFileControllerV1.getFileById("meep");

		assertThat(IOUtils.toString(response.getBody().getInputStream(), StandardCharsets.UTF_8))
			.isEqualTo("1234");
	}

	@Test
	void testGetQppById() throws IOException {
		InputStreamResource valid = new InputStreamResource(new ByteArrayInputStream("1234".getBytes()));
		when(advancedApmFileService.getQppById(anyString())).thenReturn(valid);

		ResponseEntity<InputStreamResource> response = pcfFileControllerV1.getQppById("meep");

		assertThat(IOUtils.toString(response.getBody().getInputStream(), StandardCharsets.UTF_8))
			.isEqualTo("1234");
	}

	@Test
	void testGetQppByIdFeatureFlagDisabled() throws IOException {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<InputStreamResource> response = pcfFileControllerV1.getQppById("meep");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void testMarkFileAsProcessedReturnsSuccess() {
		when(advancedApmFileService.updateFileStatus(anyString(), anyString(), any(FileStatusUpdateRequest.class))).thenReturn("success!");

		fileStatusUpdateRequest.setProcessed(true);
		ResponseEntity<String> response = markProcessed();

		verify(advancedApmFileService, times(1)).updateFileStatus("meep", Constants.CPC_ORG, fileStatusUpdateRequest);

		assertThat(response.getBody()).isEqualTo("success!");
	}

	@Test
	void testMarkFileAsProcessedHttpStatusOk() {
		when(advancedApmFileService.updateFileStatus(anyString(), anyString(), any(FileStatusUpdateRequest.class))).thenReturn("success!");

		fileStatusUpdateRequest.setProcessed(true);
		ResponseEntity<String> response = markProcessed();

		verify(advancedApmFileService, times(1)).updateFileStatus("meep", Constants.CPC_ORG, fileStatusUpdateRequest);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testEndpoint1WithFeatureFlagDisabled() {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<List<UnprocessedFileData>> cpcResponse = pcfFileControllerV1.getUnprocessedPcfPlusFiles(Constants.CPC_ORG);

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testEndpoint1WithInvalidOrganization() {
		ResponseEntity<List<UnprocessedFileData>> cpcResponse = pcfFileControllerV1.getUnprocessedPcfPlusFiles("meep");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testEndpoint2WithFeatureFlagDisabled() throws IOException {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<InputStreamResource> cpcResponse = pcfFileControllerV1.getFileById("meep");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testEndpoint3WithFeatureFlagDisabled() {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<String> cpcResponse = markProcessed();

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	private ResponseEntity<String> markProcessed() {
		fileStatusUpdateRequest.setProcessed(true);
		return pcfFileControllerV1.updateFile("meep", Constants.CPC_ORG, fileStatusUpdateRequest);
	}

	private ResponseEntity<String> markUnprocessed() {
		fileStatusUpdateRequest.setProcessed(false);
		return pcfFileControllerV1.updateFile("meep", Constants.RTI_ORG, fileStatusUpdateRequest);
	}

	List<UnprocessedFileData> createMockedUnprocessedDataList() {
		Metadata metadata = Metadata.create();
		metadata.setSubmissionLocator("Test");
		metadata.setFileName("TestFile.xml");
		metadata.setApm("TestApmEntity");
		metadata.setCreatedDate(Instant.now());
		metadata.setOverallStatus(true);

		UnprocessedFileData unprocessedFileData = new UnprocessedFileData(metadata);
		List<UnprocessedFileData> unprocessedFileDataList = new ArrayList<>();
		unprocessedFileDataList.add(unprocessedFileData);

		return unprocessedFileDataList;
	}
}
