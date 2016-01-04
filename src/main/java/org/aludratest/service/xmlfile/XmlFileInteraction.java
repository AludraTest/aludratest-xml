package org.aludratest.service.xmlfile;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.service.AttachParameter;
import org.aludratest.service.AttachResult;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.Interaction;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.w3c.dom.Document;

public interface XmlFileInteraction extends Interaction {

	/**
	 * Polls the file system until a file at the given path is found or a timeout occurs.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param filePath
	 */
	void waitUntilExists(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator String filePath);

	/**
	 * Polls the file system until no file is found at the given path.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param filePath
	 */
	void waitUntilNotExists(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator String filePath);

	/**
	 * Deletes a file.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param filePath
	 *            the path of the file to delete
	 */
	void delete(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator String filePath);

	/**
	 * Writes an XML document to a file.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            The document to write.
	 * @param filePath
	 *            The path of the file to write.
	 * @param overwrite
	 *            Flag that indicates whether a pre-existing file may be overwritten. If <code>false</code>, an existing file with
	 *            the file path will cause the method to fail.
	 */
	void writeXml(@ElementType String elementType, @ElementName String elementName,
			@AttachParameter("Document") Document document, @TechnicalLocator String filePath,
			@TechnicalArgument boolean overwrite);

	/**
	 * Creates an XML document based on a template file and a variable tree. The template file must be located on the classpath of
	 * the Classloader, and must have a valid FreeMarker template format.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param templateUri
	 *            The path of the template file.
	 * @param templateEncoding
	 *            The encoding of the template file.
	 * @param variables
	 *            the variables for the FreeMarker template engine.
	 * @return a new Document containing the information from the variables.
	 */
	@AttachResult("Created Document")
	Document createDocument(@ElementType String elementType, @ElementName String elementName,
			@TechnicalLocator String templateUri, @TechnicalArgument String templateEncoding,
			@TechnicalArgument Map<String, Object> variables);

	/**
	 * Performs an XPath query on the given XML document.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            the XML document to query
	 * @param xpathQuery
	 *            the XPath query to perform
	 * @param returnType
	 *            determines the type of the returned object: {@link XPathConstants#STRING} for a single {@link java.lang.String},
	 *            {@link XPathConstants#NODE} for a single {@link org.w3c.dom.Element}, {@link XPathConstants#NODESET} for a
	 *            {@link org.w3c.dom.NodeList}
	 * @return the query result
	 */
	Object queryXml(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpathQuery, @TechnicalArgument QName returnType);

	/**
	 * Reports the differences between two XML documents.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param expected
	 *            the expected document structure.
	 * @param actual
	 *            the actual document structure.
	 * @param settings
	 *            the settings for XML comparison.
	 * @return a list of the differences.
	 */
	AggregateXmlDiff diff(@ElementType String elementType, @ElementName String elementName, Document expected, Document actual,
			XmlComparisonSettings settings);

}
