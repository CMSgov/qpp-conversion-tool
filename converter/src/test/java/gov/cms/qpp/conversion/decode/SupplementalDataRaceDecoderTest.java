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

public class SupplementalDataRaceDecoderTest {

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
	void testDecodeRaceAlaskanNativeAmericanIndianSuccess() {
		supplementalDataNode = getNodeByIndex(3);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.ALASKAN_NATIVE_AMERICAN_INDIAN.getCode());
	}

	@Test
	void testDecodeRaceAsianSuccess() {
		supplementalDataNode = getNodeByIndex(2);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.ASIAN.getCode());
	}

	@Test
	void testDecodeRaceAfricanAmericanSuccess() {
		supplementalDataNode = getNodeByIndex(0);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.AFRICAN_AMERICAN.getCode());
	}

	@Test
	void testDecodeRaceHawaiianPacificIslanderSuccess() {
		supplementalDataNode = getNodeByIndex(4);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.HAWAIIAN_PACIFIC_ISLANDER.getCode());
	}

	@Test
	void testDecodeRaceWhiteSuccess() {
		supplementalDataNode = getNodeByIndex(1);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.WHITE.getCode());
	}

	@Test
	void testDecodeRaceOtherSuccess() {
		supplementalDataNode = getNodeByIndex(5);
		assertThat(supplementalDataNode.getValue(SupplementalDataRaceDecoder.RACE_NAME))
				.isEqualTo(SupplementalData.OTHER_RACE.getCode());
	}

	private void decodeNodeFromFile(String filename) throws XmlException {
		context = new Context();
		SupplementalDataRaceDecoder decoder = new SupplementalDataRaceDecoder(context);
		root = decoder.decode(XmlUtils.stringToDom(filename));
	}

	private Node getNodeByIndex(int index) {
		return getRaceNodeList().get(index);
	}

	private List<Node> getRaceNodeList() {
		Node measureSectionNode = root.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		List<Node> raceNodeList =
				measureSectionNode.getChildNodes(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
						.collect(Collectors.toList());
		return raceNodeList;
	}

}
