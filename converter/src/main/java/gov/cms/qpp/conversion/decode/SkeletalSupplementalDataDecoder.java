package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;

import java.util.Objects;
import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * {@link QrdaDecoder} abstraction for decoding supplemental data
 */
public abstract class SkeletalSupplementalDataDecoder extends QrdaDecoder {

	public static final String SUPPLEMENTAL_DATA_CODE = "code";
	public static final String SUPPLEMENTAL_DATA_KEY = "supplementalData";
	public static final String SUPPLEMENTAL_DATA_PAYER_CODE = "payerCode";

	private final SupplementalType type;

	/**
	 * @param context
	 * @param type the type of supplemental data to decode
	 */
	public SkeletalSupplementalDataDecoder(Context context, SupplementalType type) {
		super(context);

		Objects.requireNonNull(type, "type");
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		setSupplementalDataOnNode(element, thisNode, type);
		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Sets a given Supplemental Data by type in the current Node
	 *
	 * @param element XML element that represents SupplementalDataCode
	 * @param thisNode Current Node to decode into
	 * @param type Current Supplemental Type to put onto this node
	 */
	void setSupplementalDataOnNode(Element element, Node thisNode, SupplementalData.SupplementalType type) {
		String supplementalXpathCode = type.equals(SupplementalData.SupplementalType.PAYER)
				? SUPPLEMENTAL_DATA_PAYER_CODE :  SUPPLEMENTAL_DATA_CODE;
		String expressionStr = getXpath(supplementalXpathCode);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(SUPPLEMENTAL_DATA_KEY, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}

}
