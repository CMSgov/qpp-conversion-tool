package gov.cms.qpp.conversion.decode;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class SupplementalDataEthnicityDecoderTest {

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
	void testDecodeEthnicityNotHispanicSuccess() {
		supplementalDataNode = getNodeByIndex(0);
		assertThat(supplementalDataNode.getValue(SupplementalData.NOT_HISPANIC_LATINO.name()))
				.isEqualTo(SupplementalData.NOT_HISPANIC_LATINO.getCode());
	}

	@Test
	void testDecodeEthnicityHispanicSuccess() {
		supplementalDataNode = getNodeByIndex(1);
		assertThat(supplementalDataNode.getValue(SupplementalData.HISPANIC_LATINO.name()))
				.isEqualTo(SupplementalData.HISPANIC_LATINO.getCode());
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		SupplementalDataEthnicityDecoder decoder = new SupplementalDataEthnicityDecoder(context);
		root = decoder.decode(XmlUtils.stringToDom(filename));
	}

	private Node getNodeByIndex(int index) {
		return getEthnicityNodeList().get(index);
	}

	private List<Node> getEthnicityNodeList() {
		Node measureSectionNode = root.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		List<Node> ethnicityNodeList =
				measureSectionNode.getChildNodes(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
						.collect(Collectors.toList());
		return ethnicityNodeList;
	}
}
