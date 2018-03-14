package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

import java.util.Objects;

import org.jdom2.Element;

public abstract class SkeletalSectionDecoder extends QrdaDecoder {

	public static final String CATEGORY = "category";

	private final String category;

	public SkeletalSectionDecoder(Context context, String category) {
		super(context);

		Objects.requireNonNull(category, "category");
		this.category = category;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		thisNode.putValue(CATEGORY, category);
		return DecodeResult.TREE_CONTINUE;
	}

}
