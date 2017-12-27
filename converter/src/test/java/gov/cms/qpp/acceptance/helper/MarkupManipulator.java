package gov.cms.qpp.acceptance.helper;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class MarkupManipulator {
	private static TransformerFactory tf = TransformerFactory.newInstance();
	private static XPathFactory xpf = XPathFactory.newInstance();

	private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private Document document;
	private String pathname;

	private MarkupManipulator(String path, boolean nsAware) {
		if (nsAware) {
			dbf.setNamespaceAware(true);
		}
		pathname = path;
	}

	public InputStream upsetTheNorm(String xPath, boolean remove) {
		try {
			document = dbf.newDocumentBuilder().parse(new File(pathname));
			XPath xpath = xpf.newXPath();
			XPathExpression expression = xpath.compile(xPath);

			NodeList searchedNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
			if (searchedNodes == null) {
				System.out.println("bad path: " + xPath);
			} else {
				for (int i = 0; i < searchedNodes.getLength(); i++) {
					Node searched = searchedNodes.item(i);

					Node owningElement = (searched instanceof ElementImpl)
							? searched
							: ((AttrImpl) searched).getOwnerElement();

					Node containingParent = owningElement.getParentNode();

					if (remove) {
						containingParent.removeChild(owningElement);
					} else {
						containingParent.appendChild(owningElement.cloneNode(true));
					}

				}
			}

			Transformer t = tf.newTransformer();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Result result = new StreamResult(os);
			t.transform(new DOMSource(document), result);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (ParserConfigurationException | IOException | SAXException |
				XPathExpressionException | TransformerException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static final class MarkupManipulatorBuilder {
		private String pathname;
		private boolean nsAware;

		public MarkupManipulator build() throws IOException, SAXException, ParserConfigurationException {
			return new MarkupManipulator(pathname, nsAware);
		}

		public MarkupManipulatorBuilder setPathname(Path path) {
			this.pathname = path.toFile().getAbsolutePath();
			return this;
		}

		public MarkupManipulatorBuilder setPathname(String path) {
			this.pathname = path;
			return this;
		}

		public MarkupManipulatorBuilder setNsAware(boolean aware) {
			this.nsAware = aware;
			return this;
		}
	}
}

