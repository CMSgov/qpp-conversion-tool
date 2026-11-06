package gov.cms.qpp.conversion.api.services.internal;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.internal.DbServiceImpl;
import gov.cms.qpp.test.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

@ExtendWith(MockitoExtension.class)
class DbServiceImplTest {

	private DbServiceImpl underTest;

	@Mock
	private DynamoDbTable<Metadata> metadataTable;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@BeforeEach
	void before() {
		Optional<DynamoDbTable<Metadata>> tableWrapper = Optional.of(metadataTable);
		underTest = new DbServiceImpl(taskExecutor, tableWrapper, environment);
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	void testGetUnprocessedPcfMetaDataWithMissingDynamoDbMapper() {
		underTest = new DbServiceImpl(taskExecutor, Optional.empty(), environment);
		assertThat(underTest.getUnprocessedPcfMetaData(Constants.CPC_ORG)).isEmpty();
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
		verify(metadataTable, times(1)).putItem(any(Metadata.class));
	}

	@Test
	void testWriteByEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(metadataTable, times(1)).putItem(any(Metadata.class));
	}

	@Test
	void testNoWriteBecauseNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("trueOrSomething");

		Metadata metadataIn = Metadata.create();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyNoInteractions(metadataTable);
		assertWithMessage("The returned metadata must be an empty metadata.")
				.that(metadataOut.getUuid()).isNull();
	}

	@Test
	void testGetUnprocessedPcfMetaData() {
		when(environment.getProperty(Constants.CPC_PLUS_UNPROCESSED_FILE_SEARCH_DATE_VARIABLE)).thenReturn("2020-01-01");

		int itemsPerPartition = 11;
		int itemLimitPerpartition = 10;

		DynamoDbIndex<Metadata> index = mock(DynamoDbIndex.class);
		when(metadataTable.index(Constants.DYNAMO_PCF_ATTRIBUTE + "-" + Constants.CPC_ORG + "-index")).thenReturn(index);
		List<Metadata> items = new ArrayList<>();
		for (int indexNumber = 0; indexNumber < itemsPerPartition; indexNumber++) {
			items.add(Metadata.create());
		}
		SdkIterable<Page<Metadata>> pages = () -> Stream.of(Page.create(items)).iterator();
		when(index.query(any(QueryEnhancedRequest.class))).thenReturn(pages);

		List<Metadata> metaDataList = underTest.getUnprocessedPcfMetaData(Constants.CPC_ORG);

		verify(index, times(Constants.CPC_DYNAMO_PARTITIONS)).query(any(QueryEnhancedRequest.class));
		assertThat(metaDataList).hasSize(itemLimitPerpartition * Constants.CPC_DYNAMO_PARTITIONS);
	}

	@Test
	void testGetMetadataById() {
		String fakeUuid = "1337-f4ke-uuid";

		when(metadataTable.getItem(any(Key.class))).thenReturn(Metadata.create());

		Metadata fakeMetadata = underTest.getMetadataById(fakeUuid);

		verify(metadataTable, times(1)).getItem(any(Key.class));

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
