package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"decodeLabel",
	"encodeLabels",
	"goods"
})
public class Config {

	@JsonProperty("decodeLabel")
	private String decodeLabel;
	@JsonProperty("encodeLabels")
	private List<String> encodeLabels = new ArrayList<String>();
	@JsonProperty("goods")
	private Goods goods;

	@JsonProperty("decodeLabel")
	public String getDecodeLabel() {
		return decodeLabel;
	}

	@JsonProperty("decodeLabel")
	public void setDecodeLabel(String decodeLabel) {
		this.decodeLabel = decodeLabel;
	}

	@JsonProperty("encodeLabels")
	public List<String> getEncodeLabels() {
		return encodeLabels;
	}

	@JsonProperty("encodeLabels")
	public void setEncodeLabels(List<String> encodeLabels) {
		this.encodeLabels = encodeLabels;
	}

	@JsonProperty("goods")
	public Goods getGoods() {
		return goods;
	}

	@JsonProperty("goods")
	public void setGoods(Goods goods) {
		this.goods = goods;
	}

}
