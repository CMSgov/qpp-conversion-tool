package gov.cms.qpp.conversion.correlation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"relativeXPath",
	"xmltype"
})
public class Goods {

	@JsonProperty("relativeXPath")
	private String relativeXPath;
	@JsonProperty("xmltype")
	private String xmltype;

	@JsonProperty("relativeXPath")
	public String getRelativeXPath() {
		return relativeXPath;
	}

	@JsonProperty("relativeXPath")
	public void setRelativeXPath(String relativeXPath) {
		this.relativeXPath = relativeXPath;
	}

	@JsonProperty("xmltype")
	public String getXmltype() {
		return xmltype;
	}

	@JsonProperty("xmltype")
	public void setXmltype(String xmltype) {
		this.xmltype = xmltype;
	}

}
