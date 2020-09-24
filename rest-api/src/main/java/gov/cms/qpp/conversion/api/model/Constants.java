package gov.cms.qpp.conversion.api.model;

import java.util.HashMap;

/**
 * Constants library for use within the ReST API.
 */
public class Constants {
	public static final String DYNAMO_TABLE_NAME_ENV_VARIABLE = "DYNAMO_TABLE_NAME";
	public static final String KMS_KEY_ENV_VARIABLE = "KMS_KEY";
	public static final String NO_AUDIT_ENV_VARIABLE = "NO_AUDIT";
	public static final String BUCKET_NAME_ENV_VARIABLE = "BUCKET_NAME";
	public static final String SUBMISSION_API_TOKEN_ENV_VARIABLE = "SUBMISSION_API_TOKEN";
	public static final String VALIDATION_URL_ENV_VARIABLE = "VALIDATION_URL";
	public static final String NO_CPC_PLUS_API_ENV_VARIABLE = "NO_CPC_PLUS_API";
	public static final String V1_API_ACCEPT = "application/vnd.qpp.cms.gov.v1+json";
	public static final String V2_API_ACCEPT = "application/vnd.qpp.cms.gov.v2+json";
	public static final Integer CPC_DYNAMO_PARTITIONS = 32;
	public static final String CPC_DYNAMO_PARTITION_START = "CPC_";
	public static final String DYNAMO_CPC_ATTRIBUTE = "Cpc";
	public static final String DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE = "CpcProcessed_CreateDate";
	public static final String DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE = "RtiProcessed_CreateDate";
	public static final String DYNAMO_CREATE_DATE_ATTRIBUTE = "CreateDate";
	public static final String CPC_PLUS_BUCKET_NAME_VARIABLE = "CPC_PLUS_BUCKET_NAME";
	public static final String CPC_PLUS_FILENAME_VARIABLE = "CPC_PLUS_VALIDATION_FILE";
	public static final String CPC_PLUS_UNPROCESSED_FILE_SEARCH_DATE_VARIABLE = "CPC_PLUS_UNPROCESSED_FILTER_START_DATE";
	public static final HashMap<String, String> ORG_ATTRIBUTE_MAP;
	public static final String CPC_ORG = "cpcplus";
	public static final String RTI_ORG = "rti";
	public static final String APM_FILE_NAME_KEY = "apm_entity_ids.json";

	static {
		HashMap<String, String> mapBuilder = new HashMap<>();
		mapBuilder.put(CPC_ORG, DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE);
		mapBuilder.put(RTI_ORG,DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE);
		ORG_ATTRIBUTE_MAP = mapBuilder;
	}

	/**
	 * Library utility class so the constructor is private and empty.
	 */
	private Constants() {
		//empty
	}
}
