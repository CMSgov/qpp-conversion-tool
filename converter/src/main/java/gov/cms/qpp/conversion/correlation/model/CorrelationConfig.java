package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

public class CorrelationConfig {

	private String decodeLabel;
	private List<String> encodeLabels = new ArrayList<>();
	private Goods goods;

	public String getDecodeLabel() {
		return decodeLabel;
	}

	public void setDecodeLabel(String decodeLabel) {
		this.decodeLabel = decodeLabel;
	}

	public List<String> getEncodeLabels() {
		return encodeLabels;
	}

	public void setEncodeLabels(List<String> encodeLabels) {
		this.encodeLabels = encodeLabels;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

}
