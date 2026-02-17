package gov.cms.qpp.conversion.api.controllers.v2;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.exceptions.BadZipException;
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZipControllerTest {

	@InjectMocks
	private ZipController objectUnderTest;

	@Mock
	private QrdaService qrdaService;

	@Mock
	private ValidationService validationService;

	@Mock
	private AuditService auditService;

	@Mock
	private ConversionReport report;

	@BeforeEach
	void setup() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put("key", "Good Qpp");

		when(report.getEncodedWithMetadata()).thenReturn(wrapper);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(null);
	}

	@Test
	void uploadQrdaFile_happyPath_returnsResponsesForFiles() throws Exception {
		byte[] zipBytes = zipOf(
				entry("a.xml", "<xml/>".getBytes()),
				entry("b.xml", "<xml/>".getBytes())
		);

		MultipartFile multipartFile =
				new MockMultipartFile("good.zip", "good.zip", "application/zip", zipBytes);

		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(auditService.success(any(ConversionReport.class))).thenReturn(null);

		ResponseEntity<List<ConvertResponse>> response =
				objectUnderTest.uploadQrdaFile(multipartFile, null);

		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody().get(0).getQpp()).isNotNull();
		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));
	}

	@Test
	void uploadQrdaFile_skipsDirectoryEntries() throws Exception {
		byte[] zipBytes = zipOf(
				dir("folder/"),
				entry("folder/a.xml", "<xml/>".getBytes())
		);

		MultipartFile multipartFile = new MockMultipartFile("good.zip", "good.zip", "application/zip", zipBytes);

		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(auditService.success(any(ConversionReport.class))).thenReturn(null);

		ResponseEntity<List<ConvertResponse>> response = objectUnderTest.uploadQrdaFile(multipartFile, null);

		assertThat(response.getBody()).hasSize(2);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));
	}

	@Test
	void uploadQrdaFile_invalidZipBytes_throwsBadZipException() {
		MultipartFile multipartFile = new MockMultipartFile(
				"bad.zip", "bad.zip", "application/zip", "not-a-zip".getBytes());

		assertThrows(BadZipException.class, () -> objectUnderTest.uploadQrdaFile(multipartFile, null));
	}

	@Test
	void uploadQrdaFile_whenTransferToThrowsIOException_throwsUncheckedIOException() throws Exception {
		MultipartFile brokenFile = org.mockito.Mockito.mock(MultipartFile.class);

		org.mockito.Mockito.doThrow(new IOException("boom"))
				.when(brokenFile)
				.transferTo(any(java.io.File.class));

		assertThrows(UncheckedIOException.class, () -> objectUnderTest.uploadQrdaFile(brokenFile, null));
	}

	@Test
	void uploadQrdaFile_whenAuditReturnsMetadata_setsLocation() throws Exception {
		byte[] zipBytes = zipOf(entry("a.xml", "<xml/>".getBytes()));
		MultipartFile multipartFile =
				new MockMultipartFile("good.zip", "good.zip", "application/zip", zipBytes);

		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);

		Metadata metadata = Metadata.create();
		metadata.setUuid("uuid-123");

		when(auditService.success(any(ConversionReport.class)))
				.thenReturn(CompletableFuture.completedFuture(metadata));

		ResponseEntity<List<ConvertResponse>> response =
				objectUnderTest.uploadQrdaFile(multipartFile, null);

		assertThat(response.getBody()).hasSize(1);
		assertThat(response.getBody().get(0).getLocation()).isEqualTo("uuid-123");
	}

	private static ZipSpec entry(String name, byte[] bytes) {
		return new ZipSpec(name, bytes, false);
	}

	private static ZipSpec dir(String name) {
		return new ZipSpec(name, new byte[0], true);
	}

	private static byte[] zipOf(ZipSpec... specs) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (ZipSpec spec : specs) {
				ZipEntry ze = new ZipEntry(spec.name);
				zos.putNextEntry(ze);
				if (!spec.isDir && spec.bytes.length > 0) {
					zos.write(spec.bytes);
				}
				zos.closeEntry();
			}
		}
		return baos.toByteArray();
	}

	private static final class ZipSpec {
		private final String name;
		private final byte[] bytes;
		private final boolean isDir;

		private ZipSpec(String name, byte[] bytes, boolean isDir) {
			this.name = name;
			this.bytes = bytes;
			this.isDir = isDir;
		}
	}
}
