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

	@BeforeEach
	void setUp() {
		expectedUnprocessedPcfFileDataList = createMockedUnprocessedDataList();
	}

	@AfterEach
	void turnOffFeatureFlag() {
		System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);
	}

	@Test
	void testUpdateFileWithNullBodyMarksAsProcessed() {
		pcfFileControllerV1.updateFile("mock", Constants.CPC_ORG,null);
		verify(advancedApmFileService).processFileById("mock", Constants.CPC_ORG);
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
		when(advancedApmFileService.processFileById(anyString(), anyString())).thenReturn("success!");

		ResponseEntity<String> response = markProcessed();

		verify(advancedApmFileService, times(1)).processFileById("meep", Constants.CPC_ORG);

		assertThat(response.getBody()).isEqualTo("success!");
	}

	@Test
	void testMarkFileAsProcessedHttpStatusOk() {
		when(advancedApmFileService.processFileById(anyString(), anyString())).thenReturn("success!");

		ResponseEntity<String> response = markProcessed();

		verify(advancedApmFileService, times(1)).processFileById("meep", Constants.CPC_ORG);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testMarkFileAsUnprocessedReturnsSuccess() {
		when(advancedApmFileService.unprocessFileById(anyString(), anyString())).thenReturn("success!");

		ResponseEntity<String> response = markUnprocessed();

		verify(advancedApmFileService, times(1)).unprocessFileById("meep", Constants.RTI_ORG);

		assertThat(response.getBody()).isEqualTo("success!");
	}

	@Test
	void testMarkFileAsUnprocessedHttpStatusOk() {
		when(advancedApmFileService.unprocessFileById(anyString(), anyString())).thenReturn("success!");

		ResponseEntity<String> response = markUnprocessed();

		verify(advancedApmFileService, times(1)).unprocessFileById("meep", Constants.RTI_ORG);

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
		FileStatusUpdateRequest request = new FileStatusUpdateRequest();
		request.setProcessed(true);
		return pcfFileControllerV1.updateFile("meep", Constants.CPC_ORG, request);
	}

	private ResponseEntity<String> markUnprocessed() {
		FileStatusUpdateRequest request = new FileStatusUpdateRequest();
		request.setProcessed(false);
		return pcfFileControllerV1.updateFile("meep", Constants.RTI_ORG, request);
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
