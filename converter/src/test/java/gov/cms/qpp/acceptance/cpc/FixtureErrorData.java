package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.model.error.ErrorCode;

import java.util.List;

public class FixtureErrorData {
	private int errorCode;
	private int occurrences;
	private String message;
	private List<Object> subs;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}

	public List<Object> getSubs() {
		return subs;
	}

	public void setSubs(List<Object> subs) {
		this.subs = subs;
	}

	public String getMessage() {
		if (message == null) {
			ErrorCode ec = ErrorCode.getByCode(errorCode);
			message = (subs != null) ? ec.format(subs.toArray()).getMessage() : ec.getMessage();
		}
		return message;
	}
}
