package gov.cms.qpp.conversion.model.error;

import java.util.List;

public class ValidationResult {

	private List<Detail> errors;
	private List<Detail> warnings;

	public ValidationResult(List<Detail> errors, List<Detail> warnings) {
		this.errors = errors;
		this.warnings = warnings;
	}

	public List<Detail> getErrors() {
		return errors;
	}

	public void setErrors(List<Detail> errors) {
		this.errors = errors;
	}

	public List<Detail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Detail> warnings) {
		this.warnings = warnings;
	}

}
