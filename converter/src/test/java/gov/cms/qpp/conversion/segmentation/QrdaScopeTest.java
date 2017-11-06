package gov.cms.qpp.conversion.segmentation;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.ACI_AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.MEASURE_PERFORMED;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class QrdaScopeTest {

	@Test
	void testGetTemplates() {
		//when
		Set<QrdaScope> scopes = new HashSet<>();
		scopes.add(ACI_AGGREGATE_COUNT);
		scopes.add(MEASURE_PERFORMED);

		//then
		assertWithMessage("Should be two scopes")
				.that(QrdaScope.getTemplates(scopes)).hasSize(2);
	}

	@Test
	void testGetTemplatesNull() {
		//expect
		assertWithMessage("Should be no scopes")
				.that(QrdaScope.getTemplates(null)).isEmpty();
	}

	@Test
	void testGetTemplatesEmpty() {
		//expect
		assertWithMessage("Should be no scopes")
				.that(QrdaScope.getTemplates(new HashSet<>())).isEmpty();
	}

	@Test
	void testValueOfString() {
		//JaCoCo coverage test
		QrdaScope scope = QrdaScope.valueOf("CLINICAL_DOCUMENT");
		assertWithMessage("QrdaScope of CLINICAL_DOCUMENT equals TemplateId")
				.that(scope.name()).isSameAs("CLINICAL_DOCUMENT");
	}
}
