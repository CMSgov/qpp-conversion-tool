package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.XmlDecoder;

public class DecoderTest {

	String[] templateIDs = new String[] {
			"2.16.840.1.113883.10.20.24.2.2",
			
			"2.16.840.1.113883.10.20.27.1.2",
			"2.16.840.1.113883.10.20.27.2.3",
			"2.16.840.1.113883.10.20.27.2.4",
			"2.16.840.1.113883.10.20.27.2.5",
			"2.16.840.1.113883.10.20.27.2.6",
			"2.16.840.1.113883.10.20.27.3.1",
			"2.16.840.1.113883.10.20.27.3.3",
			
			"2.16.840.1.113883.10.20.27.3.16",
			"2.16.840.1.113883.10.20.27.3.17",
			"2.16.840.1.113883.10.20.27.3.18",
			"2.16.840.1.113883.10.20.27.3.19",
			"2.16.840.1.113883.10.20.27.3.20",
			"2.16.840.1.113883.10.20.27.3.21",
			"2.16.840.1.113883.10.20.27.3.22",
			"2.16.840.1.113883.10.20.27.3.23",
			// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3
			"2.16.840.1.113883.10.20.27.3.24",
			"2.16.840.1.113883.10.20.27.3.25",
			"2.16.840.1.113883.10.20.27.3.26",
			"2.16.840.1.113883.10.20.27.3.27",
			"2.16.840.1.113883.10.20.27.3.28",
			"2.16.840.1.113883.10.20.27.3.29",
			"2.16.840.1.113883.10.20.27.3.30",
			"2.16.840.1.113883.10.20.27.3.31",
			"2.16.840.1.113883.10.20.27.3.32",
			"2.16.840.1.113883.10.20.27.3.33",
			
			"2.16.840.1.113883.10.20.24.3.98",
	};
	
	
	@Before
	public void before() {
		Validations.init();
	}
	@After
	public void after() {
		Validations.clear();
	}
	
//	private String makeXml(String templateId) {
//		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
//				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
//				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
//				+ " 	<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>\n" 
//				+ "		<templateId root=\""+templateId+"\" extension=\"2016-09-01\" />\n"
//				+ "	</observation>\n"
//				+ "</component>";
//		return xmlFragment;
//	}

	@Test
	public void decodeTemplateIds() throws Exception {
		Registry<String, InputDecoder> registry;
		registry = new Registry<>(XmlDecoder.class);
		
		for (String templateId : templateIDs) {
			InputDecoder decoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", decoder, is(not(nullValue())));
			
//	
//			String xmlFragment = makeXml(templateId);
//			Node root = new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
//
//			// This node is the place holder around the root node
//			assertThat(templateId + " returned node should not be null", root, is(not(nullValue())));
//	
//			List<Node> children = root.getChildNodes();
//			
//			// For all Decoders this should be either a value or child node
//			assertThat(templateId + " returned node should have one child node", children.size(), is(1));
//			
//			Assert.assertEquals(templateId + " Decoder missing.", templateId, children.get(0).getId());
		}
	}


}

/*
{  "programName": "mips",  "entityType": "individual",  "taxpayerIdentificationNumber": "000123456",  "nationalProviderIdentifier": "9876543210",  "performanceYear": 2016,  "measurementSets": [    {      "category": "ia",      "source": "provider",      "performanceStart": "2016-01-01",      "performanceEnd": "2016-06-01",      "measurements": [          {            "measureId": "IA_EPA_4",            "value": true          },          {            "measureId": "IA_PSPA_4",            "value": true          },          {            "measureId": "IA_PSPA_5",            "value": true          },          {            "measureId": "IA_PM_2",            "value": true          }      ]    },    {      "category": "aci",      "source": "provider",      "performanceStart": "2016-01-01",      "performanceEnd": "2016-06-01",      "measurements": [        {          "measureId": "ACI_INFBLO_1",          "value": true        },        {          "measureId": "ACI_ONCDIR_1",          "value": true        },        {          "measureId": "ACI_EP_1",          "value": {            "numerator": 15,            "denominator": 20          }        },        {          "measureId": "ACI_PEA_1",          "value": {            "numerator": 11,            "denominator": 13          }        },        {          "measureId": "ACI_PPHI_1",          "value": true        },        {          "measureId": "ACI_HIE_1",          "value": {            "numerator": 11,            "denominator": 13          }        },        {          "measureId": "ACI_HIE_2",          "value": {            "numerator": 9,            "denominator": 13          }        },        {          "measureId": "ACI_HIE_3",          "value": {            "numerator": 1,            "denominator": 2          }        },        {          "measureId": "ACI_CCTPE_2",          "value": {            "numerator": 1,            "denominator": 2          }        }      ]    }  ] }
*/
