package gov.cms.qpp.conversion.api.model;

import java.util.List;

import gov.cms.qpp.conversion.model.error.Detail;

public class ConvertResponse {

	private Object qpp;
	private List<Detail> warnings;

	public Object getQpp() {
		return qpp;
	}

	public void setQpp(Object qpp) {
		this.qpp = qpp;
	}

	public List<Detail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Detail> warnings) {
		this.warnings = warnings;
	}

}
