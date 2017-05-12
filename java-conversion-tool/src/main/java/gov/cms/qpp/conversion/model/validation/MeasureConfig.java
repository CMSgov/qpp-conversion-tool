package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
	private int weight;
	private String measureSet;

	@JsonProperty("isBonus")
	private boolean isBonus;
	private String objective;

	private String uuid;
	private List<String> requiredPopulationCriteria;

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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public List<String> getRequiredPopulationCriteria() {
		return requiredPopulationCriteria;
	}

	public void setRequiredPopulationCriteria(final List<String> requiredPopulationCriteria) {
		this.requiredPopulationCriteria = requiredPopulationCriteria;
	}
}
