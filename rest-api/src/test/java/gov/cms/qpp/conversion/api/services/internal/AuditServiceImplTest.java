package gov.cms.qpp.conversion.api.services.internal;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.helper.MetadataHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.api.services.internal.AuditServiceImpl;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Node;
import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetadataHelper.class})
@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*", "com.sun.org.apache.xerces.*" })
public class AuditServiceImplTest {
	private static final String AN_ID = "1234567890";
	private static final String FILENAME = "file";

	@InjectMocks
	private AuditServiceImpl underTest;

	@Mock
	private StorageService storageService;

	@Mock
	private DbService dbService;

	@Mock
	private ConversionReport report;

	@Mock
	private Environment environment;

	private Metadata metadata;
	private String content = "Hello";
	private Source fileContentSource = new InputStreamSupplierSource(FILENAME, new ByteArrayInputStream(content.getBytes()));

	@Before
	public void before() {
		metadata = Metadata.create();

		mockStatic(MetadataHelper.class);
		when(MetadataHelper.generateMetadata(any(Node.class), any(MetadataHelper.Outcome.class)))
				.thenReturn(metadata);
		doReturn(CompletableFuture.completedFuture(metadata))
				.when(dbService).write(metadata);
	}

	@Test
	public void testAuditHappyPath() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);
		successfulEncodingPrep();
		allGood();
		underTest.success(report);

		assertThat(metadata.getQppLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getFileName()).isSameInstanceAs(FILENAME);
	}

	@Test
	public void testAuditHappyPathNoAuditIsEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");
		successfulEncodingPrep();
		allGood();
		underTest.success(report);

		assertThat(metadata.getQppLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getFileName()).isSameInstanceAs(FILENAME);
	}

	@Test
	public void testAuditHappyPathNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("yep");
		successfulEncodingPrep();
		allGood();
		underTest.success(report);

		verify(storageService, times(0)).store(any(String.class), any(), anyLong());
		verify(dbService, times(0)).write(metadata);
	}

	@Test
	public void testAuditHappyPathWrite() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);
		successfulEncodingPrep();
		allGood();
		underTest.success(report);

		verify(dbService, times(1)).write(metadata);
	}

	@Test
	public void testFileUploadFailureException() throws TimeoutException, InterruptedException {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);
		successfulEncodingPrep();
		problematic();
		Waiter waiter = new Waiter();
		CompletableFuture<Metadata> future = underTest.success(report);

		future.whenComplete((nada, ex) -> {
			waiter.assertNull(metadata.getQppLocator());
			waiter.assertNull(metadata.getSubmissionLocator());
			waiter.assertTrue(ex.getCause() instanceof UncheckedInterruptedException);
			waiter.resume();
		});

		waiter.await(5000);
	}


	@Test
	public void testAuditConversionFailureHappy() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);
		errorPrep();
		allGood();
		underTest.failConversion(report);

		assertThat(metadata.getConversionErrorLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameInstanceAs(AN_ID);
	}

	@Test
	public void testAuditConversionFailureNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("yep");
		errorPrep();
		allGood();
		underTest.failConversion(report);

		verify(storageService, times(0)).store(any(String.class), any(), anyLong());
		verify(dbService, times(0)).write(metadata);
	}

	@Test
	public void testAuditQppValidationFailureHappy() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);
		successfulEncodingPrep();
		errorPrep();
		allGood();
		underTest.failValidation(report);

		assertThat(metadata.getRawValidationErrorLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getValidationErrorLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getQppLocator()).isSameInstanceAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameInstanceAs(AN_ID);
	}

	@Test
	public void testAuditQppValidationFailureNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("yep");
		successfulEncodingPrep();
		errorPrep();
		allGood();
		underTest.failValidation(report);

		verify(storageService, times(0)).store(any(String.class), any(), anyLong());
		verify(dbService, times(0)).write(metadata);
	}

	private void successfulEncodingPrep() {
		prepOverlap();
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put("meep", "mawp");

		when(report.getQppSource()).thenReturn(wrapper.toSource());
	}

	private void errorPrep() {
		prepOverlap();


		when(report.getRawValidationErrorsOrEmptySource()).thenReturn(fileContentSource);
		when(report.getValidationErrorsSource()).thenReturn(fileContentSource);
	}

	private void prepOverlap() {
		Node node = new Node();
		node.putValue("meep", "mawp");

		when(report.getQrdaSource()).thenReturn(fileContentSource);
		when(report.getDecoded()).thenReturn(node);
	}

	private void allGood() {
		when(storageService.store(any(String.class), any(), anyLong()))
				.thenReturn(CompletableFuture.completedFuture(AN_ID));
	}

	private void problematic() {
		when(storageService.store(any(String.class), any(), anyLong()))
				.thenReturn(CompletableFuture.supplyAsync(() -> {
					throw new UncheckedInterruptedException(new InterruptedException());
				}));
	}

}


