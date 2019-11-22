package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class QrdaDecoderEngineTest {

	private static final String TEMPLATE_ID = "templateId";
	private static final String ROOT = "root";
	private static final String EXTENSION = "extension";

	private static Context context;

	private static int continueDecodeCount;
	private static int finishDecodeCount;
	private static int escapeDecodeCount;

	@BeforeAll
	static void mockDecoders() {
		context = new Context();
		TestHelper.mockDecoder(context, TestChildContinue.class, new ComponentKey(TemplateId.PI_SECTION, Program.ALL));
		TestHelper.mockDecoder(context, TestChildFinish.class, new ComponentKey(TemplateId.IA_SECTION, Program.ALL));
		TestHelper.mockDecoder(context, TestChildEscape.class, new ComponentKey(TemplateId.MEASURE_SECTION_V3, Program.ALL));
	}

	@BeforeEach
	void resetCounts() {
		continueDecodeCount = 0;
		finishDecodeCount = 0;
		escapeDecodeCount = 0;
	}

	@Test
	void topLevelNodeHasTemplateId() {
		Document document = new Document();

		Element testElement = createGenericElement();
		document.addContent(testElement);

		addChildToParent(testElement, createFinishElement());

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(testElement);

		assertThat(node.getType()).isEqualTo(TemplateId.IA_SECTION);
	}

	@Test
	void topLevelNodeDoesntHaveTemplateId() {
		Document document = new Document();

		Element testElement = createGenericElement();
		document.addContent(testElement);

		Element secondLevelElement = createGenericElement();

		addChildToParent(testElement, secondLevelElement);
		addChildToParent(secondLevelElement, createFinishElement());

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(testElement);

		assertThat(node.getType()).isEqualTo(TemplateId.PLACEHOLDER);
		assertThat(node.getChildNodes().get(0).getType()).isEqualTo(TemplateId.IA_SECTION);
	}

	@Test
	void continueThenContinueThenFinish() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelElement = createContinueElement();
		Element fourthLevelElement = createFinishElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelElement);
		addChildToParent(thirdLevelElement, fourthLevelElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(2, 1, 0);
		assertNodeCount(node, 2, 1, 0);
	}

	@Test
	void continueThenContinueAndEscapeAndContinueWhichStopsDecodingSiblings() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelOneElement = createContinueElement();
		Element thirdLevelTwoElement = createEscapeElement();
		Element thirdLevelThreeElement = createContinueElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelOneElement);
		addChildToParent(middleElement, thirdLevelTwoElement);
		addChildToParent(middleElement, thirdLevelThreeElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(2, 0, 1);
		assertNodeCount(node, 2, 0, 0);
	}

	@Test
	void continueThenContinueAndFinishAndContinueWhichStopsDecodingSiblings() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelOneElement = createContinueElement();
		Element thirdLevelTwoElement = createFinishElement();
		Element thirdLevelThreeElement = createContinueElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelOneElement);
		addChildToParent(middleElement, thirdLevelTwoElement);
		addChildToParent(middleElement, thirdLevelThreeElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(2, 1, 0);
		assertNodeCount(node, 2, 1, 0);
	}

	@Test
	void continueThenContinueAndEscapeThenFinishWhichStopsDecodingChildren() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelOneElement = createContinueElement();
		Element thirdLevelTwoElement = createEscapeElement();
		Element fourthLevelElement = createFinishElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelOneElement);
		addChildToParent(middleElement, thirdLevelTwoElement);
		addChildToParent(thirdLevelTwoElement, fourthLevelElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(2, 0, 1);
		assertNodeCount(node, 2, 0, 0);
	}

	@Test
	void continueThenContinueAndFinishThenFinishWhichStopsDecodingChildren() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelOneElement = createContinueElement();
		Element thirdLevelTwoElement = createFinishElement();
		Element fourthLevelElement = createFinishElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelOneElement);
		addChildToParent(middleElement, thirdLevelTwoElement);
		addChildToParent(thirdLevelTwoElement, fourthLevelElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(2, 1, 0);
		assertNodeCount(node, 2, 1, 0);
	}

	@Test
	void noDecoderWhichActsAsContinue() {
		Element rootElement = createRootElement();

		Element secondLevelElement = createContinueElement();
		Element middleElement = createGenericElement();
		Element thirdLevelOneElement = createNoDecoderElement();
		Element thirdLevelTwoElement = createFinishElement();

		addChildToParent(rootElement, secondLevelElement);
		addChildToParent(secondLevelElement, middleElement);
		addChildToParent(middleElement, thirdLevelOneElement);
		addChildToParent(middleElement, thirdLevelTwoElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node node = objectUnderTest.decode(rootElement);

		assertDecodeResultCount(1, 1, 0);
		assertNodeCount(node, 1, 1, 0);
	}

	@Test
	void testAcceptsFailureDueToNoTemplateId() {
		Element rootElement = createRootElement();

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		assertThat(objectUnderTest.accepts(rootElement)).isFalse();
	}

	@Test
	void testAcceptsFailureDueToIncorrectTemplateId() {
		Element rootElement = createRootElement();
		rootElement.getParentElement().getChildren().add(createContinueElement());

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		assertThat(objectUnderTest.accepts(rootElement)).isFalse();
	}

	@Test
	void testAcceptsFailureDueToNoClinicalDocumentElement() {
		Element rootElement = createRootElement();
		Element clinicalDocumentTemplateIdElement = new Element(TEMPLATE_ID);
		clinicalDocumentTemplateIdElement.setAttribute(ROOT, TemplateId.CLINICAL_DOCUMENT.getRoot());
		rootElement.getParentElement().getChildren().add(clinicalDocumentTemplateIdElement);
		rootElement.getParentElement().setName("somethingElse");

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		assertThat(objectUnderTest.accepts(rootElement)).isFalse();
	}

	@Test
	void testAcceptsSuccess() {
		Element rootElement = createRootElement();
		Element clinicalDocumentTemplateIdElement = new Element(TEMPLATE_ID);
		clinicalDocumentTemplateIdElement.setAttribute(ROOT, TemplateId.CLINICAL_DOCUMENT.getRoot());
		clinicalDocumentTemplateIdElement.setAttribute(EXTENSION, TemplateId.CLINICAL_DOCUMENT.getExtension());
		rootElement.getParentElement().getChildren().add(clinicalDocumentTemplateIdElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		assertThat(objectUnderTest.accepts(rootElement)).isTrue();
	}

	@Test
	void testGetUniqueTemplateIdElements() {
		Element rootElement = createRootElement();
		Element middleElement = createGenericElement();
		Element initialTemplateIdElement = createContinueElement();
		Element duplicateTemplateIdElement = createContinueElement();
		Element fourthLevelElement = createGenericElement();
		Element fifthLevelElement = createFinishElement();

		addChildToParent(rootElement, middleElement);
		addChildToParent(middleElement, initialTemplateIdElement);
		addChildToParent(middleElement, duplicateTemplateIdElement);
		addChildToParent(middleElement, fourthLevelElement);
		addChildToParent(fourthLevelElement, fifthLevelElement);


		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		Node decodedNodes = objectUnderTest.decode(rootElement);

		assertNodeCount(decodedNodes, 1, 1, 0);
	}


	@Nested
	@DisplayName("Describe branch pruning")
	class Prunes//!!!!!!!
	{
		Element rootElement;
		Element aGenericElement;
		Element noDecoderElement1;
		Element noDecoderElement2;
		Element anotherGenericElement;

		@BeforeEach
		void makeElements() {
			rootElement = createRootElement();
			aGenericElement = createGenericElement();
			anotherGenericElement = createGenericElement();
			noDecoderElement1 = createNoDecoderElement();
			noDecoderElement2 = createNoDecoderElement();
		}

		@Test
		@DisplayName("Should prune branches with insignificant children")
		void testPruneInsignificantBranches() {
			addChildToParent(rootElement, aGenericElement);
			addChildToParent(aGenericElement, noDecoderElement1);
			addChildToParent(aGenericElement, noDecoderElement2);

			QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
			Node decodedNodes = objectUnderTest.decode(rootElement);

			assertNodeCount(decodedNodes, 0, 0, 0);
		}

		@Test
		@DisplayName("Should prune branches with insignificant children but significant grand children")
		void testPruneInsignificantChildrenSignificantGrandChildrenWhenNoDefaults() {
			addChildToParent(rootElement, aGenericElement);
			addChildToParent(aGenericElement, noDecoderElement1);
			addChildToParent(aGenericElement, noDecoderElement2);
			addChildToParent(aGenericElement, anotherGenericElement);
			addChildToParent(anotherGenericElement, createContinueElement());

			QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
			Node decodedNodes = objectUnderTest.decode(rootElement);

			assertNodeCount(decodedNodes, 0, 0, 0);
		}

		@Test
		@DisplayName("Should not prune branches with invalid descendancy")
		void testPruneInvalidChildren() {
			addChildToParent(rootElement, aGenericElement);
			addChildToParent(aGenericElement, createContinueElement());
			addChildToParent(aGenericElement, anotherGenericElement);
			addChildToParent(anotherGenericElement, createContinueElement());

			QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
			Node decodedNodes = objectUnderTest.decode(rootElement);

			assertNodeCount(decodedNodes, 2, 0, 0);
		}

		@Test
		@DisplayName("Should not prune template-less branches with valid descendant elements")
		void testPrunePassThroughElements() {
			addChildToParent(rootElement, aGenericElement);
			addChildToParent(aGenericElement, anotherGenericElement);
			addChildToParent(anotherGenericElement, createContinueElement());

			QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
			Node decodedNodes = objectUnderTest.decode(rootElement);

			assertNodeCount(decodedNodes, 1, 0, 0);
		}

		@Test
		@DisplayName("Should not prune branches with a supported template amongst unsupported templates")
		void testCinderellaAndHerEvilStepSisters() {
			addChildToParent(rootElement, aGenericElement);
			addChildToParent(aGenericElement, anotherGenericElement);
			addChildToParent(anotherGenericElement, noDecoderElement1);
			addChildToParent(anotherGenericElement, noDecoderElement2);
			addChildToParent(anotherGenericElement, createContinueElement());

			QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
			Node decodedNodes = objectUnderTest.decode(rootElement);

			assertNodeCount(decodedNodes, 1, 0, 0);
		}
	}

	private Element createContinueElement() {
		Element element = new Element(TEMPLATE_ID);
		element.setAttribute(ROOT, TemplateId.PI_SECTION.getRoot());

		return element;
	}

	private Element createFinishElement() {
		Element element = new Element(TEMPLATE_ID);
		element.setAttribute(ROOT, TemplateId.IA_SECTION.getRoot());

		return element;
	}

	private Element createEscapeElement() {
		Element element = new Element(TEMPLATE_ID);
		element.setAttribute(ROOT, TemplateId.MEASURE_SECTION_V3.getRoot());

		return element;
	}

	private Element createNoDecoderElement() {
		Element element = new Element(TEMPLATE_ID);
		element.setAttribute(ROOT, "DogCowGoesMoofWhichIsn'tARealTemplateId");

		return element;
	}

	private Element createGenericElement() {
		return new Element("genericElement");
	}

	private Element createRootElement() {
		Document document = new Document();

		Element trueRootElement = new Element("ClinicalDocument");
		document.addContent(trueRootElement);

		Element subRootElement = createGenericElement();
		addChildToParent(trueRootElement, subRootElement);

		return subRootElement;
	}

	private void addChildToParent(Element parent, Element child) {
		parent.getChildren().add(child);
	}

	private void assertDecodeResultCount(int continueCount, int finishCount, int escapeCount) {
		assertThat(continueDecodeCount).isEqualTo(continueCount);
		assertThat(finishDecodeCount).isEqualTo(finishCount);
		assertThat(escapeDecodeCount).isEqualTo(escapeCount);
	}

	private void assertNodeCount(final Node rootNode, final int continueCount, final int finishCount, final int escapeCount) {
		assertThat(countChildNodes(rootNode, TemplateId.PI_SECTION)).isEqualTo(continueCount);
		assertThat(countChildNodes(rootNode, TemplateId.IA_SECTION)).isEqualTo(finishCount);
		assertThat(countChildNodes(rootNode, TemplateId.MEASURE_SECTION_V3)).isEqualTo(escapeCount);
	}

	private Long countChildNodes(final Node node, final TemplateId templateId) {
		Long directDescendantCount = node.getChildNodes(templateId).count();
		Long indirectDescendantCount = node.getChildNodes().stream().mapToLong(nestedNode -> countChildNodes(nestedNode, templateId)).sum();
		return directDescendantCount + indirectDescendantCount;
	}

	public static class TestChildContinue extends QrdaDecoder {
		public TestChildContinue(Context context) {
			super(context);
		}

		@Override
		public DecodeResult decode(Element element, Node childNode) {
			continueDecodeCount++;
			return DecodeResult.TREE_CONTINUE;
		}
	}

	public static class TestChildFinish extends QrdaDecoder {
		public TestChildFinish(Context context) {
			super(context);
		}

		@Override
		public DecodeResult decode(Element element, Node childNode) {
			finishDecodeCount++;
			return DecodeResult.TREE_FINISHED;
		}
	}

	public static class TestChildEscape extends QrdaDecoder {
		public TestChildEscape(Context context) {
			super(context);
		}

		@Override
		public DecodeResult decode(Element element, Node childNode) {
			escapeDecodeCount++;
			return DecodeResult.TREE_ESCAPED;
		}
	}
}