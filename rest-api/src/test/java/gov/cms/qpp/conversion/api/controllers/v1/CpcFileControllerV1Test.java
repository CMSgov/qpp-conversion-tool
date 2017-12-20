package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import gov.cms.qpp.test.MockitoExtension;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CpcFileControllerV1Test {

	private List<UnprocessedCpcFileData> expectedUnprocessedCpcFileDataList;

	@InjectMocks
	CpcFileControllerV1 cpcFileControllerV1;

	@Mock
	CpcFileService cpcFileService;

	@BeforeEach
	void setUp() {
		expectedUnprocessedCpcFileDataList = createMockedUnprocessedDataList();
	}

	@AfterEach
	void turnOffFeatureFlag() {
		System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);
	}

	@Test
	void testGetUnprocessedFileList() {
		when(cpcFileService.getUnprocessedCpcPlusFiles()).thenReturn(expectedUnprocessedCpcFileDataList);

		ResponseEntity<List<UnprocessedCpcFileData>> qppResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles();

		verify(cpcFileService).getUnprocessedCpcPlusFiles();

		assertThat(qppResponse.getBody()).isEqualTo(expectedUnprocessedCpcFileDataList);
	}

	@Test
	void testGetFileById() throws IOException {
		InputStreamResource valid = new InputStreamResource(new ByteArrayInputStream("1234".getBytes()));
		when(cpcFileService.getFileById(anyString())).thenReturn(valid);

		ResponseEntity<InputStreamResource> response = cpcFileControllerV1.getFileById("meep");

		assertThat(IOUtils.toString(response.getBody().getInputStream(), Charset.defaultCharset()))
				.isEqualTo("1234");
	}

	@Test
	void testEndpoint1WithFeatureFlagDisabled() {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<List<UnprocessedCpcFileData>> cpcResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles();

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void testEndpoint2WithFeatureFlagDisabled() throws IOException {
		System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "trueOrWhatever");

		ResponseEntity<InputStreamResource> cpcResponse = cpcFileControllerV1.getFileById("meep");

		assertThat(cpcResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	List<UnprocessedCpcFileData> createMockedUnprocessedDataList() {
		Metadata metadata = new Metadata();
		metadata.setSubmissionLocator("Test");
		metadata.setFileName("TestFile.xml");
		metadata.setApm("TestApmEntity");
		metadata.setCreatedDate(new Date());
		metadata.setOverallStatus(true);

		UnprocessedCpcFileData unprocessedCpcFileData = new UnprocessedCpcFileData(metadata);
		List<UnprocessedCpcFileData> unprocessedCpcFileDataList = new ArrayList<>();
		unprocessedCpcFileDataList.add(unprocessedCpcFileData);

		return unprocessedCpcFileDataList;
	}
}
