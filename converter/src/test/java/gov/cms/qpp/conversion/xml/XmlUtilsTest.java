package gov.cms.qpp.conversion.xml;

import static com.google.common.truth.Truth.assertWithMessage;

import java.lang.reflect.Constructor;
import java.util.List;

import org.jdom2.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class XmlUtilsTest {

	private String xmlFragment = XmlUtils.buildString("<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
			"  <observation classCode=\"OBS\" moodCode=\"EVN\">",
			"    <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>",
			"    <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>",
			"    <statusCode code=\"completed\"/>",
			"    <value xsi:type=\"INT\" value=\"600\"/>",
			"    <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>",
			"  </observation>", "</root>");

	@Test
	void stringToDomCanParse() throws Exception {
		Element dom = XmlUtils.stringToDom(xmlFragment);
		assertWithMessage("returned dom should not be null")
				.that(dom).isNotNull();
	}

	@Test
	void stringToDomRootChild() throws Exception {
		Element dom = XmlUtils.stringToDom(xmlFragment);
		List<Element> childElement = dom.getChildren();
		assertWithMessage("test root has one child")
				.that(childElement)
				.hasSize(1);
	}

	@Test
	void stringToDomOtherDescendants() throws Exception {
		Element dom = XmlUtils.stringToDom(xmlFragment);
		List<Element> childElement = dom.getChildren();
		List<Element> leafElements = childElement.get(0).getChildren();

		assertWithMessage("test observation has five children")
				.that(leafElements).hasSize(5);
	}

	@Test
	void stringToDom_null() throws Exception {
		Element dom = XmlUtils.stringToDom(null);

		assertWithMessage("returned dom should be null")
				.that(dom).isNull();
	}
	
	@Test
	void stringToDom_emptyString() throws Exception {
		Assertions.assertThrows(XmlException.class, () -> XmlUtils.stringToDom(""));
	}
	

	@Test
	void stringToDom_invalidXML() throws Exception {
		Assertions.assertThrows(XmlException.class, () -> XmlUtils.stringToDom("invalid XML"));
	}

	@Test
	void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<XmlUtils> constructor = XmlUtils.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		XmlUtils xmlUtils = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);
		assertWithMessage("Expect to have an instance here")
				.that(xmlUtils).isInstanceOf(XmlUtils.class);
	}
}
