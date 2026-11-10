package gov.cms.qpp.conversion.api.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.util.JsonMapperFactory;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.enhancedclient.DynamoDbEncryptionSignOnly;

/**
 * Model to hold conversion metadata. Maps to a table in DynamoDB.
 */
@DynamoDbBean
public final class Metadata {

	public static Metadata create() {
		Metadata metadata = new Metadata();
		metadata.setMetadataVersion(Metadata.CURRENT_VERSION);
		return metadata;
	}

	private static final int CPC_PROCESSED_CREATE_DATE_NUM_FIELDS = 2;
	private static final int CPC_PROCESSED_INDEX = 0;
	private static final int CPC_CREATE_DATE_INDEX = 1;
	public static final int CURRENT_VERSION = 3;
	public static final int UNKNOWN_VERSION = -1;

	private String uuid;
	private String tin;  //this field is encrypted
	private String npi;
	private String apm;
	private String programName;
	private Long submissionYear;
	private String submissionLocator;
	private String qppLocator;
	private String fileName;  //this field is encrypted
	private Boolean overallStatus;
	private Boolean conversionStatus;
	private Boolean validationStatus;
	private String cpc;
	private String pcf;
	private String conversionErrorLocator;
	private String validationErrorLocator;
	private String rawValidationErrorLocator;
	private Instant createdDate;
	private Boolean cpcProcessed;
	private Boolean rtiProcessed;
	private String purpose;
	private Details errors;
	private Details warnings;
	private Integer metadataVersion;

	/**
	 * Constructs a new {@code Metadata} with the {@code createdDate} filled in upon construction.
	 */
	public Metadata() {
		createdDate = Instant.now();
		metadataVersion = UNKNOWN_VERSION;
	}

	/**
	 * The UUID that uniquely identifies this item.
	 *
	 * @return The UUID.
	 */
	@DynamoDbPartitionKey
	@DynamoDbAttribute("Uuid")
	public String getUuid() {
		return uuid;
	}

	/**
	 * There is no reason to set this manually because it is automatically generated when saved to DynamoDB.
	 *
	 * @param uuid The UUID to use.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * The date and time when this item was created.
	 *
	 * @return The date and time.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("CreateDate")
	@DynamoDbConvertedBy(InstantConverter.class)
	public Instant getCreatedDate() {
		return createdDate;
	}

	/**
	 * There is no reason to set this manually because it is automatically generated when saved to DynamoDB.
	 *
	 * @param createdDate The date and time to use.
	 */
	public void setCreatedDate(Instant createdDate) {
		if (createdDate != null) {
			this.createdDate = createdDate;
		} else {
			this.createdDate = null;
		}
	}

	/**
	 * The TIN associated with the the conversion.
	 *
	 * @return The TIN.
	 */
	@DynamoDbAttribute("Tin")
	public String getTin() {
		return tin;
	}

	/**
	 * Sets the TIN associated with the the conversion.
	 *
	 * @param tin The TIN to use.
	 */
	public void setTin(String tin) {
		this.tin = tin;
	}

	/**
	 * The NPI associated with the the conversion.
	 *
	 * @return The NPI.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("Npi")
	public String getNpi() {
		return npi;
	}

	/**
	 * Sets the NPI associated with the the conversion
	 *
	 * @param npi The NPI to use.
	 */
	public void setNpi(String npi) {
		this.npi = npi;
	}

	/**
	 * The APM Entity ID associated with the the conversion. Used with CPC+.
	 *
	 * @return The APM Entity ID.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("Apm")
	public String getApm() {
		return apm;
	}

	/**
	 * Sets the APM Entity ID associated with the the conversion. Used with CPC+.
	 *
	 * @param apm The APM Entity ID.
	 */
	public void setApm(String apm) {
		this.apm = apm;
	}

	/**
	 * The Program Name associated with the the conversion, such as MIPPS or CPC+.
	 *
	 * @return The Program Name.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("ProgramName")
	public String getProgramName() {
		return programName;
	}

	/**
	 * Sets the Program Name associated with the the conversion, such as MIPPS or CPC+.
	 *
	 * @param programName The Program Name.
	 */
	public void setProgramName(String programName) {
		this.programName = programName;
	}

	/**
	 * The submission year associated with the the conversion.
	 *
	 * @return The submission year.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("SubmissionYear")
	public Long getSubmissionYear() {
		return submissionYear;
	}

	/**
	 * Sets the submission year associated with the the conversion
	 *
	 * @param submissionYear The submission year to use.
	 */
	public void setSubmissionYear(Long submissionYear) {
		this.submissionYear = submissionYear;
	}

