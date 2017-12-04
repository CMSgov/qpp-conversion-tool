package gov.cms.qpp.conversion.api.model;

import java.util.Date;

public class UnprocessedCpcFileData {
	private String fileId;
	private String filename;
	private String apm;
	private Date conversionDate;
	private Boolean validationSuccess;
	//- if failure, what errors (if possible) (this may be a new story for later)


	public UnprocessedCpcFileData(Metadata metadata) {
		this.fileId = metadata.getSubmissionLocator();
		this.filename = metadata.getFileName();
		this.apm = metadata.getApm();
		this.conversionDate = metadata.getCreatedDate();
		this.validationSuccess = metadata.getOverallStatus();
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getApm() {
		return apm;
	}

	public void setApm(String apm) {
		this.apm = apm;
	}

	public String getConversionDate() {
		return conversionDate.toString();
	}

	public void setConversionDate(Date conversionDate) {
		this.conversionDate = conversionDate;
	}

	public Boolean getValidationSuccess() {
		return validationSuccess;
	}

	public void setValidationSuccess(Boolean validationSuccess) {
		this.validationSuccess = validationSuccess;
	}
}
