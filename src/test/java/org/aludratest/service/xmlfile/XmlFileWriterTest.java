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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.xpath.XPathConstants;

import org.aludratest.testcase.TestStatus;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the {@link XmlFileWriter}.
 * @author Volker Bergmann
 */

public class XmlFileWriterTest extends AbstractXmlFileServiceTest {

	@Test
	public void testCreateDocument_nonExisting() {
		MyXmlFileWriter writer = new MyXmlFileWriter("testCreateDocument_nonExisting.xml", true, "some/document", "UTF-8", service);
		writer.writeContentAndClose(null);
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastFailedTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_invalidFormat() {
		MyXmlFileWriter writer = new MyXmlFileWriter("testCreateDocument_nonExisting.xml", true, "invalid_format.xml", "UTF-8", service);
		writer.writeContentAndClose(null);
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastFailedTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_success_emptyVars() {
		MyXmlFileWriter writer = new MyXmlFileWriter("testCreateDocument_nonExisting.xml", true, "valid_novars.xml", "UTF-8", service);
		writer.writeContentAndClose(null);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_success_withVars() throws IOException {
		MyXmlData xmlData = new MyXmlData();
		xmlData.setSomeText("Plain text.");
		xmlData.setSomeElem("<elem comment=\"Some element.\" />.");
		xmlData.setSomeList(Arrays.asList(new String[] { "23", "42", "4711" }));

		String fileName = "xfwt-testCreateDocument_nonExisting.xml";
		MyXmlFileWriter writer = new MyXmlFileWriter(fileName, true, "valid_vars.xml", "UTF-8", service);
		writer.writeContentAndClose(xmlData);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

		// test that variables have been written into document
		
		String filePath = fileOfName(fileName).getAbsolutePath();
		Document doc = XMLUtil.parse(filePath);
		assertEquals("4711", service.perform().queryXml("doc", "test", doc, "/doc/elem/item[3]", XPathConstants.STRING));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

}