	/**
	 * A location to where the submitted file can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("SubmissionLocator")
	public String getSubmissionLocator() {
		return submissionLocator;
	}

	/**
	 * Sets a location to where the submitted file can be found.
	 *
	 * @param submissionLocator The location to use.
	 */
	public void setSubmissionLocator(String submissionLocator) {
		this.submissionLocator = submissionLocator;
	}

	/**
	 * A location where the submission QPP can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("QPPLocator")
	public String getQppLocator() {
		return qppLocator;
	}

	/**
	 * Sets a location where the submission QPP can be found.
	 *
	 * @param qppLocator The location to use.
	 */
	public void setQppLocator(String qppLocator) {
		this.qppLocator = qppLocator;
	}

	/**
	 * The file name of the file uploaded to the converter.
	 *
	 * @return The file name.
	 */
	@DynamoDbAttribute("FileName")
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name of the file uploaded to the converter.
	 *
	 * @param fileName The file name to use.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * The success or failure of the conversion and the submission validation.
	 *
	 * @return Success or failure.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("OverallStatus")
	@DynamoDbConvertedBy(LegacyBooleanConverter.class)
	public Boolean getOverallStatus() {
		return overallStatus;
	}

	/**
	 * Sets the overall status of the conversion and submission validation.
	 *
	 * @param overallStatus The overall status to use.
	 */
	public void setOverallStatus(Boolean overallStatus) {
		this.overallStatus = overallStatus;
	}

	/**
	 * The purpose of the conversion, or null if it's a standard conversion
	 *
	 * @return The purpose of the conversion, for example "Test"
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("Purpose")
	public String getPurpose() {
		return purpose;
	}

	/**
	 * Sets the purpose of the conversion
	 *
	 * @param purpose The purpose of the conversion
	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/**
	 * The success or failure of only the conversion.
	 *
	 * @return Success or failure.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("ConversionStatus")
	@DynamoDbConvertedBy(LegacyBooleanConverter.class)
	public Boolean getConversionStatus() {
		return conversionStatus;
	}

	/**
	 * Sets the conversion status.
	 *
	 * @param conversionStatus The conversion status to use.
	 */
	public void setConversionStatus(Boolean conversionStatus) {
		this.conversionStatus = conversionStatus;
	}

	/**
	 * The success or failure of only the submission validation.
	 *
	 * @return Success or failure.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("ValidationStatus")
	@DynamoDbConvertedBy(LegacyBooleanConverter.class)
	public Boolean getValidationStatus() {
		return validationStatus;
	}

	/**
	 * Sets the submission validation status.
	 *
	 * @param validationStatus The validation status to use.
	 */
	public void setValidationStatus(Boolean validationStatus) {
		this.validationStatus = validationStatus;
	}

	/**
	 * Whether the conversion was for the CPC+ program.
	 *
	 * This is set to a {@link String} that contains "CPC_" plus a number for DynamoDB partitioning of the GSI.
	 * If this method returns {@code null}, this was not a CPC+ conversion.
	 *
	 * @return A {@link String} for a CPC+ conversion, null otherwise.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute(Constants.DYNAMO_CPC_ATTRIBUTE)
	public String getCpc() {
		return cpc;
	}

	/**
	 * Sets whether the conversion was for the CPC+ program.
	 *
	 * If not {@code null}, must be of the form "CPC_" plus a number.
	 * Setting this to {@code null}, indicates this was not a CPC+ conversion.
	 *
	 * @param cpc A CPC+ conversion or not.
	 */
	public void setCpc(String cpc) {
		this.cpc = cpc;
	}

	/**
	 * Determines if conversion was for PCF program
	 *
	 * @return
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute(Constants.DYNAMO_PCF_ATTRIBUTE)
	@DynamoDbSecondaryPartitionKey(indexNames = {
		Constants.DYNAMO_PCF_ATTRIBUTE + "-" + Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE + "-index",
		Constants.DYNAMO_PCF_ATTRIBUTE + "-" + Constants.DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE + "-index"})
	public String getPcf() {
		return pcf;
	}

	/**
	 * Manage if program is PCF
	 * @param pcf
	 */
	public void setPcf(final String pcf) {
		this.pcf = pcf;
	}

