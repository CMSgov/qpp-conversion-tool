package gov.cms.qpp.conversion.api.model;

/**
 * Constants library for use within the ReST API.
 */
public class Constants {
	public static final String API_LOG = "API_LOG";
	public static final String DYNAMO_TABLE_NAME_ENV_VARIABLE = "DYNAMO_TABLE_NAME";
	public static final String KMS_KEY_ENV_VARIABLE = "KMS_KEY";
	public static final String NO_AUDIT_ENV_VARIABLE = "NO_AUDIT";
	public static final String BUCKET_NAME_ENV_VARIABLE = "BUCKET_NAME";
	public static final String SUBMISSION_API_TOKEN_ENV_VARIABLE = "SUBMISSION_API_TOKEN";
	public static final String VALIDATION_URL_ENV_VARIABLE = "VALIDATION_URL";
	public static final String USE_SYNC_EXECUTOR = "USE_SYNC_EXECUTOR";
	public static final String V1_API_ACCEPT = "application/vnd.qpp.cms.gov.v1+json";
	public static final Integer CPC_DYNAMO_PARTITIONS = 32;
	public static final String CPC_DYNAMO_PARTITION_START = "CPC_";
	public static final String DYNAMO_CPC_ATTRIBUTE = "Cpc";
	public static final String DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE = "CpcProcessed_CreateDate";

	/**
	 * Library utility class so the constructor is private and empty.
	 */
	private Constants() {
		//empty
	}
}
