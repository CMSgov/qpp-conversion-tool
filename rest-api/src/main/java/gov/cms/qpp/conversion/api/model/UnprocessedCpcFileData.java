package gov.cms.qpp.conversion.api.model;

import java.util.Date;

/**
 * Model to hold converted metadata to Unprocessed Cpc file data.
 */
public class UnprocessedCpcFileData {
	private String fileId;
	private String fileLocationId;
	private String filename;
	private String apm;
	private Date conversionDate;
	private Boolean validationSuccess;

	/**
	 * Constructor to transform metadata into unprocessed cpc file data
	 *
	 * @param metadata object to be transformed
	 */
	public UnprocessedCpcFileData(Metadata metadata) {
		this.fileId = metadata.getUuid();
		this.fileLocationId = metadata.getSubmissionLocator();
		this.filename = metadata.getFileName();
		this.apm = metadata.getApm();
		this.conversionDate = metadata.getCreatedDate();
		this.validationSuccess = metadata.getOverallStatus();
	}

	/**
	 * retrieves the uuid for file data
	 *
	 * @return fileId
	 */
	public String getFileDataId() {
		return fileId;
	}

	/**
	 * retrieves the file location id
	 *
	 * @return fileId
	 */
	public String getFileLocationId() {
		return fileLocationId;
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
	 * retrieves the apm id
	 *
	 * @return apm
	 */
	public String getApm() {
		return apm;
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
	 * retrieves whether the validation was a success or not
	 *
	 * @return validationSuccess
	 */
	public Boolean getValidationSuccess() {
		return validationSuccess;
	}

}
