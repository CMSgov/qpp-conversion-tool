package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.Context;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class TemplateIdTest {

	@Test
	public void testRoot() {
		assertWithMessage("TemplateId#getRoot() is not working")
				.that(TemplateId.CLINICAL_DOCUMENT.getRoot()).isSameAs("2.16.840.1.113883.10.20.27.1.2");
	}

	@Test
	public void testExtension() {
		assertWithMessage("TemplateId#getExtension() is not working")
				.that(TemplateId.CLINICAL_DOCUMENT.getExtension()).isSameAs("2017-07-01");
	}

	@Test
	public void testGetTemplateId() {
		assertWithMessage("TemplateId#getTemplateId() is not working")
				.that(TemplateId.CLINICAL_DOCUMENT.getTemplateId(new Context()))
				.isEqualTo(TemplateId.CLINICAL_DOCUMENT.getRoot() + ":" +
						TemplateId.CLINICAL_DOCUMENT.getExtension());
	}

	@Test
	public void testFindByTypeId2() {
		TemplateId clinicalDocument = TemplateId.CLINICAL_DOCUMENT;
		TemplateId actual = TemplateId.getTemplateId(clinicalDocument.getRoot(),
				clinicalDocument.getExtension(), new Context());

		assertWithMessage("TemplateId#getTypeById(String, String) is not working")
				.that(actual).isSameAs(clinicalDocument);
	}

	@Test
	public void testFindByTypeId2NotExist() {
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
				"nonExistingExtension", new Context());

		assertWithMessage("TemplateId#getTypeById(String, String) is not working")
				.that(actual).isSameAs(TemplateId.DEFAULT);
	}

	@Test
	public void testFindByTypeId2NotExistAgain() {
		TemplateId actual = TemplateId.getTemplateId("nonExistingRoot",
				TemplateId.CLINICAL_DOCUMENT.getExtension(), new Context());

		assertWithMessage("TemplateId#getTypeById(String, String) is not working")
				.that(actual).isSameAs(TemplateId.DEFAULT);
	}

	@Test
	public void testGenerateTemplateIdString() {
		final String root = "asdf";
		final String extension = "jkl;";
		String actual = TemplateId.generateTemplateIdString(root, extension, new Context());

		assertWithMessage("TemplateId#generateTemplateIdString() is not working")
				.that(actual).isEqualTo(root + ":" + extension);
	}

	@Test
	public void valueOfTest() {
		String actual = TemplateId.valueOf("DEFAULT").getTemplateId(new Context());

		assertWithMessage("Expect value of to return a TemplateId")
				.that(actual).isSameAs("default");
	}
}