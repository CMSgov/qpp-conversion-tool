package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Configuration object that pairs a decode label with a list of encode labels and some goods data.
 */
public class CorrelationConfig {

	private String decodeLabel;
	private final List<String> encodeLabels = new ArrayList<>();
	private Goods goods;

	/**
	 * Gets the decode label.
	 *
	 * @return the decode label string
	 */
	public String getDecodeLabel() {
		return decodeLabel;
	}

	/**
	 * Sets the decode label.
	 *
	 * @param decodeLabel the decode label to set
	 */
	public void setDecodeLabel(String decodeLabel) {
		this.decodeLabel = decodeLabel;
	}

	/**
	 * Returns a defensive copy of the internal list of encode labels.
	 *
	 * @return a new List containing all encode labels
	 */
	public List<String> getEncodeLabels() {
		return new ArrayList<>(encodeLabels);
	}

	/**
	 * Replaces the internal list of encode labels with a copy of the provided list.
	 *
	 * @param encodeLabels the list of encode labels to store
	 */
	public void setEncodeLabels(List<String> encodeLabels) {
		this.encodeLabels.clear();
		if (encodeLabels != null) {
			this.encodeLabels.addAll(encodeLabels);
		}
	}

	/**
	 * Gets the goods object.
	 *
	 * @return the goods
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public Goods getGoods() {
		return goods;
	}

	/**
	 * Sets the goods object.
	 *
	 * @param goods the goods to set
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
}
