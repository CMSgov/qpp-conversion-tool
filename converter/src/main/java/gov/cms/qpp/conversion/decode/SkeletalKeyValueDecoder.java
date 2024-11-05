package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import java.util.Objects;
import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import static gov.cms.qpp.conversion.model.Constants.CATEGORY_SECTION_V5;
import static gov.cms.qpp.conversion.model.Constants.QUALITY_SECTION;

/**
 * {@link QrdaDecoder} that adds a {@link String} key/value pair to the {@link Node}
 */
public abstract class SkeletalKeyValueDecoder extends QrdaDecoder {

	private final String key;
	private final String value;

	/**
	 * @param context
	 * @param key the key of the value added to the node
	 * @param value the value added to the node
	 */
	public SkeletalKeyValueDecoder(Context context, String key, String value) {
		super(context);

		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");

		this.key = key;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		thisNode.putValue(key, value);
		if (QUALITY_SECTION.equalsIgnoreCase(value)){
			setCategorySectionOnNode(element, thisNode);
		}
		return DecodeResult.TREE_CONTINUE;
	}

	private void setCategorySectionOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
			thisNode.putValue(CATEGORY_SECTION_V5, p.getValue());
		setOnNode(element, getXpath(CATEGORY_SECTION_V5),
			consumer, Filters.attribute(), true);
	}

}
