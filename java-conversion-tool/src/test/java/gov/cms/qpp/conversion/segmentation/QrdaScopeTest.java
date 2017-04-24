package gov.cms.qpp.conversion.segmentation;


import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static gov.cms.qpp.conversion.segmentation.QrdaScope.ACI_AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.segmentation.QrdaScope.MEASURE_PERFORMED;
import static junit.framework.TestCase.assertEquals;

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
		assertEquals(null, QrdaScope.getTemplates(null));
	}

	@Test
	public void testGetTemplatesEmpty() {
		//expect
		assertEquals(0, QrdaScope.getTemplates(new HashSet<>()).size());
	}
}
