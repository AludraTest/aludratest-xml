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
import org.aludratest.content.xml.XmlDiffDetailType;
import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.service.xmlfile.data.XmlKeyExpressionData;
import org.aludratest.util.data.StringData;
import org.w3c.dom.Document;

/**
 * Verifies the content of an XML file against a reference document.
 * @param <E> Generic parameter that should be set by child classes to themselves
 * @author Volker Bergmann
 */
@SuppressWarnings("unchecked")
public class XmlFileVerifier<E extends XmlFileVerifier<E>> implements ActionWordLibrary<E> {

    private final String filePath;
    private final XmlFileService service;
    private final XmlComparisonSettings settings;
    private final String elementType;

    /** Constructor.
     *  @param filePath
     *  @param service */
    public XmlFileVerifier(String filePath, XmlFileService service, XmlComparisonSettings settings) {
        this.filePath = filePath;
        this.service = service;
        this.settings = settings;
        this.elementType = getClass().getSimpleName();
    }

    /** Adds an XPath expression of which all matching XML elements are ignored
     *  @param path an XPath expressions of the XML elements to ignore in comparison
     *  @return a reference to the invoked XmlFileVerifier instance */
    public E addExclusionPath(StringData path) {
        this.settings.tolerateAnyDiffAt(path.getValue());
        return (E) this;
    }

    /** Allows the given diff type in comparisons.
     * @param type the type of difference
     * @param xPath the path where the difference is tolerated
     * @return a reference to the invoked XmlFileVerifier instance */
    public E addToleratedDiff(XmlDiffDetailType type, String xPath) {
        this.settings.tolerateGenericDiff(type, xPath);
        return (E) this;
    }

    /** Adds a key expression to the verifier. It is an XPath expression by which XmlContent
     *  can determine the identity of an XML element
     *  @param keyExpression the XPath expression that provides the id
     *  @return a reference to the invoked XmlFileVerifier instance
     */
    public E addKeyExpression(XmlKeyExpressionData keyExpression) {
        this.settings.addKeyExpression(keyExpression.getElementName(), keyExpression.getKeyExpression());
        return (E) this;
    }

    /** Deletes the file.
     *  @return a reference to the FileStream object itself */
    public E delete() {
        service.perform().delete(elementType, null, filePath);
        return (E) this;
    }

    /** Polls the file system until a file at the given path is found
     *  or a timeout occurs.
     *  @return a reference to the FileStream object itself */
    public E waitUntilExists() {
        service.perform().waitUntilExists(elementType, null, filePath);
        return (E) this;
    }

    /** Polls the file system until no file is found at the given path.
     *  @return a reference to the FileStream object itself */
    public E waitUntilNotExists() {
        service.perform().waitUntilNotExists(elementType, null, filePath);
        return (E) this;
    }

    /** Asserts that the interchange stored in this document is equals to the provided
     *  interchange, ignoring the provided paths.
     *  @param referenceFileName the name of the reference file to verify against
     *  @return a reference to the invoked XmlFileVerifier instance */
    public E verifyWith(StringData referenceFileName) {
        Document expected = service.perform().readDocument(
                elementType, "reference file", referenceFileName.getValue());
        Document actual = service.perform().readDocument(
                elementType, "actual file", this.filePath);
        service.verify().assertDocumentsEqual(elementType, null, expected, actual, settings);
        return (E) this;
    }

    @Override
    public E verifyState() {
        return (E) this;
    }

}
