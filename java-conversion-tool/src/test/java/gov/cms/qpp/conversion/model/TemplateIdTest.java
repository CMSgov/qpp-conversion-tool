package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.Converter;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TemplateIdTest {

	@Test
	public void testRoot() {
		assertThat("TemplateId#getRoot() is not working", TemplateId.CLINICAL_DOCUMENT.getRoot(),
		           is("2.16.840.1.113883.10.20.27.1.2"));
	}

	@Test
	public void testExtension() {
		assertThat("TemplateId#getExtension() is not working", TemplateId.CLINICAL_DOCUMENT.getExtension(),
		           is("2017-07-01"));
	}

	@Test
	public void testGetTemplateId() {
		assertThat("TemplateId#getTemplateId() is not working", TemplateId.CLINICAL_DOCUMENT.getTemplateId(),
		           is(TemplateId.CLINICAL_DOCUMENT.getRoot() + ":" +
		              TemplateId.CLINICAL_DOCUMENT.getExtension()));
	}

	@Test
	public void testFindByTypeId2() {
		TemplateId clinicalDocument = TemplateId.CLINICAL_DOCUMENT;
		assertThat("TemplateId#getTypeById(String, String) is not working",
		           TemplateId.getTemplateId(clinicalDocument.getRoot(), clinicalDocument.getExtension()),
		           is(clinicalDocument));
	}

	@Test
	public void testFindByTypeId2NotExist() {
		assertThat("TemplateId#getTypeById(String, String) is not working",
		           TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(), "nonExistingExtension"),
		           is(TemplateId.DEFAULT));
	}

	@Test
	public void testFindByTypeId2NotExistAgain() {
		assertThat("TemplateId#getTypeById(String, String) is not working",
		           TemplateId.getTemplateId("nonExistingRoot", TemplateId.CLINICAL_DOCUMENT.getExtension()),
		           is(TemplateId.DEFAULT));
	}

	@Test
	public void testGenerateTemplateIdString() {
		final String root = "asdf";
		final String extension = "jkl;";
		assertThat("TemplateId#generateTemplateIdString() is not working",
		           TemplateId.generateTemplateIdString(root, extension), is(root + ":" + extension));
	}

	@Test
	public void valueOfTest() {
		String value = TemplateId.valueOf("DEFAULT").getTemplateId();
		assertThat("Expect value of to return a TemplateId", value, is("default"));
	}
}