	/**
	 * A location to where the conversion error JSON can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("ConversionErrorLocator")
	public String getConversionErrorLocator() {
		return conversionErrorLocator;
	}

	/**
	 * Sets a location to where the conversion error JSON can be found.
	 *
	 * @param conversionErrorLocator A location.
	 */
	public void setConversionErrorLocator(String conversionErrorLocator) {
		this.conversionErrorLocator = conversionErrorLocator;
	}

	/**
	 * A location to where the submission validation error JSON can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("ValidationErrorLocator")
	public String getValidationErrorLocator() {
		return validationErrorLocator;
	}

	/**
	 * Sets a location to where the submission validation error JSON can be found.
	 *
	 * @param validationErrorLocator A location.
	 */
	public void setValidationErrorLocator(String validationErrorLocator) {
		this.validationErrorLocator = validationErrorLocator;
	}

	/**
	 * A location to where the raw submission validation error response can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("RawValidationErrorLocator")
	public String getRawValidationErrorLocator() {
		return rawValidationErrorLocator;
	}

	/**
	 * Sets a location to where the submission validation error JSON can be found.
	 *
	 * @param rawValidationErrorLocator A location.
	 */
	public void setRawValidationErrorLocator(String rawValidationErrorLocator) {
		this.rawValidationErrorLocator = rawValidationErrorLocator;
	}

	/**
	 * Whether the file was processed by the CPC+ team
	 *
	 * Ignored when writing to DynamoDB because {@code CpcProcessed_CreateDate} holds the pertinent information.
	 *
	 * @return Whether the file was processed.
	 */
	@DynamoDbIgnore
	public Boolean getCpcProcessed() {
		return cpcProcessed;
	}

	/**
	 * Sets whether the file was processed by the CPC+ team
	 *
	 * @param cpcProcessed
	 */
	public void setCpcProcessed(Boolean cpcProcessed) {
		this.cpcProcessed = cpcProcessed;
	}

	/**
	 * Whether the file was processed by the RTI+ team
	 *
	 * Ignored when writing to DynamoDB because {@code RtiProcessed_CreateDate} holds the pertinent information.
	 *
	 * @return Whether the file was processed.
	 */
	@DynamoDbIgnore
	public Boolean getRtiProcessed() {
		return rtiProcessed;
	}

	/**
	 * Sets whether the file was processed by the Rti+ team
	 *
	 * @param rtiProcessed
	 */
	public void setRtiProcessed(Boolean rtiProcessed) {
		this.rtiProcessed = rtiProcessed;
	}

	/**
	 * Returns an attribute that combines the CPC+ processed state and the date of creation.
	 *
	 * This is mostly useful in the CPC+ global secondary index.
	 *
	 * @return The combined attribute.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute(Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE)
	@DynamoDbSecondarySortKey(indexNames =
		Constants.DYNAMO_PCF_ATTRIBUTE + "-" + Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE + "-index")
	public String getCpcProcessedCreateDate() {
		String combination = null;

		if (cpcProcessed != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
			combination = cpcProcessed.toString() + "#" + formatter.format(createdDate);
		}

		return combination;
	}

	/**
	 * Returns an attribute that combines the RTI+ processed state and the date of creation.
	 *
	 * This is mostly useful in the RTI+ global secondary index.
	 *
	 * @return The combined attribute.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute(Constants.DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE)
	@DynamoDbSecondarySortKey(indexNames =
		Constants.DYNAMO_PCF_ATTRIBUTE + "-" + Constants.DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE + "-index")
	public String getRtiProcessedCreateDate() {
		String combination = null;

		if (rtiProcessed != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
			combination = rtiProcessed.toString() + "#" + formatter.format(createdDate);
		}

		return combination;
	}

	/**
	 * DynamoDB will store the Errors object as JSON.
	 * Suppress EI_EXPOSE_REP since callers should not modify the returned Details directly.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	@DynamoDbEncryptionSignOnly
	@DynamoDbConvertedBy(DetailsConverter.class)
	@DynamoDbAttribute("Errors")
	public Details getErrors() {
		return errors;
	}

	/**
	 * Defensive copy: wrap incoming list in a new Details instance to avoid leaking the original list.
	 *
	 * @param errors list of Detail
	 */
	@DynamoDbIgnore
	public void setErrors(List<Detail> errors) {
		if (errors == null) {
			this.errors = null;
		} else {
			Details d = new Details();
			d.setDetails(errors);
			this.errors = d;
		}
	}

