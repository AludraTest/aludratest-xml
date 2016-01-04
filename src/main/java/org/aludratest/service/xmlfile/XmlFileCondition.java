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
package org.aludratest.service.xmlfile;

import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.service.Condition;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.databene.commons.Validator;
import org.w3c.dom.Document;

public interface XmlFileCondition extends Condition {

	/**
	 * Tells if a file exists at the given path.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param filePath
	 *            the file path to query
	 * @return true if a file with the provided path exists, otherwise false
	 */
	boolean exists(@ElementType String elementType, @ElementName String elementName, @TechnicalLocator String filePath);

	/**
	 * Tells if the two documents are equal, compared using the given comparison settings.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param expected
	 *            Expected document.
	 * @param actual
	 *            Actual document to compare against expected document.
	 * @param settings
	 *            Comparison settings, e.g. defining tolerated differences.
	 * 
	 * @return <code>true</code> if both documents are equal regarding to the given comparison settings, <code>false</code>
	 *         otherwise.
	 */
	boolean areDocumentsEqual(@ElementType String elementType, @ElementName String elementName, Document expected,
			Document actual, @TechnicalArgument XmlComparisonSettings settings);

	/**
	 * Tells if the given XPath node in the document exists.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            Document to check
	 * @param xpath
	 *            XPath locating the node to check.
	 * @return <code>true</code> if a node with the given XPath exists in the document, <code>false</code> otherwise.
	 */
	boolean nodeExists(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpath);

	/**
	 * Tells if the String representation of the given node (located by XPath) matches the given validator.
	 * 
	 * @param elementType
	 * @param elementName
	 * @param document
	 *            Document to check.
	 * @param xpath
	 *            XPath locating the node to check.
	 * @param validator
	 *            Validator to match the string representation of the node against.
	 * @return <code>true</code> if the string representation of the node matches the Validator, <code>false</code> otherwise.
	 */
	boolean nodeMatches(@ElementType String elementType, @ElementName String elementName, Document document,
			@TechnicalLocator String xpath, Validator<String> validator);
}
