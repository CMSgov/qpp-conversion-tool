package gov.cms.qpp.conversion.model;

import java.util.Set;

/**
 * Constants library for use within the Validate/Encode/Decode packages.
 */
public class Constants {

    public static final String AGGREGATE_COUNT = "aggregateCount";
    public static final String MEASURE_TYPE = "type";
    public static final String MEASURE_ID = "measureId";
    public static final String TEMPLATE_ID = "templateId";
    public static final String PERFORMANCE_NOT_MET = "performanceNotMet";
    public static final String MEASURE_POPULATION = "populationId";
    public static final String STRATUM_FIELD_NAME = "stratum";
    public static final String IS_END_TO_END_REPORTED = "isEndToEndReported";
    public static final String DEFAULT_INT_VALUE = "0";
    public static final String VALUE = "value";
    public static final String CATEGORY_SECTION_V5 = "clinicalDocumentV5";
    public static final String MEASUREMENT_SETS = "measurementSets";
    public static final String QUALITY_SECTION = "quality";
    public static final String SUBMISSION = "submission";
    public static final String SUBMISSION_METHOD = "submissionMethod";
    public static final String PERFORMANCE_START = "performanceStart";
    public static final String PERFORMANCE_END = "performanceEnd";
    public static final String PERFORMANCE_YEAR = "performanceYear";
    public static final String PERFORMANCE_RATE = "rate";
    public static final String NULL_PERFORMANCE_RATE = "nullRate";
    public static final String PERFORMANCE_RATE_ID = "performanceRateUuid";
    public static final String SINGLE_PERFORMANCE_RATE = "singlePerformanceRate";
    public static final String ROOT_STRING = "root";
    public static final String EXTENSION_STRING = "extension";
    public static final String SUPPLEMENTAL_DATA_CODE = "code";
    public static final String SUPPLEMENTAL_DATA_KEY = "supplementalData";
    public static final String SUPPLEMENTAL_DATA_PAYER_CODE = "payerCode";
    public static final String CATEGORY = "category";
    public static final String TRUE = "true";
    public static final int YEAR_LAST_INDEX = 4;
    public static final String NOT_VALID_QRDA_III_FORMAT = "The file is not a QRDA-III XML document";

    /*  Constants for lookups and tests */
    // Identifier constants for: Node(Identifier, Value) and xpathlocation
    public static final String NATIONAL_PROVIDER_IDENTIFIER = "nationalProviderIdentifier";
    public static final String TAX_PAYER_IDENTIFICATION_NUMBER = "taxpayerIdentificationNumber";
    public static final String PROGRAM_NAME = "programName";
    public static final String ENTITY_TYPE = "entityType";
    public static final String RAW_PROGRAM_NAME = "rawProgramName";
    public static final String PRACTICE_SITE_ADDR = "practiceSiteAddr";
    public static final String PRACTICE_ID = "practiceId";
    public static final String ENTITY_ID = "entityId";
    public static final String PCF_ENTITY_ID = "pcfEntityId";
    public static final String APM_ENTITY_ID = "apmEntityId";
    public static final String VG_ID = "virtualGroupId";
    public static final String CEHRT = "cehrtId";
    public static final String MVP_ID = "mvpId";
    public static final String SUBGROUP_ID = "subgroupId";

    //QPP Json value constants for: Node(Identifier, value)
    public static final String MIPS_PROGRAM_NAME = "mips";
    public static final String PCF_PROGRAM_NAME = "pcf";
    public static final String CPCPLUS_PROGRAM_NAME = "cpcPlus";
    public static final String ENTITY_APM = "apm";
    public static final String ENTITY_GROUP = "group";
    public static final String ENTITY_INDIVIDUAL = "individual";
    public static final String ENTITY_SUBGROUP = "subgroup";
    public static final String ENTITY_VIRTUAL_GROUP = "virtualGroup";
    public static final String APP_PROGRAM_NAME = "app1";
    public static final String MIPS = "MIPS";
    public static final Set<String> MVP_ENTITIES = Set.of(ENTITY_INDIVIDUAL, ENTITY_GROUP, ENTITY_SUBGROUP, ENTITY_APM);

    // Program names in XML format
    public static final String PCF = "PCF";
    public static final String APP = "APP";
    public static final String CPCPLUS = "CPCPLUS";
    public static final String MIPS_GROUP = "MIPS_GROUP";
    public static final String MIPS_INDIVIDUAL = "MIPS_INDIV";
    public static final String MIPS_APM = "MIPS_APMENTITY";
    public static final String MIPS_VIRTUAL_GROUP = "MIPS_VIRTUALGROUP";
    public static final String MIPS_SUBGROUP = "MIPS_SUBGROUP";
    public static final String APP_GROUP = "MIPS_APP1_GROUP";
    public static final String APP_INDIVIDUAL = "MIPS_APP1_INDIV";
    public static final String APP_APM = "MIPS_APP1_APMENTITY";

    // Library utility class so the constructor is private and empty.
    private Constants() {}
}