	/**
	 * Defensive copy: clone the passed-in Details so we don't store a reference to a mutable object.
	 *
	 * @param errors Details to copy
	 */
	public void setErrors(Details errors) {
		if (errors == null) {
			this.errors = null;
		} else {
			Details d = new Details();
			d.setDetails(errors.getDetails());
			this.errors = d;
		}
	}

	/**
	 * DynamoDB will store the Warnings object as JSON.
	 * Suppress EI_EXPOSE_REP since callers should not modify the returned Details directly.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	@DynamoDbEncryptionSignOnly
	@DynamoDbConvertedBy(DetailsConverter.class)
	@DynamoDbAttribute("Warnings")
	public Details getWarnings() {
		return warnings;
	}

	/**
	 * Defensive copy: wrap incoming list in a new Details instance to avoid leaking the original list.
	 *
	 * @param warnings list of Detail
	 */
	@DynamoDbIgnore
	public void setWarnings(List<Detail> warnings) {
		if (warnings == null) {
			this.warnings = null;
		} else {
			Details d = new Details();
			d.setDetails(warnings);
			this.warnings = d;
		}
	}

	/**
	 * Defensive copy: clone the passed-in Details so we don't store a reference to a mutable object.
	 *
	 * @param warnings Details to copy
	 */
	public void setWarnings(Details warnings) {
		if (warnings == null) {
			this.warnings = null;
		} else {
			Details d = new Details();
			d.setDetails(warnings.getDetails());
			this.warnings = d;
		}
	}


	/**
	 * Sets the separate CPC+ processed flag and created date based on the argument
	 *
	 * Splits the processed flag from the date by a "#" character.
	 * The first field must be "true" or "false" which represents the CPC+ processed boolean.
	 * The second field must be an ISO 8601 timestamp string.
	 * For example, "2018-12-08T18:32:54.846Z".
	 *
	 * @param combination The combined attribute.
	 */
	public void setCpcProcessedCreateDate(String combination) {

		String[] split = combination.split("#");

		if (split.length < CPC_PROCESSED_CREATE_DATE_NUM_FIELDS) {
			return;
		}

		String isProcessed = split[CPC_PROCESSED_INDEX];
		String creationDate = split[CPC_CREATE_DATE_INDEX];
		Instant instant = Instant.parse(creationDate);

		setCpcProcessed(Boolean.valueOf(isProcessed));
		setCreatedDate(instant);
	}

	/**
	 * Sets the separate RTI+ processed flag and created date based on the argument
	 *
	 * Splits the processed flag from the date by a "#" character.
	 * The first field must be "true" or "false" which represents the RTI+ processed boolean.
	 * The second field must be an ISO 8601 timestamp string.
	 * For example, "2018-12-08T18:32:54.846Z".
	 *
	 * @param combination The combined attribute.
	 */
	public void setRtiProcessedCreateDate(String combination) {

		String[] split = combination.split("#");

		if (split.length < CPC_PROCESSED_CREATE_DATE_NUM_FIELDS) {
			return;
		}

		String isProcessed = split[CPC_PROCESSED_INDEX];
		String creationDate = split[CPC_CREATE_DATE_INDEX];
		Instant instant = Instant.parse(creationDate);

		setRtiProcessed(Boolean.valueOf(isProcessed));
		setCreatedDate(instant);
	}

	/**
	 * The version associated with the the Metadata object.
	 *
	 * @return The Metadata Version.
	 */
	@DynamoDbEncryptionSignOnly
	@DynamoDbAttribute("MetadataVersion")
	public Integer getMetadataVersion() {
		return metadataVersion;
	}

	/**
	 * Sets the version associated with the Metadata object
	 *
	 * @param metadataVersion The Metadata version to use.
	 */
	public void setMetadataVersion(Integer metadataVersion) {
		this.metadataVersion = metadataVersion;
	}

