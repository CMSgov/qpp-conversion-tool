package gov.cms.qpp.conversion.qppdatarestoration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main script to copy items between tables
 */
public class QppDataRestorationApplication {
	private static final Logger RESTORATION_LOG = LoggerFactory.getLogger(QppDataRestorationApplication.class);

	private static final AmazonDynamoDB DYNAMO_CLIENT =
		AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	public static AmazonDynamoDB getDynamoClient() {
		return DYNAMO_CLIENT;
	}

	/**
	 * Main method for handling the export and import of tables.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			List<Map<String, AttributeValue>> exportTableData = exportData(args[0]);
			importData(exportTableData, args[1]);
		} else {
			RESTORATION_LOG.info("To run the restoration script please run as follows: "
				+ "'java -jar qpp-data-restoration-0.0.1-SNAPSHOT.jar table-to-export-from table-to-import-into'");
		}
	}

	private static List<Map<String, AttributeValue>> exportData(String tableToExportFrom) {
		RESTORATION_LOG.info("Performing export from table " + tableToExportFrom);
		ScanRequest scanRequest = new ScanRequest().withTableName(tableToExportFrom).withLimit(100);

		ScanResult scanResult = DYNAMO_CLIENT.scan(scanRequest);
		List<Map<String, AttributeValue>> metadataList = scanResult.getItems();
		while (scanResult.getLastEvaluatedKey() != null && !scanResult.getLastEvaluatedKey().isEmpty()) {
			scanResult = DYNAMO_CLIENT.scan(new ScanRequest().withTableName(tableToExportFrom).withLimit(100)
				.withExclusiveStartKey(scanResult.getLastEvaluatedKey()));
			metadataList.addAll(scanResult.getItems());
			RESTORATION_LOG.info("Exported {} items into {}. Sleeping for one second now...",
				metadataList.size(), tableToExportFrom);
			pauseExecution();
		}
		RESTORATION_LOG.info("Finished exporting from table " + tableToExportFrom);

		return metadataList;
	}

	private static void importData(final List<Map<String, AttributeValue>> metadataList, String tableToImportInto) {
		RESTORATION_LOG.info("Performing import into table " + tableToImportInto);
		AtomicInteger count = new AtomicInteger();

		metadataList.forEach(metadataMap -> {
			int itemPosition = count.incrementAndGet();
			if ( itemPosition % 100 == 0) {
				RESTORATION_LOG.info("Imported {} items into {}. Sleeping for one second now...",
					itemPosition, tableToImportInto);
				pauseExecution();
			}
			DYNAMO_CLIENT.putItem(tableToImportInto, metadataMap);
		});
		RESTORATION_LOG.info("Finished importing into table " + tableToImportInto);
	}

	private static void pauseExecution() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			RESTORATION_LOG.info("Sleep has been interrupted!");
		}
	}
}
