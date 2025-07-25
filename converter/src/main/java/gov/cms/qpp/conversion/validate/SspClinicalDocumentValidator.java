package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;

import java.util.Locale;

import static gov.cms.qpp.conversion.model.Constants.CATEGORY;
import static gov.cms.qpp.conversion.model.Constants.PROGRAM_NAME;

@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.SSP)
public class SspClinicalDocumentValidator extends NodeValidator {
    private static final String ALLOWED_CATEGORY = "pi";

    @Override
    protected void performValidation(Node clinicalDocument) {
        String programName = clinicalDocument.getValue(PROGRAM_NAME).toUpperCase(Locale.ROOT);
        clinicalDocument
                .getChildNodes(TemplateId.MEASURE_SECTION_V5)
                .forEach(sectionNode -> {
                    String category = sectionNode.getValue(CATEGORY);
                    System.out.println("category: " + category);
                    if (category == null || !ALLOWED_CATEGORY.equalsIgnoreCase(category)) {
                        LocalizedProblem error = ProblemCode.SSP_PI_ONLY_MEASURE_CATEGORY
                                .format(programName, category == null ? "none" : category);
                        checkErrors(sectionNode)
                                .valueIn(error, CATEGORY, ALLOWED_CATEGORY);
                    }
                });
    }
}