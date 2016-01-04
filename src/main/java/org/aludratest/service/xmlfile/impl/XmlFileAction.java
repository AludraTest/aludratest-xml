/*
 * Copyright (C) 2015 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.service.xmlfile.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlContent;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.FileService;
import org.aludratest.service.xmlfile.XmlFileCondition;
import org.aludratest.service.xmlfile.XmlFileInteraction;
import org.aludratest.service.xmlfile.XmlFileVerification;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.databene.commons.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlFileAction implements XmlFileInteraction, XmlFileCondition, XmlFileVerification {

	private FileService fileService;

	private XmlContent contentHandler;

	private String encoding;

	public XmlFileAction(XmlContent contentHandler, FileService fileService, String encoding) {
		this.fileService = fileService;
		this.contentHandler = contentHandler;
		this.encoding = encoding;
	}

	@Override
	public List<Attachment> createDebugAttachments() {
		// none available
		return null;
	}

	@Override
	public List<Attachment> createAttachments(Object object, String title) {
		if (object instanceof Document) {
			StringWriter sw = new StringWriter();
			try {
				documentToXml((Document) object, sw);
				return Collections.<Attachment> singletonList(new StringAttachment(title, sw.toString(), "xml"));
			}
			catch (IOException e) {
				return Collections.singletonList(createErrorAttachment("Error when serializing XML", e));
			}
		}

		return null;
	}

	@Override
	public void setSystemConnector(SystemConnector systemConnector) {
		// not supported / required for XmlFileService
	}

	@Override
	public void assertDocumentsEqual(String elementType, String elementName, Document expected, Document actual, XmlComparisonSettings settings) {
		if (!contentHandler.isEqual(expected, actual, settings)) {
			throw new FunctionalFailure("The two XML documents do not match.");
		}
	}

	@Override
	public boolean exists(String elementType, String elementName, String filePath) {
		return fileService.check().exists(filePath);
	}

	@Override
	public boolean areDocumentsEqual(String elementType, String elementName, Document expected, Document actual,
			XmlComparisonSettings settings) {
		return contentHandler.isEqual(expected, actual, settings);
	}

	@Override
	public void assertNodeExists(String elementType, String elementName, Document document, String xpath) {
		if (!nodeExists(elementType, elementName, document, xpath)) {
			throw new FunctionalFailure("Node " + xpath + " does not exist in document although expected");
		}
	}

	@Override
	public void assertNodeNotExists(String elementType, String elementName, Document document, String xpath) {
		if (nodeExists(elementType, elementName, document, xpath)) {
			throw new FunctionalFailure("Node " + xpath + " exists in document although not expected");
		}
	}

	@Override
	public void assertNodeMatches(String elementType, String elementName, Document document, String xpath,
			Validator<String> validator) {
		if (!nodeMatches(elementType, elementName, document, xpath, validator)) {
			throw new FunctionalFailure("Node " + xpath + " does not match validator");
		}
	}

	@Override
	public boolean nodeExists(String elementType, String elementName, Document document, String xpath) {
		NodeList nl = (NodeList) contentHandler.queryXPath(document, xpath, XPathConstants.NODESET);
		return nl != null && nl.getLength() > 0;
	}

	@Override
	public boolean nodeMatches(String elementType, String elementName, Document document, String xpath,
			Validator<String> validator) {
		String str = (String) contentHandler.queryXPath(document, xpath, XPathConstants.STRING);
		return str != null && validator.valid(str);
	}

	@Override
	public Object queryXml(String elementType, String elementName, Document document, String xpathQuery, QName returnType) {
		return contentHandler.queryXPath(document, xpathQuery, returnType);
	}

	@Override
	public AggregateXmlDiff diff(String elementType, String elementName, Document expected, Document actual,
			XmlComparisonSettings settings) {
		return contentHandler.compare(expected, actual, settings);
	}

	@Override
	public void waitUntilExists(String elementType, String elementName, String filePath) {
		fileService.perform().waitUntilExists(elementType, filePath);
	}

	@Override
	public void waitUntilNotExists(String elementType, String elementName, String filePath) {
		fileService.perform().waitUntilNotExists(filePath);
	}

	@Override
	public void delete(String elementType, String elementName, String filePath) {
		fileService.perform().delete(filePath);
	}

	@Override
	public void writeXml(String elementType, String elementName, Document document, String filePath, boolean overwrite) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		contentHandler.writeDocument(document, this.encoding, baos);
		fileService.perform().writeBinaryFile(filePath, baos.toByteArray(), overwrite);
	}

	@Override
	public Document createDocument(String elementType, String elementName, String templateUri, String templateEncoding, Map<String, Object> variables) {
		if (variables == null) {
			variables = Collections.emptyMap();
		}
		try {
			return contentHandler.createDocument(templateUri, templateEncoding, variables);
		}
		catch (Exception e) {
			throw new AutomationException("Could not create XML document from template", e);
		}
	}

	private void documentToXml(Document document, Writer writer) throws IOException {
		DOMSource src = new DOMSource(document);
		StreamResult res = new StreamResult(writer);

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer tf = factory.newTransformer();
			tf.transform(src, res);
		}
		catch (TransformerException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}

			throw new IOException("Could not write XML", e);
		}
	}

	private Attachment createErrorAttachment(String message, IOException e) {
		StringWriter sw = new StringWriter();
		sw.append(message).append("\n");
		e.printStackTrace(new PrintWriter(sw));

		return new StringAttachment(message, sw.toString(), "txt");
	}



}
