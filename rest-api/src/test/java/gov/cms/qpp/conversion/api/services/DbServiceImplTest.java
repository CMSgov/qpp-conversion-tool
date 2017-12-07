package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DbServiceImplTest {

	@InjectMocks
	private DbServiceImpl underTest;

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@BeforeEach
	void before() {
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

		Metadata metadataIn = new Metadata();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyZeroInteractions(dbMapper);
		assertWithMessage("The returned metadata must be an empty metadata.")
				.that(metadataOut.getUuid()).isNull();
	}

	@Test
	void testGetUnprocessedCpcPlusMetaData() {
		PaginatedQueryList<Metadata> mockMetadataList = mock(PaginatedQueryList.class);
		when(mockMetadataList.stream()).thenAnswer(invocationOnMock -> Stream.of(new Metadata(), new Metadata()));
		when(dbMapper.query(eq(Metadata.class), any(DynamoDBQueryExpression.class))).thenReturn(mockMetadataList);

		List<Metadata> metaDataList = underTest.getUnprocessedCpcPlusMetaData();

		verify(dbMapper, times(Constants.CPC_DYNAMO_PARTITIONS)).query(any(Class.class), any(DynamoDBQueryExpression.class));
		assertThat(metaDataList).hasSize(2 * Constants.CPC_DYNAMO_PARTITIONS);
	}

	private Metadata writeMeta() {
		return writeMeta(new Metadata());
	}

	private Metadata writeMeta(Metadata metadata) {
		CompletableFuture<Metadata> writeResult = underTest.write(metadata);
		return writeResult.join();
	}
}

