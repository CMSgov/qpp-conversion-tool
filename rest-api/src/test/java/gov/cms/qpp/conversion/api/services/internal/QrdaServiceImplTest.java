package gov.cms.qpp.conversion.api.services.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QrdaServiceImplTest {

	private static final Source GOOD_SOURCE =
			new InputStreamSupplierSource("Good Qrda", new ByteArrayInputStream("<xml/>".getBytes()));
	private static final Source ERROR_SOURCE =
			new InputStreamSupplierSource("Error Qrda", new ByteArrayInputStream("<xml/>".getBytes()));

	private static final String KEY = "key";
	private static final String GOOD_QPP = "Good Qpp";

	@Test
	void convertQrda3ToQpp_success_callsTransform_andReturnsReport() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = spy(new QrdaServiceImpl(storage));

		Converter converter = mock(Converter.class);
		ConversionReport report = mock(ConversionReport.class);

		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put(KEY, GOOD_QPP);

		when(service.initConverter(GOOD_SOURCE)).thenReturn(converter);

		when(converter.transform()).thenReturn(wrapper);

		when(converter.getReport()).thenReturn(report);
		when(report.getEncodedWithMetadata()).thenReturn(wrapper);

		ConversionReport result = service.convertQrda3ToQpp(GOOD_SOURCE);

		verify(converter).transform();
		verify(converter).getReport();
		assertThat(result.getEncodedWithMetadata().getString(KEY)).isEqualTo(GOOD_QPP);
	}

	@Test
	void convertQrda3ToQpp_whenTransformThrows_propagatesTransformException() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = spy(new QrdaServiceImpl(storage));

		Converter converter = mock(Converter.class);

		AllErrors allErrors = new AllErrors();
		allErrors.addError(new Error("Error Identifier", null));

		ConversionReport report = mock(ConversionReport.class);
		when(report.getReportDetails()).thenReturn(allErrors);

		TransformException boom = new TransformException("mock problem", new NullPointerException(), report);

		when(service.initConverter(ERROR_SOURCE)).thenReturn(converter);
		when(converter.transform()).thenThrow(boom);

		assertThrows(TransformException.class, () -> service.convertQrda3ToQpp(ERROR_SOURCE));
	}

	@Test
	void retrieveCpcPlusValidationFile_delegatesToStorageService() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = new QrdaServiceImpl(storage);

		InputStream expected = new ByteArrayInputStream("x".getBytes());
		when(storage.getCpcPlusValidationFile()).thenReturn(expected);

		InputStream actual = service.retrieveCpcPlusValidationFile();

		assertThat(actual).isSameInstanceAs(expected);
		verify(storage).getCpcPlusValidationFile();
	}

	@Test
	void retrieveApmValidationFile_delegatesToStorageService() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = new QrdaServiceImpl(storage);

		InputStream expected = new ByteArrayInputStream("{}".getBytes());
		when(storage.getApmValidationFile("file.json")).thenReturn(expected);

		InputStream actual = service.retrieveApmValidationFile("file.json");

		assertThat(actual).isSameInstanceAs(expected);
		verify(storage).getApmValidationFile("file.json");
	}

	@Test
	void loadApmData_thenInitConverter_fetchesApmFile_once_dueToMemoization() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = new QrdaServiceImpl(storage);

		when(storage.getApmValidationFile(Constants.PCF_APM_FILE_NAME_KEY)).thenReturn(null);

		service.loadApmData();

		assertThat(service.initConverter(GOOD_SOURCE)).isNotNull();
		assertThat(service.initConverter(GOOD_SOURCE)).isNotNull();

		verify(storage, times(1)).getApmValidationFile(Constants.PCF_APM_FILE_NAME_KEY);
	}

	@Test
	void preloadMeasureConfigs_forCoverage() {
		StorageService storage = mock(StorageService.class);
		QrdaServiceImpl service = new QrdaServiceImpl(storage);

		service.preloadMeasureConfigs();
	}
}
