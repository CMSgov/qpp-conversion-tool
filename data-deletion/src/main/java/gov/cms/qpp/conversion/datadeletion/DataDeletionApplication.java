package gov.cms.qpp.conversion.datadeletion;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataDeletionApplication {
	private static final Logger DELETION_LOG = LoggerFactory.getLogger(DataDeletionApplication.class);

	private static final AmazonDynamoDB DYNAMO_CLIENT =
		AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	public static AmazonDynamoDB getDynamoClient() {
		return DYNAMO_CLIENT;
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			deleteData(args[0]);
		} else {
			DELETION_LOG.info("To run the deletion script please run as follows: "
				+ "'java -jar delete-data-0.0.1-SNAPSHOT.jar table-to-delete-from'");
		}
	}

	private static void deleteData(String tableToDeleteFrom) {
		DELETION_LOG.info("Performing delete from table " + tableToDeleteFrom);
		ScanResult scanResult = DYNAMO_CLIENT.scan(tableToDeleteFrom, Lists.newArrayList("Uuid"));
		List<Map<String, AttributeValue>> metadataList = scanResult.getItems();
		while (scanResult.getLastEvaluatedKey() != null && !scanResult.getLastEvaluatedKey().isEmpty()) {
			scanResult = DYNAMO_CLIENT.scan(new ScanRequest().withTableName(tableToDeleteFrom)
				.withAttributesToGet("Uuid")
				.withLimit(100)
				.withExclusiveStartKey(scanResult.getLastEvaluatedKey()));
			metadataList.addAll(scanResult.getItems());
			DELETION_LOG.info("Scanned 100 items... Sleeping for one second...");
			pauseExecution();
		}

		AtomicInteger count = new AtomicInteger();
		metadataList.forEach(map -> {
			int itemPosition = count.incrementAndGet();
			if (itemPosition % 100 == 0) {
				DELETION_LOG.info("Deleted {} items from {}. Sleeping for one second...",
					itemPosition, tableToDeleteFrom);
				pauseExecution();
			}
			DYNAMO_CLIENT.deleteItem(tableToDeleteFrom, map);
		});
		DELETION_LOG.info("Finished deleting from table " + tableToDeleteFrom);
	}

	private static void pauseExecution() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			DELETION_LOG.info("Sleep has been interrupted!");
		}
	}
}
