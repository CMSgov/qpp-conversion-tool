package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AuditServiceImplTest {
	private static final String AN_ID = "1234567890";

	@InjectMocks
	private AuditServiceImpl underTest;

	@Mock
	private StorageService storageService;

	@Spy
	private DbService dbService;

	private InputStream fileContent;
	private InputStream jsonContent;
	private Metadata metadata;

	@Before
	public void before() throws NoSuchFieldException, IllegalAccessException {
		fileContent = new ByteArrayInputStream("Hello".getBytes());
		JsonWrapper json = new JsonWrapper();
		json.putString("meep", "mawp");
		jsonContent = json.contentStream();

		metadata = new Metadata();
		Field metadataField = underTest.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(underTest, metadata);

		doReturn(CompletableFuture.completedFuture(metadata))
				.when(dbService).write(metadata);
	}

	@Test
	public void testAuditHappyPath() {
		allGood();
		underTest.audit(fileContent, jsonContent);

		assertThat(metadata.getQppLocator()).isSameAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameAs(AN_ID);
	}

	@Test
	public void testAuditHappyPathWrite() {
		allGood();
		underTest.audit(fileContent, jsonContent).join();

		verify(dbService, times(1)).write(metadata);
	}

	@Test
	public void testFileUploadFailureException() throws TimeoutException {
		problematic();
		final Waiter waiter = new Waiter();
		CompletableFuture<Void> future = underTest.audit(fileContent, jsonContent);

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


