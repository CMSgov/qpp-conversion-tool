package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.TemplateId.Extension;
import gov.cms.qpp.test.enums.EnumContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class TemplateIdTest implements EnumContract {

	@AfterEach
	void cleanUp() {
		System.clearProperty(Extension.STRICT_EXTENSION);
	}

	@Test
	void testRoot() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getRoot()).isSameAs("2.16.840.1.113883.10.20.27.1.2");
	}

	@Test
	void testExtension() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getExtension()).isSameAs("2017-07-01");
	}

	@Test
	void testGetTemplateId() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getTemplateId(new Context()))
				.isEqualTo(TemplateId.CLINICAL_DOCUMENT.getRoot() + ":" +
						TemplateId.CLINICAL_DOCUMENT.getExtension());
	}

	@Test
	void testGetTemplateIdHistoricalWithExtension() {
		Context context = new Context();
		context.setHistorical(true);
		assertThat(TemplateId.CLINICAL_DOCUMENT.getTemplateId(context))
				.isEqualTo(TemplateId.CLINICAL_DOCUMENT.getRoot());
	}

	@Test
	void testGetTemplateIdHistoricalNoExtension() {
		Context context = new Context();
		context.setHistorical(true);
		assertThat(TemplateId.PLACEHOLDER.getTemplateId(context))
				.isEqualTo(TemplateId.PLACEHOLDER.getRoot());
	}

	@Test
	void testGetTemplateIdNotHistoricalNoExtension() {
		Context context = new Context();
		context.setHistorical(false);
		assertThat(TemplateId.PLACEHOLDER.getTemplateId(context))
				.isEqualTo(TemplateId.PLACEHOLDER.getRoot());
	}

	@Test
	void testFindByTypeId2() {
		TemplateId clinicalDocument = TemplateId.CLINICAL_DOCUMENT;
		TemplateId actual = TemplateId.getTemplateId(clinicalDocument.getRoot(),
				clinicalDocument.getExtension(), new Context());

		assertThat(actual).isSameAs(clinicalDocument);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testInvalidExtensionFindWithNoExtensionEnforcement(TemplateId templateId) {
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
				"nonExistingExtension", new Context());

		assertThat(actual).isSameAs(templateId);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class)
	void testInvalidExtensionFindWithExtensionEnforcement(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			"nonExistingExtension", new Context());

		assertThat(actual).isSameAs(TemplateId.DEFAULT);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class)
	void testInvalidExtensionFindWithExtensionEnforcementHappy(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			templateId.getExtension(), new Context());

		assertThat(actual).isSameAs(templateId);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testMissingExtensionFindWithNoExtensionEnforcement(TemplateId templateId) {
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			null, new Context());

		assertThat(actual).isSameAs(templateId);
	}

	@Test
	void testClinicalDocumentInvalidExtensionFindWithNoExtensionEnforcement() {
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			"nonExistingExtension", new Context());

		assertThat(actual).isSameAs(TemplateId.DEFAULT);
	}

	@Test
	void testClinicalDocumentMissingExtensionFindWithExtensionEnforcement() {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			null, new Context());

		assertThat(actual).isSameAs(TemplateId.DEFAULT);
	}

	@Test
	void testClinicalDocumentMissingExtensionFindWithNoExtensionEnforcement() {
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			null, new Context());

		assertThat(actual).isSameAs(TemplateId.DEFAULT);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testMissingExtensionFindWithExtensionEnforcement(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			null, new Context());

		assertThat(actual).isSameAs(templateId);
	}

	@Test
	void testFindByTypeId2NotExistAgain() {
		TemplateId actual = TemplateId.getTemplateId("nonExistingRoot",
				TemplateId.CLINICAL_DOCUMENT.getExtension(), new Context());

		assertThat(actual).isSameAs(TemplateId.DEFAULT);
	}

	@Test
	void testGenerateTemplateIdString() {
		final String root = "asdf";
		final String extension = "jkl;";
		String actual = TemplateId.generateTemplateIdString(root, extension, new Context());

		assertThat(actual).isEqualTo(root + ":" + extension);
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return TemplateId.class;
	}

	@Nested
	static class ExtensionTest implements EnumContract {

		@Override
		public Class<? extends Enum<?>> getEnumType() {
			return Extension.class;
		}

	}
}