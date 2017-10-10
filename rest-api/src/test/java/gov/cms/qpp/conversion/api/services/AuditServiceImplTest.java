package gov.cms.qpp.conversion.api.services;


import gov.cms.qpp.conversion.api.exceptions.UncheckedInterruptedException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class AuditServiceImplTest {
	private static final String AN_ID = "1234567890";

	@InjectMocks
	private AuditServiceImpl underTest;

	private AuditServiceImpl spy;
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

		spy = spy(underTest);
		doReturn(allGood()).when(spy).storeContent(fileContent);
		doReturn(allGood()).when(spy).storeContent(jsonContent);
	}

	@Test
	public void testAuditHappyPath() {
		spy.audit(fileContent, jsonContent);

		assertThat(metadata.getQppLocator()).isSameAs(AN_ID);
		assertThat(metadata.getSubmissionLocator()).isSameAs(AN_ID);
		verify(spy, times(1)).persist(isNull(), isNull());
	}

	@Test
	public void testFileUploadFailure(){
		doReturn(problematic()).when(spy).storeContent(any());

		spy.audit(fileContent, jsonContent);

		assertThat(metadata.getQppLocator()).isNull();
		assertThat(metadata.getSubmissionLocator()).isNull();
		verify(spy, times(1)).persist(isNull(), any(CompletionException.class));
	}


	private CompletableFuture<String> allGood() {
		return CompletableFuture.completedFuture(AN_ID);
	}
	private CompletableFuture problematic() {
		return CompletableFuture.supplyAsync( () -> {
			throw new UncheckedInterruptedException(new InterruptedException());
		});
	}

}


