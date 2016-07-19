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

import java.io.File;

import org.aludratest.testcase.TestStatus;
import org.aludratest.util.data.StringData;
import org.databene.commons.FileUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link XmlFileVerifier}.
 * @author Volker Bergmann
 */

public class XmlFileVerifierTest extends AbstractXmlFileServiceTest {

	@Before
	public void copyFiles() throws Exception {
		FileUtil.copy(new File("target/test-classes/compare1.xml"), fileOfName("compare1.xml"), true);
		FileUtil.copy(new File("target/test-classes/compare3.xml"), fileOfName("compare3.xml"), true);
	}
	
	@Test
	public void testAssertDocumentsEqual_success() throws Exception {
		MyTolerantXmlFileVerifier verifier = new MyTolerantXmlFileVerifier("compare3.xml", service);
		verifier.verifyWith(new StringData("compare1.xml"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertDocumentsEqual_fail() {
		MyStrictXmlFileVerifier verifier = new MyStrictXmlFileVerifier("compare3.xml", service);
		verifier.verifyWith(new StringData("compare1.xml"));
		assertEquals(TestStatus.FAILED, getLastFailedTestStep().getTestStatus());
	}

}
