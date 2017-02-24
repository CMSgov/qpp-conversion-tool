package gov.cms.qpp.conversion.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.xml.XmlUtils;

public class XmlUtilsTest {

	@Test
	public void stringToDom() throws Exception {
		String xmlFragment = XmlUtils.buildString("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
				"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
				"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
				"    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
				"    <statusCode code=\"completed\"/>",
				"    <value xsi:type=\"INT\" value=\"600\"/>",
				"    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
				"  </observation>", "</root>");

		Element dom = XmlUtils.stringToDOM(xmlFragment);

		assertThat("returned dom should not be null", dom, is(not(nullValue())));
		
		List<Element> childElement = dom.getChildren();
		assertThat("test root has one child",dom.getChildren().size(), is(1));
		
		List<Element> leafElements = childElement.get(0).getChildren();
		
		assertThat("test observation has five children", leafElements.size(), is(5));
	}

	@Test
	public void stringToDom_null() throws Exception {

		Element dom = XmlUtils.stringToDOM(null);

		assertThat("returned dom should not be null", dom, is(nullValue()));
	}
	
	@Test(expected=XmlException.class)
	public void stringToDom_emptyString() throws Exception {

		Element dom = XmlUtils.stringToDOM("");

		assertThat("returned dom should not be null", dom, is(nullValue()));
	}
	

	@Test(expected=XmlException.class)
	public void stringToDom_invalidXML() throws Exception {

		Element dom = XmlUtils.stringToDOM("invalid XML");

		assertThat("returned dom should not be null", dom, is(nullValue()));
	}
	
	@Test
	public void fileToDom() throws Exception {
		Element dom = XmlUtils.fileToDOM("target/test-classes/test.xml");

		assertThat("returned dom should not be null", dom, is(not(nullValue())));
		
		List<Element> childElement = dom.getChildren();
		assertThat("test root has one child",dom.getChildren().size(), is(1));
		
		List<Element> leafElements = childElement.get(0).getChildren();
		
		assertThat("test observation has five children", leafElements.size(), is(5));
	}
	
	@Test
	public void fileToDom_null() throws Exception {
		String nullfilename = null;
		XmlUtils.fileToDOM(nullfilename);
	}
	
	@Test(expected=XmlException.class)
	public void fileToDom_fileNotFound() throws Exception {
		XmlUtils.fileToDOM("file/does/not/exist");
	}
}
