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

import java.io.File;

import org.aludratest.content.xml.XmlDiffDetail;
import org.aludratest.testing.service.AbstractAludraServiceTest;
import org.databene.commons.ConversionException;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.xml.XMLUtil;
import org.junit.After;
import org.junit.Before;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Parent class for tests that use the {@link XmlFileService}.
 * @author Volker Bergmann
 */

public abstract class AbstractXmlFileServiceTest extends AbstractAludraServiceTest {

	protected static final String LF = SystemInfo.getLineSeparator();

	protected XmlFileService service;
	private File filedir;

	@Before
	public void setUp() throws Exception {
		File basedir = new File("").getAbsoluteFile();
		System.setProperty("xml.test.base.dir", basedir.getAbsolutePath());
		filedir = new File(basedir, "target/xmlfile-test");
		filedir.mkdirs();
		service = getLoggingService(XmlFileService.class, "unittest");
	}

	@After
	public void tearDown() throws Exception {
		if (this.service != null) {
			this.service.close();
		}
	}
	
	protected File fileOfName(String fileName) {
		return new File(filedir, fileName);
	}

	protected static boolean equals(XmlDiffDetail diff1, XmlDiffDetail diff2) {
		if (diff1.getXmlDiffType() != diff2.getXmlDiffType()) {
			return false;
		}
		
		// check string properties
		if (!NullSafeComparator.equals(diff1.getLocatorOfActual(), diff2.getLocatorOfActual())
				|| !NullSafeComparator.equals(diff1.getLocatorOfExpected(), diff2.getLocatorOfExpected())
				|| !NullSafeComparator.equals(diff1.getObjectClassifier(), diff2.getObjectClassifier())) {
			return false;
		}
		
		if (!compareNodeSafe(diff1.getActual(), diff2.getActual())
				|| !compareNodeSafe(diff1.getExpected(), diff2.getExpected())) {
			return false;
		}
		
		return true;
	}
	
	protected static boolean compareNodeSafe(Object o1, Object o2) {
		if (o1 == null && o2 != null) {
			return false;
		}
		if (o1 != null) {
			if ((o1 instanceof String) && !o1.equals(o2)) {
				return false;
			}
			
			if (o1 instanceof Node) {
				String s1 = convert(o1);
				if (!s1.equals(o2)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
    public static String renderDetails(XmlDiffDetail detail) {
        return "expected=" + convert(detail.getExpected()) + ", " 
        		+ "actual=" + convert(detail.getActual()) + ", "
                + "objectClassifier=" + detail.getObjectClassifier() + ", " 
        		+ "type=" + detail.getXmlDiffType() + ", " 
                + "locatorOfExpected=" + detail.getLocatorOfExpected() + ", " 
        		+ "locatorOfActual=" + detail.getLocatorOfActual();
    }

	protected static String convert(Object node) throws ConversionException {
		if (node instanceof CDATASection)
			return "<![CDATA[" + ((CDATASection) node).getTextContent() + "]]>";
		else if (node instanceof Text) 
			return ((Text) node).getTextContent();
		else if (node instanceof Element)
			return XMLUtil.format((Element) node).trim();
		else if (node instanceof Document)
			return XMLUtil.format((Document) node);
		else if (node instanceof String)
			return node.toString();
		else
			return ToStringConverter.convert(node, "");
	}
	
}
