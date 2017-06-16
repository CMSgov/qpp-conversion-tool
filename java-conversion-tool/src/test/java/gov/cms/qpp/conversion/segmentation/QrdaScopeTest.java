package gov.cms.qpp.conversion.segmentation;


import static gov.cms.qpp.conversion.segmentation.QrdaScope.ACI_AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.MEASURE_PERFORMED;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class QrdaScopeTest {
	@Test
	public void testGetTemplates() {
		//when
		Set<QrdaScope> scopes = new HashSet<>();
		scopes.add(ACI_AGGREGATE_COUNT);
		scopes.add(MEASURE_PERFORMED);

		//then
		assertEquals(2, QrdaScope.getTemplates(scopes).size());
	}

	@Test
	public void testGetTemplatesNull() {
		//expect
		assertEquals(0, QrdaScope.getTemplates(null).size());
	}

	@Test
	public void testGetTemplatesEmpty() {
		//expect
		assertEquals(0, QrdaScope.getTemplates(new HashSet<>()).size());
	}

	@Test
	public void testValueOfString() {
		//JaCoCo coverage test
		QrdaScope scope = QrdaScope.valueOf("CLINICAL_DOCUMENT");
		assertThat("QrdaScope of CLINICAL_DOCUMENT equals TemplateId", scope.name(), is("CLINICAL_DOCUMENT"));
	}
}
