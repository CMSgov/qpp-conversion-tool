package gov.cms.qpp.conversion.api.model;

import java.util.List;
import java.util.Objects;

/**
 * Response sent when a health check request is received
 */
public class HealthCheck {

	private List<String> environmentVariables;
	private List<String> systemProperties;
	private String implementationVersion;

	public List<String> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(List<String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	public List<String> getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(List<String> systemProperties) {
		this.systemProperties = systemProperties;
	}

	public String getImplementationVersion() {
		return implementationVersion;
	}

	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
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

		boolean equals = Objects.equals(environmentVariables, that.environmentVariables);
		equals &= Objects.equals(systemProperties, that.systemProperties);
		equals &= Objects.equals(implementationVersion, that.implementationVersion);
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(environmentVariables, systemProperties, implementationVersion);
	}

}
