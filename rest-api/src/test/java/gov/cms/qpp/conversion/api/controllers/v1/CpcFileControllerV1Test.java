package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedCpcFileData;
import gov.cms.qpp.conversion.api.services.CpcFileService;
import gov.cms.qpp.test.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

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

	@Test
	void testGetUnprocessedFileList() throws IOException {
		when(cpcFileService.getUnprocessedCpcPlusFiles()).thenReturn(expectedUnprocessedCpcFileDataList);

		ResponseEntity qppResponse = cpcFileControllerV1.getUnprocessedCpcPlusFiles();

		verify(cpcFileService).getUnprocessedCpcPlusFiles();

		assertThat(qppResponse.getBody()).isEqualTo(expectedUnprocessedCpcFileDataList);
	}

	@Test
	void testGetFileById() throws IOException {
		InputStream valid = new ByteArrayInputStream("1234".getBytes());
		when(cpcFileService.getFileById(anyString())).thenReturn(valid);

		ResponseEntity<String> response = cpcFileControllerV1.getFileById("meep");

		assertThat(response.getBody()).isEqualTo("1234");
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
