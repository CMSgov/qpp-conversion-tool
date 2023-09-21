package gov.cms.qpp.conversion.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.TemplateId.Extension;
import gov.cms.qpp.test.enums.EnumContract;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class TemplateIdTest implements EnumContract {

	static Context defaultsContext;

	@BeforeAll
	static void setup() {
		defaultsContext = new Context();
	}

	@AfterEach
	void cleanUp() {
		System.clearProperty(Extension.STRICT_EXTENSION);
	}

	@Test
	void testRoot() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getRoot()).isEqualTo("2.16.840.1.113883.10.20.27.1.2");
	}

	@Test
	void testExtension() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getExtension()).isEqualTo("2022-12-01");
	}

	@Test
	void testHumanReadableTitle() {
		assertThat(TemplateId.CLINICAL_DOCUMENT.getHumanReadableTitle()).isEqualTo("Clinical Document");
	}

	@Test
	void testHumanReadableTitleDoesntExist() {
		assertThat(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2.getHumanReadableTitle()).isNull();
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

		assertThat(actual).isSameInstanceAs(clinicalDocument);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testInvalidExtensionFindWithNoExtensionEnforcement(TemplateId templateId) {
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
				"nonExistingExtension", new Context());

		assertThat(actual).isSameInstanceAs(templateId);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class)
	void testInvalidExtensionFindWithExtensionEnforcement(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			"nonExistingExtension", defaultsContext);

		assertThat(actual).isSameInstanceAs(TemplateId.UNIMPLEMENTED);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class)
	void testInvalidExtensionFindWithExtensionEnforcementHappy(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			templateId.getExtension(), new Context());

		assertThat(actual).isSameInstanceAs(templateId);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testMissingExtensionFindWithNoExtensionEnforcement(TemplateId templateId) {
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			null, defaultsContext);

		assertThat(actual).isSameInstanceAs(templateId);
	}

	@Test
	void testClinicalDocumentInvalidExtensionFindWithNoExtensionEnforcement() {
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			"nonExistingExtension", defaultsContext);

		assertThat(actual).isSameInstanceAs(TemplateId.UNIMPLEMENTED);
	}

	@Test
	void testClinicalDocumentMissingExtensionFindWithExtensionEnforcement() {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			null, defaultsContext);

		assertThat(actual).isSameInstanceAs(TemplateId.UNIMPLEMENTED);
	}

	@Test
	void testClinicalDocumentMissingExtensionFindWithNoExtensionEnforcement() {
		TemplateId actual = TemplateId.getTemplateId(TemplateId.CLINICAL_DOCUMENT.getRoot(),
			null, defaultsContext);

		assertThat(actual).isSameInstanceAs(TemplateId.UNIMPLEMENTED);
	}

	@ParameterizedTest
	@EnumSource(value = TemplateId.class, mode = EXCLUDE, names = { "CLINICAL_DOCUMENT" })
	void testMissingExtensionFindWithExtensionEnforcement(TemplateId templateId) {
		System.setProperty(Extension.STRICT_EXTENSION, "yep");
		TemplateId actual = TemplateId.getTemplateId(templateId.getRoot(),
			null, defaultsContext);

		assertThat(actual).isSameInstanceAs(templateId);
	}

	@Test
	void testFindByTypeId2NotExistAgain() {
		TemplateId actual = TemplateId.getTemplateId("nonExistingRoot",
				TemplateId.CLINICAL_DOCUMENT.getExtension(), defaultsContext);

		assertThat(actual).isSameInstanceAs(TemplateId.UNIMPLEMENTED);
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