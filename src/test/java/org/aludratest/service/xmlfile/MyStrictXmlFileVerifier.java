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
import org.aludratest.content.xml.util.DatabeneXmlComparisonSettings;

/**
 * Strict implementation of the {@link XmlFileVerifier} class for testing.
 * @author Volker Bergmann
 */

public class MyStrictXmlFileVerifier extends XmlFileVerifier<MyStrictXmlFileVerifier>{

	public MyStrictXmlFileVerifier(String filePath, XmlFileService service) {
		super(filePath, service, createSettings(service));
	}

	private static XmlComparisonSettings createSettings(XmlFileService service) {
		XmlComparisonSettings settings = new DatabeneXmlComparisonSettings();
		settings.setWhitespaceRelevant(false);
		settings.tolerateAnyDiffAt("/doc/@timestamp");
		return settings;
	}

}
