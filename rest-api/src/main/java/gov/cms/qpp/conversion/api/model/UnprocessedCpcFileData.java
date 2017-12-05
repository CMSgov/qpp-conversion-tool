package gov.cms.qpp.conversion.api.model;

import java.util.Date;

/**
 * Model to hold converted metadata to Unprocessed Cpc file data.
 */
public class UnprocessedCpcFileData {
	private String fileId;
	private String filename;
	private String apm;
	private Date conversionDate;
	private Boolean validationSuccess;

	/**
	 * Constructor to transform metadata into unprocessed cpc file data
	 *
	 * @param metadata
	 */
	public UnprocessedCpcFileData(Metadata metadata) {
		this.fileId = metadata.getSubmissionLocator();
		this.filename = metadata.getFileName();
		this.apm = metadata.getApm();
		this.conversionDate = metadata.getCreatedDate();
		this.validationSuccess = metadata.getOverallStatus();
	}

	/**
	 * retrieves the id for the file location
	 *
	 * @return fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * sets the id for the file location
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * retrieves the filename
	 *
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * sets the file id location
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * retrieves the apm id
	 *
	 * @return apm
	 */
	public String getApm() {
		return apm;
	}

	/**
	 * sets the file id location
	 */
	public void setApm(String apm) {
		this.apm = apm;
	}

	/**
	 * retrieves the conversion date
	 *
	 * @return conversionDate formatted as a string
	 */
	public String getConversionDate() {
		return conversionDate.toString();
	}

	/**
	 * sets the file id location
	 */
	public void setConversionDate(Date conversionDate) {
		this.conversionDate = conversionDate;
	}

	/**
	 * retrieves whether the validation was a success or not
	 *
	 * @return validationSuccess
	 */
	public Boolean getValidationSuccess() {
		return validationSuccess;
	}

	/**
	 * sets whether the validation was a success or not
	 */
	public void setValidationSuccess(Boolean validationSuccess) {
		this.validationSuccess = validationSuccess;
	}
}
