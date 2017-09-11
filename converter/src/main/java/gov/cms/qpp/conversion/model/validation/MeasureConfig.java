package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasureConfig {

	private String category;
	private int firstPerformanceYear;
	private int lastPerformanceYear;
	private String metricType;
	private String measureId;
	private String title;
	private String description;

	@JsonProperty("isRequired")
	private boolean isRequired;
	private String measureSet;

	@JsonProperty("isBonus")
	private boolean isBonus;
	private String objective;

	@JsonProperty("eMeasureId")
	private String electronicMeasureId;

	@JsonProperty("eMeasureUuid")
	private String electronicMeasureVerUuid;

	private List<Strata> strata;

	public MeasureConfig() {
		// empty constructor for jackson
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getFirstPerformanceYear() {
		return firstPerformanceYear;
	}

	public void setFirstPerformanceYear(int firstPerformanceYear) {
		this.firstPerformanceYear = firstPerformanceYear;
	}

	public int getLastPerformanceYear() {
		return lastPerformanceYear;
	}

	public void setLastPerformanceYear(int lastPerformanceYear) {
		this.lastPerformanceYear = lastPerformanceYear;
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public String getMeasureId() {
		return measureId;
	}

	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getMeasureSet() {
		return measureSet;
	}

	public void setMeasureSet(String measureSet) {
		this.measureSet = measureSet;
	}

	public boolean isBonus() {
		return isBonus;
	}

	public void setIsBonus(boolean isBonus) {
		this.isBonus = isBonus;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getElectronicMeasureId() {
		return electronicMeasureId;
	}

	public void setElectronicMeasureId(String electronicMeasureId) {
		this.electronicMeasureId = electronicMeasureId;
	}

	public String getElectronicMeasureVerUuid() {
		return electronicMeasureVerUuid;
	}

	public void setElectronicMeasureVerUuid(final String electronicMeasureVerUuid) {
		this.electronicMeasureVerUuid = electronicMeasureVerUuid;
	}

	public List<Strata> getStrata() {
		return strata;
	}

	public void setStrata(final List<Strata> strata) {
		this.strata = strata;
	}

	public List<SubPopulation> getSubPopulation() {
		List<Strata> stratas = getStrata();

		if (stratas == null) {
			return Collections.emptyList();
		}

		return stratas.stream().map(Strata::getElectronicMeasureUuids).collect(Collectors.toList());
	}
}
