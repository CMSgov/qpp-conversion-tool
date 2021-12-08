package gov.cms.qpp.conversion.api.model;

public class TinNpiCombination {
	private String tin;
	private String npi;

	public TinNpiCombination(final String tin, final String npi) {
		this.tin = tin;
		this.npi = npi;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(final String tin) {
		this.tin = tin;
	}

	public String getMaskedTin() {
		return "*****" + tin.substring(5);
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(final String npi) {
		this.npi = npi;
	}
}
