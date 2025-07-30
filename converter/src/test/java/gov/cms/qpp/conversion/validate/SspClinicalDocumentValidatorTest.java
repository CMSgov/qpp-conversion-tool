package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class SspClinicalDocumentValidatorTest {

    private Node clinicalDocumentNode;
    private Node measureSectionNode;

    @BeforeEach
    void setUp() {
        clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
        clinicalDocumentNode.putValue("programName", "SSP");

        measureSectionNode = new Node(TemplateId.MEASURE_SECTION_V5);
    }

    @Test
    void testValidCategory_NoErrors() {
        measureSectionNode.putValue("category", "pi");
        clinicalDocumentNode.addChildNode(measureSectionNode);

        SspClinicalDocumentValidator validator = new SspClinicalDocumentValidator();
        List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

        assertWithMessage("There should be no errors for category 'pi'")
                .that(errors).isEmpty();
    }

    @Test
    void testNullCategory_ShouldError() {
        // category is not set
        clinicalDocumentNode.addChildNode(measureSectionNode);

        SspClinicalDocumentValidator validator = new SspClinicalDocumentValidator();
        List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

        assertWithMessage("Must report SSP_PI_ONLY_MEASURE_CATEGORY when category is null")
                .that(errors)
                .comparingElementsUsing(DetailsErrorEquals.INSTANCE)
                .containsExactly(
                        ProblemCode.SSP_PI_ONLY_MEASURE_CATEGORY.format("SSP", "none")
                );
    }

    @Test
    void testInvalidCategory_ShouldError() {
        measureSectionNode.putValue("category", "invalid");
        clinicalDocumentNode.addChildNode(measureSectionNode);

        SspClinicalDocumentValidator validator = new SspClinicalDocumentValidator();
        List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

        assertWithMessage("Must report SSP_PI_ONLY_MEASURE_CATEGORY when category is invalid")
                .that(errors)
                .comparingElementsUsing(DetailsErrorEquals.INSTANCE)
                .containsExactly(
                        ProblemCode.SSP_PI_ONLY_MEASURE_CATEGORY.format("SSP", "invalid")
                );
    }
}
