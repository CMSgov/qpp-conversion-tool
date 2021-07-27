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
import gov.cms.qpp.conversion.api.model.Report;
import gov.cms.qpp.conversion.api.model.Status;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;
import gov.cms.qpp.conversion.api.services.AdvancedApmFileService;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.test.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CpcFileControllerV1Test {

	private List<UnprocessedFileData> expectedUnprocessedFileDataList;

	@InjectMocks
	CpcFileControllerV1 cpcFileControllerV1;

	@Mock
	AdvancedApmFileService advancedApmFileService;

	FileStatusUpdateRequest fileStatusUpdateRequest;

	@BeforeEach
	void setUp() {
		expectedUnprocessedFileDataList = createMockedUnprocessedDataList();
		fileStatusUpdateRequest = new FileStatusUpdateRequest();
	}

	@AfterEach
	void turnOffFeatureFlag() {
		System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);
	}

	@Test
	void testUpdateFileWithNullBodyMarksAsProcessed() {
		cpcFileControllerV1.updateFile("mock", Constants.CPC_ORG,null);
		fileStatusUpdateRequest.setProcessed(true);
		verify(advancedApmFileService).updateFileStatus("mock", Constants.CPC_ORG, fileStatusUpdateRequest);
	}

	@Test
	void testGetUnprocessedFileList() {
		when(advancedApmFileService.getUnprocessedCpcPlusFiles(anyString())).thenReturn(expectedUnprocessedFileDataList);

		ResponseEntity<List<UnprocessedFileData>> qppResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles(Constants.CPC_ORG);

		verify(advancedApmFileService).getUnprocessedCpcPlusFiles(Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE);

		assertThat(qppResponse.getBody()).isEqualTo(expectedUnprocessedFileDataList);
	}

	@Test
	void testGetFileById() throws IOException {
		InputStreamResource valid = new InputStreamResource(new ByteArrayInputStream("1234".getBytes()));
		when(advancedApmFileService.getCpcFileById(anyString())).thenReturn(valid);

		ResponseEntity<InputStreamResource> response = cpcFileControllerV1.getFileById("meep");

		assertThat(IOUtils.toString(response.getBody().getInputStream(), StandardCharsets.UTF_8))
				.isEqualTo("1234");
	}

	@Test
	void testGetQppById() throws IOException {
		InputStreamResource valid = new InputStreamResource(new ByteArrayInputStream("1234".getBytes()));
		when(advancedApmFileService.getQppById(anyString())).thenReturn(valid);

		ResponseEntity<InputStreamResource> response = cpcFileControllerV1.getQppById("meep");

		assertThat(IOUtils.toString(response.getBody().getInputStream(), StandardCharsets.UTF_8))
			.isEqualTo("1234");
	}

	@Test
	void testGetQppByIdFeatureFlagDisabled() throws IOException {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<InputStreamResource> response = cpcFileControllerV1.getQppById("meep");

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

		ResponseEntity<List<UnprocessedFileData>> cpcResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles(Constants.CPC_ORG);

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testEndpoint1WithInvalidOrganization() {
		ResponseEntity<List<UnprocessedFileData>> cpcResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles("meep");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testEndpoint2WithFeatureFlagDisabled() throws IOException {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<InputStreamResource> cpcResponse = cpcFileControllerV1.getFileById("meep");

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

	@Test
	void testReportWithFeatureFlagDisabled() {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<Report> cpcResponse = report("test");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testReportWithEarlierMetadataVersion() {
		Metadata testMetadata = Metadata.create();
		testMetadata.setConversionStatus(true);
		testMetadata.setProgramName(UUID.randomUUID().toString());
		testMetadata.setMetadataVersion(-1);
		when(advancedApmFileService.getMetadataById("test")).thenReturn(testMetadata);

		ResponseEntity<Report> cpcResponse = report("test");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(cpcResponse.getBody()).isNull();
	}

	@Test
	void testReport() {
		Metadata testMetadata = Metadata.create();
		testMetadata.setConversionStatus(true);
		testMetadata.setProgramName(UUID.randomUUID().toString());
		when(advancedApmFileService.getMetadataById("test")).thenReturn(testMetadata);

		Report cpcResponse = report("test").getBody();

		assertThat(cpcResponse.getProgramName()).isEqualTo(testMetadata.getProgramName());
		assertThat(cpcResponse.getStatus()).isEqualTo(Status.ACCEPTED);
	}

	@Test
	void testReportWithWarnings() {
		Metadata testMetadata = Metadata.create();
		testMetadata.setConversionStatus(true);
		List<Detail> testDetails = new ArrayList<>();
		testDetails.add(new Detail());
		testMetadata.setWarnings(testDetails);
		when(advancedApmFileService.getMetadataById("test")).thenReturn(testMetadata);

		Report cpcResponse = report("test").getBody();

		assertThat(cpcResponse.getStatus()).isEqualTo(Status.ACCEPTED_WITH_WARNINGS);
	}

	@Test
	void testReportWithEmptyWarnings() {
		Metadata testMetadata = Metadata.create();
		testMetadata.setConversionStatus(true);
		List<Detail> testDetails = new ArrayList<>();
		testMetadata.setWarnings(testDetails);
		when(advancedApmFileService.getMetadataById("test")).thenReturn(testMetadata);

		Report cpcResponse = report("test").getBody();

		assertThat(cpcResponse.getStatus()).isEqualTo(Status.ACCEPTED);
	}

	@Test
	void testReportWithErrors() {
		Metadata testMetadata = Metadata.create();
		testMetadata.setConversionStatus(false);
		List<Detail> testDetails = new ArrayList<>();
		testMetadata.setErrors(testDetails);
		when(advancedApmFileService.getMetadataById("test")).thenReturn(testMetadata);

		Report cpcResponse = report("test").getBody();

		assertThat(cpcResponse.getStatus()).isEqualTo(Status.REJECTED);
	}

	private ResponseEntity<Report> report(String fileId) {
		return cpcFileControllerV1.getReportByFileId(fileId);
	}

	private ResponseEntity<String> markProcessed() {
		FileStatusUpdateRequest request = new FileStatusUpdateRequest();
		request.setProcessed(true);
		return cpcFileControllerV1.updateFile("meep", Constants.CPC_ORG, request);
	}

	private ResponseEntity<String> markUnprocessed() {
		FileStatusUpdateRequest request = new FileStatusUpdateRequest();
		request.setProcessed(false);
		return cpcFileControllerV1.updateFile("meep", Constants.RTI_ORG, request);
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
