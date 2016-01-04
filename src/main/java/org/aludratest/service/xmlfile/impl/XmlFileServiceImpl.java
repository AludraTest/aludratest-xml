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

import org.aludratest.config.Preferences;
import org.aludratest.content.xml.XmlContent;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.Implementation;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.FileService;
import org.aludratest.service.xmlfile.XmlFileCondition;
import org.aludratest.service.xmlfile.XmlFileInteraction;
import org.aludratest.service.xmlfile.XmlFileService;
import org.aludratest.service.xmlfile.XmlFileVerification;

@Implementation({ XmlFileService.class })
public class XmlFileServiceImpl extends AbstractConfigurableAludraService implements XmlFileService {

	private FileService fileService;

	/** The action object that implements all XmlFileService action interfaces */
	private XmlFileAction action;

	/** A copy of the encoding property from configuration. Used for XML writes */
	private String encoding = "UTF-8";

	@Override
	public String getPropertiesBaseName() {
		return "xmlService";
	}

	@Override
	public void configure(Preferences preferences) {
		encoding = preferences.getStringValue("encoding", "UTF-8");
	}

	@Override
	public void initService() {
		this.fileService = aludraServiceContext.getNonLoggingService(ComponentId.create(FileService.class, getInstanceName()));
		XmlContent contentHandler = aludraServiceContext.newComponentInstance(XmlContent.class);
		this.action = new XmlFileAction(contentHandler, fileService, encoding);
	}

	@Override
	public String getDescription() {
		return XmlFileServiceImpl.class.getSimpleName();
	}

	@Override
	public void setSystemConnector(SystemConnector connector) {
		// not supported / required for XmlFileService
	}

	@Override
	public void close() {
		fileService.close();
	}

	@Override
	public XmlFileInteraction perform() {
		return action;
	}

	@Override
	public XmlFileVerification verify() {
		return action;
	}

	@Override
	public XmlFileCondition check() {
		return action;
	}

	@Override
	public FileService getFileService() {
		return fileService;
	}


}
