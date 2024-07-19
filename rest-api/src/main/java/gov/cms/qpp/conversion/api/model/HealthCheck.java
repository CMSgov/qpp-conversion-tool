package gov.cms.qpp.conversion.api.model;

import java.util.Objects;
import org.springframework.http.HttpStatus;

/**
 * Response sent when a health check request is received
 */
public class HealthCheck {

	private String implementationVersion;
	private HttpStatus status;
	private String javaVersion;
	private String pcfClose;
	private String validationUrl;
	private String validationFile;

	public String getImplementationVersion() {
		return implementationVersion;
	}

	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
	}

	public String getJavaVersion() { return javaVersion; }
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getValidationUrl() { return validationUrl; }
	public void setValidationUrl(String validationUrl) {
		this.validationUrl = validationUrl;
	}

	public String getValidationFile() { return validationFile; }
	public void setValidationFile(String validationFile) {
		this.validationFile = validationFile;
	}

	public String getPcfClose() { return pcfClose; }
	public void setPcfClose(String pcfClose) {
		this.pcfClose = pcfClose;
	}

	public HttpStatus getStatus() { return status; }
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || o.getClass() != getClass()) {
			return false;
		}

		HealthCheck that = (HealthCheck) o;

		boolean equals = Objects.equals(implementationVersion, that.implementationVersion);
		equals &= Objects.equals(status, that.status);
		equals &= Objects.equals(javaVersion, that.javaVersion);
		equals &= Objects.equals(pcfClose, that.pcfClose);
		equals &= Objects.equals(validationUrl, that.validationUrl);
		equals &= Objects.equals(validationFile, that.validationFile);

		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(implementationVersion, status, javaVersion, pcfClose, validationUrl, validationFile);
	}
}
