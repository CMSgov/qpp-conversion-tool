package gov.cms.qpp.conversion.api.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model to hold conversion metadata.
 */
@Entity
@Table(name = "conversion_metadata")
public final class Metadata {
	private static final int CPC_PROCESSED_CREATE_DATE_NUM_FIELDS = 2;
	private static final int CPC_PROCESSED_INDEX = 0;
	private static final int CPC_CREATE_DATE_INDEX = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String uuid;
	private String tin;  //this field is encrypted
	private String npi;
	private String apm;
	private Long submissionYear;
	private String submissionLocator;
	private String qppLocator;
	private String fileName;  //this field is encrypted
	private Boolean overallStatus;
	private Boolean conversionStatus;
	private Boolean validationStatus;
	private Boolean cpc;
	private String conversionErrorLocator;
	private String validationErrorLocator;
	private String rawValidationErrorLocator;
	private Long createdDate;
	private Boolean cpcProcessed;
	private String purpose;

	public Metadata() {
		setCreatedDate(System.currentTimeMillis());
	}

	/**
	 * The UUID that uniquely identifies this item.
	 *
	 * @return The UUID.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * There is no reason to set this manually because it is automatically generated when saved to the database.
	 *
	 * @param uuid The UUID to use.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * The timestamp when this item was created.
	 *
	 * @return The date and time.
	 */
	public Long getCreatedDate() {
		return createdDate;
	}

	/**
	 * There is no reason to set this manually because it is automatically generated when saved to the database.
	 *
	 * @param createdDate The timestamp to use.
	 */
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * The TIN associated with the the conversion.
	 *
	 * @return The TIN.
	 */
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
	public String getApm() {
		return apm;
	}

	/**
	 * Sets the the APM Entity ID associated with the the conversion. Used with CPC+.
	 *
	 * @param apm The APM Entity ID.
	 */
	public void setApm(String apm) {
		this.apm = apm;
	}

	/**
	 * The submission year associated with the the conversion.
	 *
	 * @return The submission year.
	 */
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
	 * @return The purpose of the conversion, for example \"Test\"
	 */
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
	 * @return A true {@link Boolean} for a CPC+ conversion, null OR a false {@link Boolean} otherwise.
	 */
	public Boolean getCpc() {
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
	public void setCpc(Boolean cpc) {
		this.cpc = cpc;
	}

	/**
	 * A location to where the conversion error JSON can be found.
	 *
	 * For example, for AWS, this could be an ARN.
	 *
	 * @return The location.
	 */
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
	 * @return Whether the file was processed.
	 */
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
	 * Returns an attribute that combines the CPC+ processed state and the date of creation.
	 *
	 * This is mostly useful in the CPC+ global secondary index.
	 *
	 * @return The combined attribute.
	 */
	public String getCpcProcessedCreateDate() {
		if (cpcProcessed != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
			return cpcProcessed.toString() + "#" + formatter.format(Instant.ofEpochMilli(createdDate));
		}

		return null;
	}

	/**
	 * Sets the separate CPC+ processed flag and created date based on the argument
	 *
	 * Splits the the processed flag from the date by a {@code #} character.
	 * The first field must be {@code true} or {@code false} which represents the CPC+ processed boolean.
	 * The second field must be an ISO 8601 timestamp string.  For example, {@code 2018-12-08T18:32:54.846Z}.
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
		setCreatedDate(instant.toEpochMilli());
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
		equals &= Objects.equals(purpose, that.purpose);
		return equals;
	}

	/**
	 * Computes and returns the hash code for this object.
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(uuid, tin, npi, createdDate, apm, submissionYear, submissionLocator, qppLocator, fileName,
				overallStatus, conversionStatus, validationStatus, cpc, conversionErrorLocator, validationErrorLocator,
				rawValidationErrorLocator, cpcProcessed, purpose);
	}
}
