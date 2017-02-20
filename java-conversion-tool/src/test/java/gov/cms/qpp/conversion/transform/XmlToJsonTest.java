package gov.cms.qpp.conversion.transform;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlToJsonTest {

	@Test
	public void JacksonTest() throws Exception {
		//ClassPathResource xmlResource = new ClassPathResource("measures-data.xml");
		ClassPathResource xmlResource = new ClassPathResource("QRDA_III_1.xml");
		
        XPath xPath = XPathFactory.newInstance().newXPath();

        InputSource inputSource = new InputSource(xmlResource.getInputStream());
        
        // String expression = "/*/measure";
        String expression = "/*/*[local-name()='component']//*[local-name()='observation' and *[local-name()='templateId']]";
        XPathExpression exp = xPath.compile(expression);
        
 	    NodeList result = (NodeList) exp.evaluate(inputSource, XPathConstants.NODESET);
	    // System.out.println(result);
	    
        Transformer xform = TransformerFactory.newInstance().newTransformer();
 	    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
 	    XmlMapper xmlMapper = new XmlMapper();
	    int size = result.getLength();
	    for (int i = 0; i <size; i++ ) {
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    Source xmlSource = new DOMSource(result.item(i));
		    Result outputTarget = new StreamResult(outputStream);
		    xform.transform(xmlSource, outputTarget);
		    Object entries = xmlMapper.readValue(outputStream.toByteArray(), Object.class);
		    ObjectMapper jsonMapper = new ObjectMapper();
		    jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		    String json = jsonMapper.writeValueAsString(entries);
		    System.out.println(json);
	    }
	    
	}

}