	/**
	 * Determines the equality between this object and another.
	 *
	 * @param o The other object.
	 * @return True if equal, false if not equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || o.getClass() != getClass()) {
			return false;
		}

		Metadata that = (Metadata) o;

		boolean equals = Objects.equals(submissionYear, that.submissionYear);
		equals &= Objects.equals(overallStatus, that.overallStatus);
		equals &= Objects.equals(conversionStatus, that.conversionStatus);
		equals &= Objects.equals(validationStatus, that.validationStatus);
		equals &= Objects.equals(cpc, that.cpc);
		equals &= Objects.equals(pcf, that.pcf);
		equals &= Objects.equals(uuid, that.uuid);
		equals &= Objects.equals(tin, that.tin);
		equals &= Objects.equals(npi, that.npi);
		equals &= Objects.equals(createdDate, that.createdDate);
		equals &= Objects.equals(apm, that.apm);
		equals &= Objects.equals(submissionLocator, that.submissionLocator);
		equals &= Objects.equals(qppLocator, that.qppLocator);
		equals &= Objects.equals(fileName, that.fileName);
		equals &= Objects.equals(conversionErrorLocator, that.conversionErrorLocator);
		equals &= Objects.equals(validationErrorLocator, that.validationErrorLocator);
		equals &= Objects.equals(rawValidationErrorLocator, that.rawValidationErrorLocator);
		equals &= Objects.equals(cpcProcessed, that.cpcProcessed);
		equals &= Objects.equals(rtiProcessed, that.rtiProcessed);
		equals &= Objects.equals(purpose, that.purpose);
		equals &= Objects.equals(programName, that.programName);
		equals &= Objects.equals(errors, that.errors);
		equals &= Objects.equals(warnings, that.warnings);
		equals &= Objects.equals(metadataVersion, that.metadataVersion);
		return equals;
	}

	/**
	 * Computes and returns the hash code for this object.
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(uuid, tin, npi, createdDate, apm, submissionYear, submissionLocator, qppLocator, fileName,
				overallStatus, conversionStatus, validationStatus, cpc, pcf, conversionErrorLocator, validationErrorLocator,
				rawValidationErrorLocator, cpcProcessed, rtiProcessed, purpose, programName, errors, warnings, metadataVersion);
	}

	/**
	 * Converter for DynamoDb to convert type {@link Instant} to a {@link String} in the database
	 */
	public static class InstantConverter implements AttributeConverter<Instant> {
		/**
		 * Converts the an Instant to the ISO INSTANT format string for db storage
		 *
		 * @param date Object to be converted
		 * @return Converted Object
		 */
		@Override
		public AttributeValue transformFrom(final Instant date) {
			return AttributeValue.builder().s(DateTimeFormatter.ISO_INSTANT.format(date)).build();
		}

		/**
		 * Converts the stored string attribute to an Instant for application usage.
		 *
		 * @param attributeValue the stored DynamoDB attribute
		 * @return the parsed Instant
		 */
		@Override
		public Instant transformTo(final AttributeValue attributeValue) {
			return Instant.parse(attributeValue.s());
		}

		@Override
		public EnhancedType<Instant> type() {
			return EnhancedType.of(Instant.class);
		}

		@Override
		public AttributeValueType attributeValueType() {
			return AttributeValueType.S;
		}
	}

	public static class DetailsConverter implements AttributeConverter<Details> {
		private static final ObjectMapper MAPPER = JsonMapperFactory.createMapper();

		@Override
		public AttributeValue transformFrom(Details details) {
			try {
				return AttributeValue.builder().s(MAPPER.writeValueAsString(details)).build();
			} catch (JacksonException exception) {
				throw new IllegalArgumentException("Unable to serialize metadata details", exception);
			}
		}

		@Override
		public Details transformTo(AttributeValue attributeValue) {
			if (attributeValue.s() == null) {
				return null;
			}
			try {
				return MAPPER.readValue(attributeValue.s(), Details.class);
			} catch (JacksonException exception) {
				throw new IllegalArgumentException("Unable to deserialize metadata details", exception);
			}
		}

		@Override
		public EnhancedType<Details> type() {
			return EnhancedType.of(Details.class);
		}

		@Override
		public AttributeValueType attributeValueType() {
			return AttributeValueType.S;
		}
	}

	/**
	 * Reads legacy numeric (0/1) booleans as well as native BOOL values; writes the legacy numeric format.
	 */
	public static class LegacyBooleanConverter implements AttributeConverter<Boolean> {
		@Override
		public AttributeValue transformFrom(final Boolean value) {
			return AttributeValue.builder().n(value ? "1" : "0").build();
		}

		@Override
		@SuppressFBWarnings("NP_BOOLEAN_RETURN_NULL")
		public Boolean transformTo(final AttributeValue attributeValue) {
			if (attributeValue.bool() != null) {
				return attributeValue.bool();
			}
			if (attributeValue.n() != null) {
				return !"0".equals(attributeValue.n());
			}
			return null;
		}

		@Override
		public EnhancedType<Boolean> type() {
			return EnhancedType.of(Boolean.class);
		}

		@Override
		public AttributeValueType attributeValueType() {
			return AttributeValueType.N;
		}
	}
}
