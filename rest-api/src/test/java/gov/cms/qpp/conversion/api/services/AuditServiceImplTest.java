package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.helper.MetadataHelper;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Node;
import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MetadataHelper.class})
public class AuditServiceImplTest {
	private static final String AN_ID = "1234567890";

	@InjectMocks
	private AuditServiceImpl underTest;

	@Mock
	private StorageService storageService;

	@Mock
	private DbService dbService;

	@Mock
	private Converter.ConversionReport report;

	private Metadata metadata;

	@Before
	public void before() throws NoSuchFieldException, IllegalAccessException {
		Node node = new Node();
		node.putValue("meep", "mawp");

		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("meep", "mawp");

		InputStream fileContent = new ByteArrayInputStream("Hello".getBytes());
		metadata = new Metadata();

		when(report.getFileInput()).thenReturn(fileContent);
		when(report.getDecoded()).thenReturn(node);
		when(report.getEncoded()).thenReturn(wrapper);

		mockStatic(MetadataHelper.class);
		when(MetadataHelper.generateMetadata(any(Node.class)))
				.thenReturn(metadata);
		doReturn(CompletableFuture.completedFuture(metadata))
				.when(dbService).write(metadata);
	}

	@Test
	public void testAuditHappyPath() {
		allGood();
		underTest.success(report);

		assertThat(metadata.getQppLocator()).isSameAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameAs(AN_ID);
	}

	@Test
	public void testAuditHappyPathWrite() {
		allGood();
		underTest.success(report);

		verify(dbService, times(1)).write(metadata);
	}

	@Test
	public void testFileUploadFailureException() throws TimeoutException {
		problematic();
		final Waiter waiter = new Waiter();
		CompletableFuture<Void> future = underTest.success(report);

		future.whenComplete((nada, ex) -> {
			waiter.assertNull(metadata.getQppLocator());
			waiter.assertNull(metadata.getSubmissionLocator());
			waiter.assertTrue(ex.getCause() instanceof UncheckedInterruptedException);
			waiter.resume();
		});

		waiter.await(5000);
	}

	private void allGood() {
		when(storageService.store(any(String.class), any(InputStream.class)))
				.thenReturn(CompletableFuture.completedFuture(AN_ID));
	}

	private void problematic() {
		when(storageService.store(any(String.class), any(InputStream.class)))
				.thenReturn(CompletableFuture.supplyAsync( () -> {
					throw new UncheckedInterruptedException(new InterruptedException());
				}));
	}

}


