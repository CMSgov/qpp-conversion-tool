package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
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
	private DynamoDBMapper dbMapper;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@BeforeEach
	void before() {
		Optional<DynamoDBMapper> dbMapperWrapper = Optional.of(dbMapper);
		underTest = new DbServiceImpl(taskExecutor, dbMapperWrapper, environment);
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	void testGetUnprocessedCpcPlusMetaDataWithMissingDynamoDbMapper() {
		underTest = new DbServiceImpl(taskExecutor, Optional.empty(), environment);
		assertThat(underTest.getUnprocessedCpcPlusMetaData()).isEmpty();
	}

	@Test
	void testGetMetadataByIdWithMissingDynamoDbMapper() {
		underTest = new DbServiceImpl(taskExecutor, Optional.empty(), environment);
		assertThat(underTest.getMetadataById(null)).isNull();
	}

	@Test
	void testWriteByNull() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	void testWriteByEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	void testNoWriteBecauseNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("trueOrSomething");

		Metadata metadataIn = Metadata.create();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyNoInteractions(dbMapper);
		assertWithMessage("The returned metadata must be an empty metadata.")
				.that(metadataOut.getUuid()).isNull();
	}

	@Test
	@SuppressWarnings("unchecked")
	void testGetUnprocessedCpcPlusMetaData() {
		int itemsPerPartition = 2;

		PaginatedQueryList<Metadata> mockMetadataPage = mock(PaginatedQueryList.class);
		when(mockMetadataPage).thenReturn(Stream.generate(Metadata::new).limit(itemsPerPartition).collect(Collectors.toList()));
		when(dbMapper.query(eq(Metadata.class), any(DynamoDBQueryExpression.class))).thenReturn(mockMetadataPage);

		List<Metadata> metaDataList = underTest.getUnprocessedCpcPlusMetaData();

		verify(dbMapper, times(Constants.CPC_DYNAMO_PARTITIONS)).queryPage(eq(Metadata.class), any(DynamoDBQueryExpression.class));
		assertThat(metaDataList).hasSize(itemsPerPartition * Constants.CPC_DYNAMO_PARTITIONS);
	}

	@Test
	void testGetMetadataById() {
		String fakeUuid = "1337-f4ke-uuid";

		when(dbMapper.load(eq(Metadata.class), anyString())).thenReturn(Metadata.create());

		Metadata fakeMetadata = underTest.getMetadataById(fakeUuid);

		verify(dbMapper, times(1)).load(eq(Metadata.class), anyString());

		assertThat(fakeMetadata).isNotNull();
	}

	private Metadata writeMeta() {
		return writeMeta(Metadata.create());
	}

	private Metadata writeMeta(Metadata metadata) {
		CompletableFuture<Metadata> writeResult = underTest.write(metadata);
		return writeResult.join();
	}
}
