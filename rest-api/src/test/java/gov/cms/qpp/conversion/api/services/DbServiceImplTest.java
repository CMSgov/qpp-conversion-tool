package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

@ExtendWith(MockitoExtension.class)
class DbServiceImplTest {

	private DbServiceImpl underTest;

	@Mock
	private MetadataRepository metadataRepo;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@BeforeEach
	void before() {
		underTest = new DbServiceImpl(taskExecutor, environment, metadataRepo);
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	void testWriteByNull() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(metadataRepo, times(1)).save(any(Metadata.class));
	}

	@Test
	void testWriteByEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(metadataRepo, times(1)).save(any(Metadata.class));
	}

	@Test
	void testNoWriteBecauseNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("trueOrSomething");

		Metadata metadataIn = new Metadata();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyZeroInteractions(metadataRepo);
		assertWithMessage("The returned metadata must be an empty metadata.")
				.that(metadataOut.getUuid()).isNull();
	}

	@Test
	void testGetUnprocessedCpcPlusMetaData() {
		int size = ThreadLocalRandom.current().nextInt(10, 20);
		when(metadataRepo.getUnprocessedCpcPlusMetadata(any())).thenReturn(Stream.generate(Metadata::new).limit(size).collect(Collectors.toList()));

		List<Metadata> metaDataList = underTest.getUnprocessedCpcPlusMetaData();

		assertThat(metaDataList).hasSize(size);
	}

	@Test
	void testGetMetadataById() {
		String fakeUuid = "1337-f4ke-uuid";

		when(metadataRepo.findById(anyString())).thenReturn(Optional.of(new Metadata()));

		Metadata fakeMetadata = underTest.getMetadataById(fakeUuid);

		verify(metadataRepo, times(1)).findById(anyString());

		assertThat(fakeMetadata).isNotNull();
	}

	private Metadata writeMeta() {
		return writeMeta(new Metadata());
	}

	private Metadata writeMeta(Metadata metadata) {
		CompletableFuture<Metadata> writeResult = underTest.write(metadata);
		return writeResult.join();
	}
}
