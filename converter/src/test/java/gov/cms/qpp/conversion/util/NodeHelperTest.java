package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.lang.reflect.Constructor;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class NodeHelperTest {

	@Test
	void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<NodeHelper> constructor = NodeHelper.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		NodeHelper nodeHelper = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);

		assertWithMessage("Expect to have an instance here ")
			.that(nodeHelper).isInstanceOf(NodeHelper.class);
	}

	@Test
	void findParentTest() {
		Node grandParentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		Node parentNode = new Node(TemplateId.MEASURE_SECTION_V4);
		parentNode.setParent(grandParentNode);

		Node testNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		testNode.setParent(parentNode);

		Node expectedGrandparent = NodeHelper.findParent(testNode, grandParentNode.getType());
		Node expectedParent = NodeHelper.findParent(testNode, parentNode.getType());

		assertThat(expectedGrandparent.getType()).isSameInstanceAs(TemplateId.CLINICAL_DOCUMENT);
		assertThat(expectedParent.getType()).isSameInstanceAs(TemplateId.MEASURE_SECTION_V4);
	}
}
