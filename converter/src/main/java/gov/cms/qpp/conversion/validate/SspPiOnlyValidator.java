package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;

import static gov.cms.qpp.conversion.model.Constants.*;

/**
 * Validates that SSP submissions only contain PI‐category measure sections.
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.SSP)
public class SspPiOnlyValidator extends NodeValidator {

    @Override
    protected void performValidation(Node clinicalDocument) {
        // only apply when programName == "ssp"
        String program = clinicalDocument.getValue(PROGRAM_NAME);
        if (!SSP_PROGRAM_NAME.equalsIgnoreCase(program)) {
            return;
        }

        // for each QRDA Category III measure section…
        clinicalDocument
                .getChildNodes(TemplateId.MEASURE_SECTION_V5)
                .forEach(sectionNode -> {
                    String category = sectionNode.getValue(CATEGORY);

                    // enforce category == "pi"
                    checkErrors(sectionNode)
                            .valueIn(
                                    // Problem message will include the actual bad category
                                    ProblemCode.SSP_PI_ONLY_MEASURE_CATEGORY
                                            .format(category == null ? "<missing>" : category),
                                    CATEGORY,
                                    "pi"
                            );
                });
    }
}
