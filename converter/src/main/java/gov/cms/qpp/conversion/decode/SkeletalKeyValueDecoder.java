package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import java.util.Objects;

import org.jdom2.Element;

public abstract class SkeletalKeyValueDecoder extends QrdaDecoder {

	private final String key;
	private final String value;

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
		return DecodeResult.TREE_CONTINUE;
	}

}
