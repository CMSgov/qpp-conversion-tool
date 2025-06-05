package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * Returns an unmodifiable view of the encodeLabels list.
	 */
	public List<String> getEncodeLabels() {
		return Collections.unmodifiableList(encodeLabels);
	}

	/**
	 * Stores a defensive copy of the provided list.
	 */
	public void setEncodeLabels(List<String> encodeLabels) {
		if (encodeLabels == null) {
			this.encodeLabels = new ArrayList<>();
		} else {
			this.encodeLabels = new ArrayList<>(encodeLabels);
		}
	}

	/**
	 * Returns a defensive copy of goods, or null if unset.
	 * Since Goods lacks a copy constructor or public clone(), we manually copy fields.
	 */
	public Goods getGoods() {
		if (goods == null) {
			return null;
		}
		Goods copy = new Goods();
		copy.setRelativeXPath(goods.getRelativeXPath());
		copy.setXmltype(goods.getXmltype());
		return copy;
	}

	/**
	 * Stores a defensive copy of the provided Goods, or clears if null.
	 */
	public void setGoods(Goods goods) {
		if (goods == null) {
			this.goods = null;
		} else {
			Goods copy = new Goods();
			copy.setRelativeXPath(goods.getRelativeXPath());
			copy.setXmltype(goods.getXmltype());
			this.goods = copy;
		}
	}
}
