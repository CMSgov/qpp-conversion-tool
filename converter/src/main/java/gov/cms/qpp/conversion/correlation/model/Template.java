package gov.cms.qpp.conversion.correlation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"templateId",
	"correlationId"
})
public class Template {

	@JsonProperty("templateId")
	private String templateId;
	@JsonProperty("correlationId")
	private String correlationId;

	@JsonProperty("templateId")
	public String getTemplateId() {
		return templateId;
	}

	@JsonProperty("templateId")
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@JsonProperty("correlationId")
	public String getCorrelationId() {
		return correlationId;
	}

	@JsonProperty("correlationId")
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

}
