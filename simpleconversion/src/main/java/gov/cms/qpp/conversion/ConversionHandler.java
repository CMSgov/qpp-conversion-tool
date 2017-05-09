package gov.cms.qpp.conversion;

import gov.cms.qpp.model.TemplateId;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;


public class ConversionHandler extends DefaultHandler {
	private Stack<String> docDescendancy;
	private Stack<TieredDecoder> decoderDescendancy;
	private TieredDecoder activeDecoder;

	public void startDocument() throws SAXException {
		docDescendancy = new Stack<>();
		decoderDescendancy = new Stack<>();
	}

	public void startElement (String uri, String localName,
							  String qName, Attributes attributes)
			throws SAXException
	{
		docDescendancy.push(qName);

		if (qName.equals("templateId")) {
			TieredDecoder decoder = findDecoder(attributes);
			if (decoder != null) {
				decoder.setTier(docDescendancy.size() - 1);
				decoder.associateWith(activeDecoder);
				decoderDescendancy.push(decoder);
				activeDecoder = decoder;
			}
		}

		if (activeDecoder != null) {
			activeDecoder.handleStartElement(uri, localName, qName, attributes);
		}
	}

	private TieredDecoder findDecoder(Attributes attributes) {
		if (attributes.getValue("root").equals(TemplateId.CLINICAL_DOCUMENT.getRoot())) {
			return new ClinicalDocumentDecoder();
		} else if (attributes.getValue("root").equals(TemplateId.ACI_SECTION.getRoot())) {
			return new AciSectionDecoder();
		} else if (attributes.getValue("root").equals(TemplateId.ACI_NUMERATOR_DENOMINATOR.getRoot())) {
			return new AciNumeratorDenominatorDecoder();
		} else {
			return null;
		}
	}

	public void endElement (String uri, String localName, String qName)
			throws SAXException
	{
		if (activeDecoder != null && activeDecoder.getTier() == docDescendancy.size()){
			System.out.println(activeDecoder.exportDecoded().toString());
			decoderDescendancy.pop();
			if (!decoderDescendancy.empty()) {
				activeDecoder = decoderDescendancy.peek();
			}
		}
		docDescendancy.pop();
	}

	/**
	 * Receive notification of a parser warning.
	 *
	 * <p>The default implementation does nothing.  Application writers
	 * may override this method in a subclass to take specific actions
	 * for each warning, such as inserting the message in a log file or
	 * printing it to the console.</p>
	 *
	 * @param e The warning information encoded as an exception.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#warning
	 * @see org.xml.sax.SAXParseException
	 */
	public void warning (SAXParseException e)
			throws SAXException
	{
		System.out.println("warning");
		e.printStackTrace(System.out);
	}


	/**
	 * Receive notification of a recoverable parser error.
	 *
	 * <p>The default implementation does nothing.  Application writers
	 * may override this method in a subclass to take specific actions
	 * for each error, such as inserting the message in a log file or
	 * printing it to the console.</p>
	 *
	 * @param e The error information encoded as an exception.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#warning
	 * @see org.xml.sax.SAXParseException
	 */
	public void error (SAXParseException e)
			throws SAXException
	{
		System.out.println("error");
		e.printStackTrace(System.out);
	}


	/**
	 * Report a fatal XML parsing error.
	 *
	 * <p>The default implementation throws a SAXParseException.
	 * Application writers may override this method in a subclass if
	 * they need to take specific actions for each fatal error (such as
	 * collecting all of the errors into a single report): in any case,
	 * the application must stop all regular processing when this
	 * method is invoked, since the document is no longer reliable, and
	 * the parser may no longer report parsing events.</p>
	 *
	 * @param e The error information encoded as an exception.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ErrorHandler#fatalError
	 * @see org.xml.sax.SAXParseException
	 */
	public void fatalError (SAXParseException e)
			throws SAXException
	{
		System.out.println("fatal");
		e.printStackTrace(System.out);
		throw e;
	}
}
