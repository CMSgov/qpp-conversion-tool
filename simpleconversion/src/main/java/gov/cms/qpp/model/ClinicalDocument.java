package gov.cms.qpp.model;


import java.util.ArrayList;
import java.util.List;

public class ClinicalDocument {
	private int index;
	private String programName;
	private String entityType = "individual";
	private String taxpayerIdentificationNumber;
	private String nationalProviderIdentifier;
	private int performanceYear;
	private List<Object> measurements = new ArrayList<>();


	public int getIndex() {
		return index;
	}

	public void setId(int index) {
		this.index = index;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getTaxpayerIdentificationNumber() {
		return taxpayerIdentificationNumber;
	}

	public void setTaxpayerIdentificationNumber(String taxpayerIdentificationNumber) {
		this.taxpayerIdentificationNumber = taxpayerIdentificationNumber;
	}

	public String getNationalProviderIdentifier() {
		return nationalProviderIdentifier;
	}

	public void setNationalProviderIdentifier(String nationalProviderIdentifier) {
		this.nationalProviderIdentifier = nationalProviderIdentifier;
	}

	public int getPerformanceYear() {
		return performanceYear;
	}

	public void setPerformanceYear(int performanceYear) {
		this.performanceYear = performanceYear;
	}

	public void addMeasurement(Object measurement) {
		measurements.add(measurement);
	}

	@Override
	public String toString() {
		return "ClinicalDocument{" +
				"index=" + index +
				", programName='" + programName + '\'' +
				", entityType='" + entityType + '\'' +
				", taxpayerIdentificationNumber='" + taxpayerIdentificationNumber + '\'' +
				", nationalProviderIdentifier='" + nationalProviderIdentifier + '\'' +
				", performanceYear=" + performanceYear +
				", measurements=" + measurements +
				'}';
	}
}
