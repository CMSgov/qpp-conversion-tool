package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_KEY;

public class SupplementalDataPayerDecoderTest {
	private static String successFile;

	private Context context;
	private Node root;
	private Node supplementalDataNode;

	@BeforeAll
	static void setup() throws IOException {
		successFile = TestHelper.getFixture("successfulSupplementalDataFile.xml");
	}

	@BeforeEach
	void before() throws XmlException {
		decodeNodeFromFile(successFile);
	}

	@Test
	void testDecodePayerMedicareSuccess() {
		supplementalDataNode = getNodeByIndex(0);
		assertThat(supplementalDataNode.getValue(SUPPLEMENTAL_DATA_KEY))
				.isEqualTo(SupplementalData.MEDICARE.getCode());
	}

	@Test
	void testDecodePayerMedicaidSuccess() {
		supplementalDataNode = getNodeByIndex(1);
		assertThat(supplementalDataNode.getValue(SUPPLEMENTAL_DATA_KEY))
				.isEqualTo(SupplementalData.MEDICAID.getCode());
	}

	@Test
	void testDecodePayerPrivateHealthSuccess() {
		supplementalDataNode = getNodeByIndex(2);
		assertThat(supplementalDataNode.getValue(SUPPLEMENTAL_DATA_KEY))
				.isEqualTo(SupplementalData.PRIVATE_HEALTH_INSURANCE.getCode());
	}

	@Test
	void testDecodePayerOtherSuccess() {
		supplementalDataNode = getNodeByIndex(3);
		assertThat(supplementalDataNode.getValue(SUPPLEMENTAL_DATA_KEY))
				.isEqualTo(SupplementalData.OTHER_PAYER.getCode());
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		SupplementalDataPayerDecoder decoder = new SupplementalDataPayerDecoder(context);
		QrdaDecoderEngine engine = new QrdaDecoderEngine(context);
		root = engine.decode(XmlUtils.stringToDom(filename));
	}

	private Node getNodeByIndex(int index) {
		return getPayerNodeList().get(index);
	}

	private List<Node> getPayerNodeList() {
		Node measureSectionNode = root.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		List<Node> payerNodeList =
				measureSectionNode.getChildNodes(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
						.collect(Collectors.toList());
		return payerNodeList;
	}
}
