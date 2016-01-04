package org.aludratest.service.xmlfile;

import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.service.Verification;
import org.databene.commons.Validator;
import org.w3c.dom.Document;

public interface XmlFileVerification extends Verification {

	/**
	 * Asserts that two XML documents are equal.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param expected
	 *            the expected document.
	 * @param actual
	 *            the actual document.
	 * @param settings
	 *            the {@link XmlComparisonSettings} to apply.
	 */
	void assertDocumentsEqual(@ElementType String elementType, @ElementName String elementName, Document expected,
			Document actual, @TechnicalArgument XmlComparisonSettings settings);

	/**
	 * Asserts that the given XPath node in the document exists.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            Document to check
	 * @param xpath
	 *            XPath locating the node to check.
	 */
	void assertNodeExists(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpath);

	/**
	 * Asserts that the given XPath node in the document does NOT exist.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            Document to check
	 * @param xpath
	 *            XPath locating the node to check.
	 */
	void assertNodeNotExists(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpath);

	/**
	 * Asserts that the String representation of the given node (located by XPath) matches the given validator.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            Document to check.
	 * @param xpath
	 *            XPath locating the node to check.
	 * @param validator
	 *            Validator to match the string representation of the node against.
	 */
	void assertNodeMatches(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpath, Validator<String> validator);
}
