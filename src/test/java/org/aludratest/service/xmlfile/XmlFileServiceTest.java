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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.aludratest.content.xml.AggregateXmlDiff;
import org.aludratest.content.xml.XmlComparisonSettings;
import org.aludratest.content.xml.XmlDiffDetail;
import org.aludratest.content.xml.util.DatabeneXmlComparisonSettings;
import org.aludratest.content.xml.util.DatabeneXmlDiffDetail;
import org.aludratest.service.ComponentId;
import org.aludratest.service.xmlfile.impl.XmlFileServiceImpl;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.StartsWithValidator;
import org.databene.commons.IOUtil;
import org.databene.formats.compare.DiffDetailType;
import org.junit.Test;
import org.w3c.dom.Document;

public class XmlFileServiceTest extends AbstractXmlFileServiceTest {

	@Test
	public void testDefaultImplementation() {
		XmlFileService svc = context.getNonLoggingService(ComponentId.create(XmlFileService.class, "impl-test"));
		assertEquals(XmlFileServiceImpl.class.getName(), svc.getClass().getName());
	}

	@Test
	public void testCreateDocument_nonExisting() {
		service.perform().createDocument("doc", "test", "some/document", "UTF-8", null);
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_invalidFormat() {
		service.perform().createDocument("doc", "test", "invalid_format.xml", "UTF-8", null);
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_success_emptyVars() {
		service.perform().createDocument("doc", "test", "valid_novars.xml", "UTF-8", null);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testCreateDocument_success_withVars() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("someText", "Plain text.");
		vars.put("someElem", "<elem comment=\"Some element.\" />.");
		vars.put("someList", Arrays.asList(new String[] { "23", "42", "4711" }));
		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("data", vars);

		Document doc = service.perform().createDocument("doc", "test", "valid_vars.xml", "UTF-8", context);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		assertNotNull(doc);

		// test that variables have been written into document
		assertEquals("4711", service.perform().queryXml("doc", "test", doc, "/doc/elem/item[3]", XPathConstants.STRING));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testWriteDocument_success() throws Exception {
		Document doc = service.perform().createDocument("doc", "test", "valid_novars.xml", "UTF-8", null);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		service.perform().writeXml("doc", "test", doc, "test_out.xml", true);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

		// check that written document is in UTF-16LE, and contains matching XML declaration
		FileInputStream fis = new FileInputStream(fileOfName("test_out.xml"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtil.transfer(fis, baos);
		fis.close();
		
		byte[] data = baos.toByteArray();
		String xml = new String(data, "UTF-16");

		// would be FALSE if other encoding
		// -2 subtracts the leading BOM
		assertTrue(xml.length() * 2 == (data.length - 2));
		assertTrue(xml.contains("encoding=\"UTF-16\""));
	}

	/**
	 * Asserts that the "overwrite protection" mechanism works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWriteDocument_fail() throws Exception {
		Document doc = service.perform().createDocument("doc", "test", "valid_novars.xml", "UTF-8", null);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

		fileOfName("test_ovr.xml").createNewFile();
		service.perform().writeXml("doc", "test", doc, "test_ovr.xml", false);
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testDiff_success() {
		Document doc1 = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		Document doc2 = service.perform().createDocument("doc", "doc2", "compare2.xml", "UTF-8", null);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

		XmlComparisonSettings settings = new DatabeneXmlComparisonSettings();
		settings.setWhitespaceRelevant(false);
		settings.tolerateAnyDiffAt("/doc/@timestamp");

		AggregateXmlDiff actualDiff = service.perform().diff("doc", "docs", doc1, doc2, settings);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

		// these are all diffs we expect. Collect them for quick check (order does not matter)
		// @formatter:off
		DatabeneXmlDiffDetail[] expectedDiffs = new DatabeneXmlDiffDetail[] { 
				new DatabeneXmlDiffDetail("bold", "none", "attribute value", DiffDetailType.DIFFERENT, "/doc/sect1/header/@style", "/doc/sect1/header/@style"),
				new DatabeneXmlDiffDetail("Some header", "Some Header", "element text", DiffDetailType.DIFFERENT, "/doc/sect1/header/text()", "/doc/sect1/header/text()"),
				new DatabeneXmlDiffDetail("This is some text here.", "This is some here.", "element text", DiffDetailType.DIFFERENT, "/doc/sect1/body/p[1]/text()", "/doc/sect1/body/p/text()"),
				new DatabeneXmlDiffDetail("<p>This is more text.</p>", null, "element", DiffDetailType.MISSING, "/doc/sect1/body/p[2]", null),
				new DatabeneXmlDiffDetail(null, "<body>" + LF + "\t\t\t<p>A body which does not exist in other document.</p>" + LF + "\t\t</body>", "element", DiffDetailType.UNEXPECTED, null, "/doc/sect2/body")
				};
		// @formatter:on

		for (DatabeneXmlDiffDetail expectedDetail : expectedDiffs) {
			// a simple "contains" would not work because expected / actual objects are Nodes
			boolean found = false;
			for (XmlDiffDetail actualDetail : actualDiff.getXmlDetails()) {
				if (equals(actualDetail, expectedDetail)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				System.out.println("Expected detail:");
				System.out.println("\t" + renderDetails(expectedDetail));
				System.out.println();
				System.out.println("Actual details:");
				for (XmlDiffDetail d : actualDiff.getXmlDetails()) {
					System.out.println("\t" + renderDetails(d));
				}
				System.out.println();
				fail("Expected diff detail not detected by XML service: " + expectedDetail);
			}
		}
	}
	
	@Test
	public void testNodeExists() {
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		assertFalse(service.check().nodeExists("doc", "test", doc, "/some/xpath"));
		// matches more than one node
		assertTrue(service.check().nodeExists("doc", "test", doc, "/doc/sect1/body/p"));
		// matches a string
		assertTrue(service.check().nodeExists("doc", "test", doc, "/doc/sect2/header/text()"));
		// matches an attribute
		assertTrue(service.check().nodeExists("doc", "test", doc, "/doc/@timestamp"));
	}

	@Test
	public void testNodeMatches() {
		StartsWithValidator validator = new StartsWithValidator("Some header");
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		// this matches TWO elements, but should verify only the first
		assertTrue(service.check().nodeMatches("doc", "test", doc, "//header", validator));
	}

	@Test
	public void testAssertNodeExists_fail() {
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		service.verify().assertNodeExists("doc", "test", doc, "/some/xpath");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertNodeExists_success() {
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		service.verify().assertNodeExists("doc", "test", doc, "/doc/sect1/body/p");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertNodeMatches_fail() {
		StartsWithValidator validator = new StartsWithValidator("Some thing");
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		service.verify().assertNodeMatches("doc", "test", doc, "//header", validator);
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertNodeMatches_success() {
		StartsWithValidator validator = new StartsWithValidator("Some header");
		Document doc = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		service.verify().assertNodeMatches("doc", "test", doc, "//header", validator);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertDocumentsEqual_success() {
		Document doc1 = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		Document doc2 = service.perform().createDocument("doc", "doc2", "compare3.xml", "UTF-8", null);

		XmlComparisonSettings settings = new DatabeneXmlComparisonSettings();
		settings.tolerateAnyDiffAt("//body");
		settings.tolerateAnyDiffAt("/doc/sect1/header/@style");
		settings.tolerateDifferentAt("/doc/@timestamp");

		service.verify().assertDocumentsEqual("doc", "docs", doc1, doc2, settings);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertDocumentsEqual_fail() {
		// same documents, but we "forget" to tolerate the bodies
		Document doc1 = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		Document doc2 = service.perform().createDocument("doc", "doc2", "compare3.xml", "UTF-8", null);

		XmlComparisonSettings settings = new DatabeneXmlComparisonSettings();
		settings.tolerateDifferentAt("/doc/@timestamp");
		settings.tolerateAnyDiffAt("/doc/sect1/header/@style");

		service.verify().assertDocumentsEqual("doc", "docs", doc1, doc2, settings);
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAreDocumentsEqual() {
		Document doc1 = service.perform().createDocument("doc", "doc1", "compare1.xml", "UTF-8", null);
		Document doc2 = service.perform().createDocument("doc", "doc2", "compare3.xml", "UTF-8", null);

		XmlComparisonSettings settings = new DatabeneXmlComparisonSettings();
		settings.tolerateDifferentAt("/doc/@timestamp");
		settings.tolerateAnyDiffAt("/doc/sect1/header/@style");

		assertFalse(service.check().areDocumentsEqual("doc", "docs", doc1, doc2, settings));

		settings.tolerateAnyDiffAt("//body");

		assertTrue(service.check().areDocumentsEqual("doc", "docs", doc1, doc2, settings));
	}

}
