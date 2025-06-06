package gov.cms.qpp.conversion.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

/**
 * Represents a conversion report including warnings and errors.
 */
public class Report {

	private String programName;
	private String practiceSiteId;
	private Status status;
	private Long timestamp;
	private final List<Detail> warnings = new ArrayList<>();
	private final List<Detail> errors = new ArrayList<>();

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getPracticeSiteId() {
		return practiceSiteId;
	}

	public void setPracticeSiteId(String practiceSiteId) {
		this.practiceSiteId = practiceSiteId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns an unmodifiable copy of the warnings list to prevent external mutation.
	 *
	 * @return a new List of Detail warnings
	 */
	public List<Detail> getWarnings() {
		return new ArrayList<>(warnings);
	}

	/**
	 * Clears any existing warnings and replaces them with a copy of the provided list.
	 *
	 * @param warnings the List of Detail warnings to store
	 */
	public void setWarnings(List<Detail> warnings) {
		this.warnings.clear();
		if (warnings != null) {
			this.warnings.addAll(warnings);
		}
	}

	/**
	 * Returns an unmodifiable copy of the errors list to prevent external mutation.
	 *
	 * @return a new List of Detail errors
	 */
	public List<Detail> getErrors() {
		return new ArrayList<>(errors);
	}

	/**
	 * Clears any existing errors and replaces them with a copy of the provided list.
	 *
	 * @param errors the List of Detail errors to store
	 */
	public void setErrors(List<Detail> errors) {
		this.errors.clear();
		if (errors != null) {
			this.errors.addAll(errors);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Report that = (Report) o;

		boolean equals = Objects.equals(practiceSiteId, that.practiceSiteId);
		equals &= Objects.equals(programName, that.programName);
		equals &= Objects.equals(status, that.status);
		equals &= Objects.equals(timestamp, that.timestamp);
		equals &= Objects.equals(errors, that.errors);
		equals &= Objects.equals(warnings, that.warnings);
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(practiceSiteId, status, timestamp, programName, errors, warnings);
	}
}
