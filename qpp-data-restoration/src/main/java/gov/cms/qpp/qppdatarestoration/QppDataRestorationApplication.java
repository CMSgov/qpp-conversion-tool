package gov.cms.qpp.qppdatarestoration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;

import java.util.List;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QppDataRestorationApplication {
	private static final Logger RESTORATION_LOG = LoggerFactory.getLogger(QppDataRestorationApplication.class);

	private static final AmazonDynamoDB DYNAMO_CLIENT =
		AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	private static AWSKMS awsKms = AWSKMSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	public static AmazonDynamoDB getDynamoClient() {
		return DYNAMO_CLIENT;
	}

	public static void main(String[] args) {
		if (args.length == 3) {
			String kmsKey = args[2];
			RESTORATION_LOG.info("Running with variables: " + args[0] + ", " + args[1] + ", " + kmsKey);
			List<Metadata> exportTableData = exportData(args[0], kmsKey);
			importData(exportTableData, args[1], kmsKey);
		} else {
			RESTORATION_LOG.info("To run the restoration script please run as follows: "
				+ "'java -jar qpp-data-restoration-0.0.1-SNAPSHOT.jar table-to-export-from table-to-import-into'");
		}
	}

	public static List<Metadata> exportData(String tableToExportFrom, String kmsKey) {
		RESTORATION_LOG.info("Performing export from table " + tableToExportFrom);
		DynamoDBMapper exportMapper = createDynamoDbMapper(DYNAMO_CLIENT,
			tableNameOverrideConfig(tableToExportFrom),
			encryptionTransformer(kmsKey));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		List<Metadata> scanResult = exportMapper.scan(Metadata.class, scanExpression);

		RESTORATION_LOG.info("Finished exporting from table " + tableToExportFrom);

		return scanResult;
	}

	public static void importData(final List<Metadata> metadataList, String tableToImportInto, String kmsKey) {
		RESTORATION_LOG.info("Performing import into table " + tableToImportInto);
		DynamoDBMapper importMapper = createDynamoDbMapper(DYNAMO_CLIENT,
			tableNameOverrideConfig(tableToImportInto),
			encryptionTransformer(kmsKey));
		metadataList.forEach(importMapper::save);
		RESTORATION_LOG.info("Finished importing into table " + tableToImportInto);
	}

	public static DynamoDBMapper createDynamoDbMapper(
		final AmazonDynamoDB dynamoDb,
		final DynamoDBMapperConfig config,
		final AttributeTransformer transformer) {
		return new DynamoDBMapper(dynamoDb, config, transformer);
	}

	private static DynamoDBMapperConfig tableNameOverrideConfig(String tableName) {
		return DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
			.build();
	}

	private static AttributeTransformer encryptionTransformer(String kmsKey) {
		return new AttributeEncryptor(new DirectKmsMaterialProvider(awsKms, kmsKey));
	}
}
