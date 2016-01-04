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

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.file.FileService;

/**
 * Interface for a service for processing generic XML documents.
 * 
 * @author falbrech
 */
@ServiceInterface(name = "XML File Service", description = "Offers XML related access and verifaction methods.")
@ConfigProperties({ @ConfigProperty(name = "encoding", description = "The encoding to use for XML file creation. This MUST be equal to the encoding used for the file service configuration.", defaultValue = "UTF-8", required = true, type = String.class) })
public interface XmlFileService extends AludraService {

	/** Provides an object to parse and save XML documents from and to streams. */
	@Override
	XmlFileInteraction perform();

	/** Provides an object to verify XML documents. */
	@Override
	XmlFileVerification verify();

	/**
	 * Provides an object for performing queries on XML documents and analyze their differences.
	 */
	@Override
	XmlFileCondition check();

	/**
	 * Provides the internally used {@link FileService} instance
	 * 
	 * @return the internally used {@link FileService} instance
	 */
	FileService getFileService();

}
