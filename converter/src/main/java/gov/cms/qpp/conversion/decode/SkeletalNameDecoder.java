package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import java.util.Objects;

import org.jdom2.Element;

public abstract class SkeletalNameDecoder extends QrdaDecoder {

	public static final String NAME = "name";

	private final String name;

	public SkeletalNameDecoder(Context context, String name) {
		super(context);

		Objects.requireNonNull(name, "name");
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		thisNode.putValue(NAME, name);
		return DecodeResult.TREE_CONTINUE;
	}

}
